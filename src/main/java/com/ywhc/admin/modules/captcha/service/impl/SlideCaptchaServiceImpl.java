package com.ywhc.admin.modules.captcha.service.impl;

import com.ywhc.admin.modules.captcha.dto.SlideCaptchaGenerateResponse;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaVerifyRequest;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaVerifyResponse;
import com.ywhc.admin.modules.captcha.entity.SlideCaptcha;
import com.ywhc.admin.modules.captcha.service.SlideCaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 滑块验证码服务实现类
 *
 * @author YWHC Team
 * @since 2024-09-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SlideCaptchaServiceImpl implements SlideCaptchaService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 验证码配置常量
    private static final int BACKGROUND_WIDTH = 300;
    private static final int BACKGROUND_HEIGHT = 150;
    private static final int PUZZLE_WIDTH = 60;
    private static final int PUZZLE_HEIGHT = 60;
    private static final int EXPIRE_MINUTES = 5; // 验证码过期时间（分钟）
    private static final int TOKEN_EXPIRE_MINUTES = 10; // token过期时间（分钟）
    private static final String CAPTCHA_KEY_PREFIX = "slide_captcha:";
    private static final String TOKEN_KEY_PREFIX = "captcha_token:";

    // 验证容差 - 重新调整以确保视觉对齐与数学验证匹配
    private static final int POSITION_TOLERANCE = 15; // 位置容差，确保真实对齐
    private static final int OVERSHOOT_THRESHOLD = 25; // 过度拖拽阈值
    private static final double MIN_TRACK_SMOOTHNESS = 0.5; // 轨迹平滑度最小值

    @Override
    public SlideCaptchaGenerateResponse generateCaptcha() {
        try {
            // 生成验证码ID
            String captchaId = UUID.randomUUID().toString().replace("-", "");

            // 生成随机拼图位置 - 确保有足够的滑动距离
            Random random = new Random();
            int puzzleX = random.nextInt(BACKGROUND_WIDTH - PUZZLE_WIDTH - 80) + 80; // 最小距离80px，确保有足够滑动空间
            int puzzleY = random.nextInt(BACKGROUND_HEIGHT - PUZZLE_HEIGHT - 20) + 10;

            // 生成背景图片
            BufferedImage backgroundImage = generateBackgroundImage();

            // 生成拼图块
            BufferedImage puzzleImage = generatePuzzleImage(backgroundImage, puzzleX, puzzleY);

            // 在背景图上挖空拼图区域
            cutPuzzleFromBackground(backgroundImage, puzzleX, puzzleY);

            // 转换为Base64
            String backgroundBase64 = imageToBase64(backgroundImage);
            String puzzleBase64 = imageToBase64(puzzleImage);

            // 创建验证码对象
            SlideCaptcha captcha = new SlideCaptcha();
            captcha.setCaptchaId(captchaId);
            captcha.setPuzzleX(puzzleX);
            captcha.setPuzzleY(puzzleY);
            captcha.setPuzzleWidth(PUZZLE_WIDTH);
            captcha.setPuzzleHeight(PUZZLE_HEIGHT);
            captcha.setCreateTime(LocalDateTime.now());
            captcha.setExpireTime(LocalDateTime.now().plusMinutes(EXPIRE_MINUTES));
            captcha.setVerified(false);

            // 存储到Redis
            String cacheKey = CAPTCHA_KEY_PREFIX + captchaId;
            redisTemplate.opsForValue().set(cacheKey, captcha, EXPIRE_MINUTES, TimeUnit.MINUTES);

            // 返回响应
            SlideCaptchaGenerateResponse response = SlideCaptchaGenerateResponse.builder()
                    .captchaId(captchaId)
                    .backgroundImage(backgroundBase64)
                    .puzzleImage(puzzleBase64)
                    .puzzleX(puzzleX)
                    .puzzleY(puzzleY)
                    .build();

            log.info("生成验证码成功 - ID: {}, 拼图位置: ({}, {})", captchaId, puzzleX, puzzleY);
            return response;

        } catch (Exception e) {
            log.error("生成滑块验证码失败", e);
            throw new RuntimeException("生成验证码失败");
        }
    }

    @Override
    public SlideCaptchaVerifyResponse verifyCaptcha(SlideCaptchaVerifyRequest request) {
        try {
            // 从Redis获取验证码信息
            String cacheKey = CAPTCHA_KEY_PREFIX + request.getCaptchaId();
            SlideCaptcha captcha = (SlideCaptcha) redisTemplate.opsForValue().get(cacheKey);

            if (captcha == null) {
                return SlideCaptchaVerifyResponse.builder()
                        .success(false)
                        .message("验证码已过期或不存在")
                        .build();
            }

            if (captcha.getVerified()) {
                return SlideCaptchaVerifyResponse.builder()
                        .success(false)
                        .message("验证码已被使用")
                        .build();
            }

            // 验证滑块位置 - 前端发送的是滑块移动距离，需要加上原始位置
            // 但是由于拼图在原始位置，滑块移动距离应该等于拼图的X坐标
            boolean positionValid = verifyPosition(captcha.getPuzzleX(), request.getSlideX());
            log.info("位置验证结果: {}", positionValid);

            // 验证拖拽轨迹
            boolean trackValid = verifyTrack(request.getTrack());
            log.info("轨迹验证结果: {}", trackValid);

            if (positionValid && trackValid) {
                log.info("验证成功，生成token");
                // 验证成功，生成token
                String token = UUID.randomUUID().toString().replace("-", "");

                // 标记验证码已验证
                captcha.setVerified(true);
                captcha.setToken(token);
                redisTemplate.opsForValue().set(cacheKey, captcha, EXPIRE_MINUTES, TimeUnit.MINUTES);

                // 存储token
                String tokenKey = TOKEN_KEY_PREFIX + token;
                redisTemplate.opsForValue().set(tokenKey, captcha.getCaptchaId(), TOKEN_EXPIRE_MINUTES,
                        TimeUnit.MINUTES);

                SlideCaptchaVerifyResponse response = SlideCaptchaVerifyResponse.builder()
                        .success(true)
                        .token(token)
                        .build();
                log.info("验证成功，返回响应: {}", response);
                return response;
            } else {
                log.info("验证失败 - 位置验证: {}, 轨迹验证: {}", positionValid, trackValid);
                SlideCaptchaVerifyResponse response = SlideCaptchaVerifyResponse.builder()
                        .success(false)
                        .message("验证失败，请重试")
                        .build();
                log.info("验证失败，返回响应: {}", response);
                return response;
            }

        } catch (Exception e) {
            log.error("验证滑块验证码失败", e);
            return SlideCaptchaVerifyResponse.builder()
                    .success(false)
                    .message("验证失败")
                    .build();
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String tokenKey = TOKEN_KEY_PREFIX + token;
        return redisTemplate.hasKey(tokenKey);
    }

    /**
     * 生成背景图片
     */
    private BufferedImage generateBackgroundImage() {
        BufferedImage image = new BufferedImage(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 生成渐变背景
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(135, 206, 250),
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT, new Color(70, 130, 180));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        // 添加一些装饰性图形
        Random random = new Random();
        g2d.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(BACKGROUND_WIDTH);
            int y = random.nextInt(BACKGROUND_HEIGHT);
            int size = random.nextInt(20) + 10;
            g2d.fillOval(x, y, size, size);
        }

        g2d.dispose();
        return image;
    }

    /**
     * 生成拼图块
     */
    private BufferedImage generatePuzzleImage(BufferedImage backgroundImage, int x, int y) {
        BufferedImage puzzleImage = new BufferedImage(PUZZLE_WIDTH, PUZZLE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = puzzleImage.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 直接复制背景图片的对应区域，不使用复杂形状
        g2d.drawImage(backgroundImage, 0, 0, PUZZLE_WIDTH, PUZZLE_HEIGHT,
                x, y, x + PUZZLE_WIDTH, y + PUZZLE_HEIGHT, null);

        // 添加简单的边框
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(0, 0, PUZZLE_WIDTH - 1, PUZZLE_HEIGHT - 1);

        g2d.dispose();
        return puzzleImage;
    }

    /**
     * 在背景图上挖空拼图区域
     */
    private void cutPuzzleFromBackground(BufferedImage backgroundImage, int x, int y) {
        Graphics2D g2d = backgroundImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 直接填充矩形区域为半透明灰色，模拟缺口效果
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(x, y, PUZZLE_WIDTH, PUZZLE_HEIGHT);

        // 添加边框
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(x, y, PUZZLE_WIDTH - 1, PUZZLE_HEIGHT - 1);

        g2d.dispose();
    }

    /**
     * 创建拼图形状 - 简化形状，提高匹配精度
     */
    private Shape createPuzzleShape() {
        // 创建简单的圆角矩形，避免复杂形状导致的匹配问题
        return new RoundRectangle2D.Double(0, 0, PUZZLE_WIDTH, PUZZLE_HEIGHT, 8, 8);
    }

    /**
     * 验证滑块位置 - 增强版本，加强坐标验证
     */
    private boolean verifyPosition(int correctX, int slideX) {
        int diff = Math.abs(correctX - slideX);
        
        // 详细的坐标验证日志
        log.info("位置验证详情:");
        log.info("  - 拼图目标位置(puzzleX): {}px", correctX);
        log.info("  - 滑块实际位置(slideX): {}px", slideX);
        log.info("  - 位置差值: {}px", diff);
        log.info("  - 验证容差: {}px", POSITION_TOLERANCE);
        log.info("  - 拼图尺寸: {}x{} px", PUZZLE_WIDTH, PUZZLE_HEIGHT);
        
        boolean positionValid = diff <= POSITION_TOLERANCE;
        
        if (positionValid) {
            log.info("✅ 位置验证通过 - 差值{}px在容差{}px范围内", diff, POSITION_TOLERANCE);
        } else {
            log.warn("❌ 位置验证失败 - 差值{}px超过容差{}px", diff, POSITION_TOLERANCE);
        }
        
        // 坐标合理性检查
        if (correctX < 0 || correctX > BACKGROUND_WIDTH) {
            log.warn("⚠️  异常：目标位置{}超出背景宽度{}", correctX, BACKGROUND_WIDTH);
        }
        if (slideX < 0 || slideX > BACKGROUND_WIDTH) {
            log.warn("⚠️  异常：滑动位置{}超出背景宽度{}", slideX, BACKGROUND_WIDTH);
        }
        
        return positionValid;
    }

    /**
     * 验证拖拽轨迹 - 增强版本
     */
    private boolean verifyTrack(List<SlideCaptchaVerifyRequest.TrackPoint> track) {
        if (track == null || track.size() < 3) {
            log.warn("轨迹验证失败 - 轨迹点数量不足: {}", track != null ? track.size() : 0);
            return false;
        }

        SlideCaptchaVerifyRequest.TrackPoint start = track.get(0);
        SlideCaptchaVerifyRequest.TrackPoint end = track.get(track.size() - 1);

        // 检查拖拽时间（更严格的时间范围）
        long duration = end.getTime() - start.getTime();
        log.info("轨迹验证 - 拖拽时长: {}ms", duration);

        if (duration < 300 || duration > 30000) { // 300ms到30s之间，更合理的时间限制
            log.warn("轨迹验证失败 - 拖拽时间不合理: {}ms，有效范围300ms-30s", duration);
            return false;
        }

        // 检查轨迹距离是否合理
        double totalDistance = end.getX() - start.getX();
        if (totalDistance < 20) { // 提高最小移动距离要求
            log.warn("轨迹验证失败 - 移动距离过小: {}", totalDistance);
            return false;
        }

        // 过度拖拽检测
        boolean overshootValid = verifyOvershoot(track, totalDistance);
        if (!overshootValid) {
            log.warn("轨迹验证失败 - 检测到过度拖拽行为");
            return false;
        }

        // 轨迹平滑度验证
        double smoothness = calculateTrackSmoothness(track);
        if (smoothness < MIN_TRACK_SMOOTHNESS) {
            log.warn("轨迹验证失败 - 轨迹不够平滑: {}", smoothness);
            return false;
        }

        // 速度一致性检查
        boolean velocityValid = verifyVelocityConsistency(track);
        if (!velocityValid) {
            log.warn("轨迹验证失败 - 速度变化异常");
            return false;
        }

        log.info("轨迹验证通过 - 轨迹点数: {}, 时长: {}ms, 距离: {}, 平滑度: {}",
                track.size(), duration, totalDistance, smoothness);
        return true;
    }

    /**
     * 验证过度拖拽行为
     */
    private boolean verifyOvershoot(List<SlideCaptchaVerifyRequest.TrackPoint> track, double totalDistance) {
        if (track.size() < 5) return true;

        // 找到最大X位置和最终位置
        int maxX = track.stream().mapToInt(SlideCaptchaVerifyRequest.TrackPoint::getX).max().orElse(0);
        int finalX = track.get(track.size() - 1).getX();
        
        // 计算过度拖拽距离
        int overshoot = maxX - finalX;
        
        log.info("过度拖拽分析 - 最大位置: {}, 最终位置: {}, 过度距离: {}, 阈值: {}",
                maxX, finalX, overshoot, OVERSHOOT_THRESHOLD);

        // 如果过度拖拽超过阈值，则验证失败
        if (overshoot > OVERSHOOT_THRESHOLD) {
            log.warn("检测到过度拖拽 - 过度距离{}超过阈值{}", overshoot, OVERSHOOT_THRESHOLD);
            return false;
        }

        return true;
    }

    /**
     * 计算轨迹平滑度
     */
    private double calculateTrackSmoothness(List<SlideCaptchaVerifyRequest.TrackPoint> track) {
        if (track.size() < 3) return 1.0;

        double totalVariation = 0.0;
        double totalDistance = 0.0;

        for (int i = 1; i < track.size(); i++) {
            SlideCaptchaVerifyRequest.TrackPoint prev = track.get(i - 1);
            SlideCaptchaVerifyRequest.TrackPoint curr = track.get(i);

            // 计算距离
            double distance = Math.sqrt(Math.pow(curr.getX() - prev.getX(), 2) + Math.pow(curr.getY() - prev.getY(), 2));
            totalDistance += distance;

            // 计算方向变化（如果有足够的点）
            if (i > 1) {
                SlideCaptchaVerifyRequest.TrackPoint prev2 = track.get(i - 2);
                
                // 计算两个向量的角度差异
                double angle1 = Math.atan2(prev.getY() - prev2.getY(), prev.getX() - prev2.getX());
                double angle2 = Math.atan2(curr.getY() - prev.getY(), curr.getX() - prev.getX());
                double angleDiff = Math.abs(angle2 - angle1);
                
                // 标准化角度差异到0-π范围
                if (angleDiff > Math.PI) {
                    angleDiff = 2 * Math.PI - angleDiff;
                }
                
                totalVariation += angleDiff;
            }
        }

        // 计算平滑度分数（角度变化越小，平滑度越高）
        double smoothness = Math.max(0.0, 1.0 - (totalVariation / (Math.PI * (track.size() - 2))));
        
        log.debug("轨迹平滑度计算 - 总变化: {}, 总距离: {}, 平滑度: {}", totalVariation, totalDistance, smoothness);
        
        return smoothness;
    }

    /**
     * 验证速度一致性
     */
    private boolean verifyVelocityConsistency(List<SlideCaptchaVerifyRequest.TrackPoint> track) {
        if (track.size() < 5) return true;

        List<Double> velocities = new ArrayList<>();
        
        for (int i = 1; i < track.size(); i++) {
            SlideCaptchaVerifyRequest.TrackPoint prev = track.get(i - 1);
            SlideCaptchaVerifyRequest.TrackPoint curr = track.get(i);
            
            double distance = Math.abs(curr.getX() - prev.getX());
            long timeDiff = curr.getTime() - prev.getTime();
            
            if (timeDiff > 0) {
                double velocity = distance / timeDiff; // 像素/毫秒
                velocities.add(velocity);
            }
        }

        if (velocities.isEmpty()) return false;

        // 计算速度的标准差
        double avgVelocity = velocities.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = velocities.stream()
                .mapToDouble(v -> Math.pow(v - avgVelocity, 2))
                .average().orElse(0.0);
        double stdDev = Math.sqrt(variance);

        // 如果标准差相对于平均速度过大，说明速度变化异常
        double coefficient = avgVelocity > 0 ? stdDev / avgVelocity : 1.0;
        boolean isConsistent = coefficient < 2.0; // 变异系数小于2

        log.debug("速度一致性分析 - 平均速度: {}, 标准差: {}, 变异系数: {}, 一致性: {}",
                avgVelocity, stdDev, coefficient, isConsistent);

        return isConsistent;
    }

    /**
     * 图片转Base64
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
