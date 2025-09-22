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

    // 验证容差
    private static final int TOLERANCE = 30; // 像素容差，考虑人工操作的实际精度

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
     * 验证滑块位置
     */
    private boolean verifyPosition(int correctX, int slideX) {
        int diff = Math.abs(correctX - slideX);
        log.info("验证位置 - 正确位置: {}, 滑动位置: {}, 差值: {}, 容差: {}", correctX, slideX, diff, TOLERANCE);
        return diff <= TOLERANCE;
    }

    /**
     * 验证拖拽轨迹
     */
    private boolean verifyTrack(List<SlideCaptchaVerifyRequest.TrackPoint> track) {
        if (track == null || track.size() < 2) {
            log.warn("轨迹验证失败 - 轨迹点数量不足: {}", track != null ? track.size() : 0);
            return false;
        }

        // 简单的轨迹验证：检查是否有合理的拖拽时间和轨迹
        SlideCaptchaVerifyRequest.TrackPoint start = track.get(0);
        SlideCaptchaVerifyRequest.TrackPoint end = track.get(track.size() - 1);

        // 检查拖拽时间（应该在合理范围内）
        long duration = end.getTime() - start.getTime();
        log.info("轨迹验证 - 拖拽时长: {}ms", duration);

        if (duration < 200 || duration > 20000) { // 200ms到20s之间，允许用户调整
            log.warn("轨迹验证失败 - 拖拽时间不合理: {}ms", duration);
            return false;
        }

        // 检查轨迹距离是否合理
        double totalDistance = end.getX() - start.getX();
        if (totalDistance < 10) { // 至少要有10像素的移动距离
            log.warn("轨迹验证失败 - 移动距离过小: {}", totalDistance);
            return false;
        }

        // 检查是否有调整行为（用户可能会拖过头然后调整）
        boolean hasAdjustment = checkForAdjustmentBehavior(track);
        log.info("轨迹分析 - 是否有调整行为: {}", hasAdjustment);

        log.info("轨迹验证通过 - 轨迹点数: {}, 时长: {}ms, 距离: {}", track.size(), duration, totalDistance);
        return true;
    }

    /**
     * 检查轨迹中是否有调整行为（拖过头后回调）
     */
    private boolean checkForAdjustmentBehavior(List<SlideCaptchaVerifyRequest.TrackPoint> track) {
        if (track.size() < 10)
            return false;

        // 找到最大X位置
        int maxX = track.stream().mapToInt(SlideCaptchaVerifyRequest.TrackPoint::getX).max().orElse(0);
        int finalX = track.get(track.size() - 1).getX();

        // 如果最终位置比最大位置小超过5像素，说明有回调行为
        boolean hasAdjustment = maxX - finalX > 5;

        if (hasAdjustment) {
            log.info("检测到调整行为 - 最大位置: {}, 最终位置: {}, 回调距离: {}", maxX, finalX, maxX - finalX);
        }

        return hasAdjustment;
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
