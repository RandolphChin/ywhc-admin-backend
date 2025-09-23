package com.ywhc.admin.common.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * RSA密钥管理服务
 * 用于登录密码加密传输
 * 支持集群环境，密钥对存储在Redis中
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RSAKeyService {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int KEY_SIZE = 2048;

    // Redis键名
    private static final String REDIS_PUBLIC_KEY = "rsa:public_key";
    private static final String REDIS_PRIVATE_KEY = "rsa:private_key";
    private static final String REDIS_KEY_LOCK = "rsa:key_lock";

    // 密钥过期时间（7天）
    private static final long KEY_EXPIRE_DAYS = 7;

    private final RedisTemplate<String, String> redisTemplate;

    // 内存缓存，避免频繁访问Redis
    private volatile String cachedPublicKey;
    private volatile String cachedPrivateKey;
    private volatile long lastCacheTime = 0;
    private static final long CACHE_EXPIRE_MS = 5 * 60 * 1000; // 5分钟缓存

    /**
     * 初始化RSA密钥对
     * 优先从Redis获取，如果不存在则生成新的密钥对
     */
    public void initKeyPair() {
        try {
            // 尝试从Redis获取现有密钥对
            String publicKey = redisTemplate.opsForValue().get(REDIS_PUBLIC_KEY);
            String privateKey = redisTemplate.opsForValue().get(REDIS_PRIVATE_KEY);

            if (publicKey != null && privateKey != null) {
                // 使用现有密钥对
                this.cachedPublicKey = publicKey;
                this.cachedPrivateKey = privateKey;
                this.lastCacheTime = System.currentTimeMillis();
                log.info("从Redis加载RSA密钥对成功");
                return;
            }

            // Redis中没有密钥对，需要生成新的
            generateAndStoreKeyPair();

        } catch (Exception e) {
            log.error("RSA密钥对初始化失败", e);
            throw new RuntimeException("RSA密钥对初始化失败", e);
        }
    }

    /**
     * 生成新的密钥对并存储到Redis
     */
    private void generateAndStoreKeyPair() {
        // 使用分布式锁，防止多个实例同时生成密钥对
        String lockValue = String.valueOf(System.currentTimeMillis());
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                REDIS_KEY_LOCK, lockValue, 30, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(lockAcquired)) {
            try {
                // 再次检查是否已经有其他实例生成了密钥对
                String existingPublicKey = redisTemplate.opsForValue().get(REDIS_PUBLIC_KEY);
                if (existingPublicKey != null) {
                    // 其他实例已经生成了，直接使用
                    initKeyPair();
                    return;
                }

                // 生成新的密钥对
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
                keyPairGenerator.initialize(KEY_SIZE);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                // 转换为Base64字符串
                String publicKeyBase64 = Base64.getEncoder().encodeToString(
                        keyPair.getPublic().getEncoded());
                String privateKeyBase64 = Base64.getEncoder().encodeToString(
                        keyPair.getPrivate().getEncoded());

                // 存储到Redis
                redisTemplate.opsForValue().set(REDIS_PUBLIC_KEY, publicKeyBase64,
                        KEY_EXPIRE_DAYS, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(REDIS_PRIVATE_KEY, privateKeyBase64,
                        KEY_EXPIRE_DAYS, TimeUnit.DAYS);

                // 更新内存缓存
                this.cachedPublicKey = publicKeyBase64;
                this.cachedPrivateKey = privateKeyBase64;
                this.lastCacheTime = System.currentTimeMillis();

                log.info("生成新的RSA密钥对并存储到Redis成功，密钥长度: {} bits", KEY_SIZE);

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } finally {
                // 释放锁
                String currentLockValue = redisTemplate.opsForValue().get(REDIS_KEY_LOCK);
                if (lockValue.equals(currentLockValue)) {
                    redisTemplate.delete(REDIS_KEY_LOCK);
                }
            }
        } else {
            // 获取锁失败，等待一下再重试
            try {
                Thread.sleep(1000);
                initKeyPair();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("密钥对初始化被中断", e);
            }
        }
    }

    /**
     * 获取公钥（Base64编码）
     */
    public String getPublicKeyBase64() {
        ensureKeysLoaded();
        // 双重保险：如果内存缓存存在但Redis中的密钥被删除了，强制刷新
        if (cachedPublicKey != null && !isRedisKeysValid()) {
            log.warn("检测到Redis中的密钥已被删除，强制刷新缓存");
            refreshKeyCache();
        }
        return cachedPublicKey;
    }

    /**
     * 获取私钥（Base64编码）
     */
    private String getPrivateKeyBase64() {
        ensureKeysLoaded();
        // 双重保险：如果内存缓存存在但Redis中的密钥被删除了，强制刷新
        if (cachedPrivateKey != null && !isRedisKeysValid()) {
            log.warn("检测到Redis中的密钥已被删除，强制刷新缓存");
            refreshKeyCache();
        }
        return cachedPrivateKey;
    }

    /**
     * 确保密钥已加载到内存缓存中
     */
    private void ensureKeysLoaded() {
        long currentTime = System.currentTimeMillis();
        boolean needReload = false;

        // 检查是否需要重新加载密钥
        if (cachedPublicKey == null || cachedPrivateKey == null) {
            // 内存缓存为空，需要加载
            needReload = true;
        } else if ((currentTime - lastCacheTime) > CACHE_EXPIRE_MS) {
            // 内存缓存过期，需要验证Redis中的密钥是否还存在
            needReload = true;
        }

        if (needReload) {
            synchronized (this) {
                // 双重检查
                currentTime = System.currentTimeMillis();
                if (cachedPublicKey == null || cachedPrivateKey == null ||
                        (currentTime - lastCacheTime) > CACHE_EXPIRE_MS) {

                    // 从Redis重新加载并验证密钥是否存在
                    String publicKey = redisTemplate.opsForValue().get(REDIS_PUBLIC_KEY);
                    String privateKey = redisTemplate.opsForValue().get(REDIS_PRIVATE_KEY);

                    if (publicKey == null || privateKey == null) {
                        // Redis中没有密钥，重新初始化
                        log.warn("Redis中的RSA密钥对丢失或过期，重新生成");
                        // 清除内存缓存
                        this.cachedPublicKey = null;
                        this.cachedPrivateKey = null;
                        this.lastCacheTime = 0;
                        // 重新初始化
                        initKeyPair();
                    } else {
                        // 更新缓存
                        this.cachedPublicKey = publicKey;
                        this.cachedPrivateKey = privateKey;
                        this.lastCacheTime = currentTime;
                        log.debug("从Redis重新加载RSA密钥对到内存缓存");
                    }
                }
            }
        }
    }

    /**
     * 使用私钥解密数据
     *
     * @param encryptedData 加密的数据（Base64编码）
     * @return 解密后的原始数据
     */
    public String decrypt(String encryptedData) {
        try {
            ensureKeysLoaded();

            if (cachedPrivateKey == null) {
                throw new RuntimeException("RSA私钥未找到");
            }

            // Base64解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] privateKeyBytes = Base64.getDecoder().decode(cachedPrivateKey);

            // 重构私钥对象
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // 解密
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("RSA解密失败: {}", e.getMessage());
            throw new RuntimeException("密码解密失败", e);
        }
    }

    /**
     * 使用公钥加密数据（主要用于测试）
     *
     * @param data 原始数据
     * @return 加密后的数据（Base64编码）
     */
    public String encrypt(String data) {
        try {
            ensureKeysLoaded();

            if (cachedPublicKey == null) {
                throw new RuntimeException("RSA公钥未找到");
            }

            // Base64解码公钥
            byte[] publicKeyBytes = Base64.getDecoder().decode(cachedPublicKey);

            // 重构公钥对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 加密
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("RSA加密失败: {}", e.getMessage());
            throw new RuntimeException("数据加密失败", e);
        }
    }

    /**
     * 验证时间戳是否有效（防重放攻击）
     *
     * @param timestamp     时间戳
     * @param maxAgeSeconds 最大允许的时间差（秒）
     * @return 是否有效
     */
    public boolean isValidTimestamp(long timestamp, long maxAgeSeconds) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = Math.abs(currentTime - timestamp);
        return timeDiff <= maxAgeSeconds * 1000;
    }

    /**
     * 验证Redis中的密钥是否存在且有效
     */
    private boolean isRedisKeysValid() {
        try {
            String publicKey = redisTemplate.opsForValue().get(REDIS_PUBLIC_KEY);
            String privateKey = redisTemplate.opsForValue().get(REDIS_PRIVATE_KEY);
            return publicKey != null && privateKey != null &&
                    !publicKey.trim().isEmpty() && !privateKey.trim().isEmpty();
        } catch (Exception e) {
            log.error("验证Redis密钥时发生异常", e);
            return false;
        }
    }

    /**
     * 强制刷新密钥缓存
     * 会重新从Redis加载密钥，如果Redis中没有则重新生成
     */
    public void refreshKeyCache() {
        synchronized (this) {
            log.info("强制刷新RSA密钥缓存");
            this.cachedPublicKey = null;
            this.cachedPrivateKey = null;
            this.lastCacheTime = 0;
            ensureKeysLoaded();
        }
    }

    /**
     * 重新生成密钥对
     */
    public void regenerateKeyPair() {
        log.info("重新生成RSA密钥对");
        // 清除Redis中的旧密钥
        redisTemplate.delete(REDIS_PUBLIC_KEY);
        redisTemplate.delete(REDIS_PRIVATE_KEY);
        // 清除内存缓存
        this.cachedPublicKey = null;
        this.cachedPrivateKey = null;
        this.lastCacheTime = 0;
        // 生成新密钥对
        generateAndStoreKeyPair();
    }
}
