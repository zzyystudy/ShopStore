package com.zxyy.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * AES-GCM 字符串加密工具。
 *
 * <p>本工具使用 AES-256-GCM，每次加密都会生成一个新的 12 字节随机 nonce，
 * 并把密文（包含 GCM 认证标签）和 nonce 分别进行 Base64 编码，方便存入数据库。</p>
 *
 * <p>注意：密钥不能硬编码在代码中，也不能和密文保存在同一张表中。
 * 应从环境变量、配置中心或密钥管理服务中读取。</p>
 */
@Component
public class AESGCMUtil {

    private final String key;

    public AESGCMUtil(@Value("${shop.aes.key}") String base6Key) {
        this.key = base6Key;
    }

    private final String TRANSFORMATION = "AES/GCM/NoPadding";
    private final String KEY_ALGORITHM = "AES";

    /** AES-256 密钥长度：32 字节。 */
    private final int KEY_LENGTH_BYTES = 32;

    /** GCM 推荐的 nonce 长度：12 字节（96 bit）。 */
    private final int NONCE_LENGTH_BYTES = 12;

    /** GCM 认证标签长度：128 bit。 */
    private final int TAG_LENGTH_BITS = 128;
    private final int TAG_LENGTH_BYTES = TAG_LENGTH_BITS / Byte.SIZE;

    private final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成一把可用于 AES-256-GCM 的 Base64 密钥。
     * 只生成1次数据库只存密钥版本 大公司每年都会更换密钥
     * 密钥存放在环境变量中 不能存储在数据库中
     *
     * <p>通常只在首次部署或密钥轮换时生成一次，生成结果应放入安全配置，
     * 不能在每次加密时重新生成。</p>
     *
     * @return 32 字节随机密钥的 Base64 字符串
     */
    public String generateBase64Key() {
        byte[] keyBytes = new byte[KEY_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(keyBytes);
        try {
            return Base64.getEncoder().encodeToString(keyBytes);
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }

    /**
     * 加密字符串，不使用附加认证数据。
     *
     * @param plaintext 明文，可以是序列化后的 JSON
     * @return Base64 编码后的密文和 nonce
     */
    public EncryptedPayload encrypt(String plaintext) {
        return encrypt(plaintext, key, null);
    }
    public EncryptedPayload encrypt(String plaintext,String aad){
        return encrypt(plaintext, key, aad);
    }

    /**
     * 加密字符串，并使用附加认证数据（AAD）。
     *
     * <p>AAD 不会被加密，但会参与 GCM 完整性认证。对于虚拟库存，建议传入
     * {@code inventoryNo}。解密时必须传入完全相同的 AAD，否则认证失败。</p>
     *
     * @param plaintext      明文，可以是序列化后的 JSON
     * @param base64Key      Base64 编码的 32 字节 AES 密钥
     * @param associatedData 附加认证数据，建议使用稳定且唯一的库存编号
     * @return Base64 编码后的密文和 nonce
     */
    public EncryptedPayload encrypt(
            String plaintext,
            String base64Key,
            String associatedData
    ) {
        Objects.requireNonNull(plaintext, "待加密内容不能为 null");

        byte[] keyBytes = decodeAndValidateKey(base64Key);
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] nonce = new byte[NONCE_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(nonce);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
            updateAssociatedData(cipher, associatedData);

            // doFinal 返回“密文 + 认证标签”，认证标签不需要再单独保存。
            byte[] ciphertextBytes = cipher.doFinal(plaintextBytes);
            try {
                return new EncryptedPayload(
                        Base64.getEncoder().encodeToString(ciphertextBytes),
                        Base64.getEncoder().encodeToString(nonce)
                );
            } finally {
                Arrays.fill(ciphertextBytes, (byte) 0);
            }
        } catch (GeneralSecurityException exception) {
            throw new CryptoException("AES-GCM 加密失败", exception);
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
            Arrays.fill(plaintextBytes, (byte) 0);
            Arrays.fill(nonce, (byte) 0);
        }
    }

    /**
     * 解密字符串，不使用附加认证数据。
     *
     * @param ciphertextBase64 Base64 编码的密文（包含 GCM 认证标签）
     * @param nonceBase64      Base64 编码的 12 字节 nonce
     * @return UTF-8 明文
     */
    public String decrypt(
            String ciphertextBase64,
            String nonceBase64
    ) {
        return decrypt(ciphertextBase64, nonceBase64, key, null);
    }

    public String decrypt(
            String ciphertextBase64,
            String nonceBase64,
            String aad
    ) {
        return decrypt(ciphertextBase64, nonceBase64, key, aad);
    }

    /**
     * 解密字符串，并验证附加认证数据（AAD）。
     *
     * <p>密钥、nonce、密文或 AAD 中任意一项不正确，GCM 认证都会失败，
     * 方法会抛出 {@link CryptoException}，不会返回伪造或损坏的明文。</p>
     *
     * @param ciphertextBase64 Base64 编码的密文（包含 GCM 认证标签）
     * @param nonceBase64      Base64 编码的 12 字节 nonce
     * @param base64Key        Base64 编码的 32 字节 AES 密钥
     * @param associatedData   加密时使用的附加认证数据
     * @return UTF-8 明文
     */
    public String decrypt(
            String ciphertextBase64,
            String nonceBase64,
            String base64Key,
            String associatedData
    ) {
        byte[] keyBytes = decodeAndValidateKey(base64Key);
        byte[] nonce = decodeBase64(nonceBase64, "nonce");
        byte[] ciphertextBytes = decodeBase64(ciphertextBase64, "密文");

        if (nonce.length != NONCE_LENGTH_BYTES) {
            throw new CryptoException("nonce 解码后必须是 12 字节");
        }
        if (ciphertextBytes.length < TAG_LENGTH_BYTES) {
            throw new CryptoException("密文长度不合法，缺少 GCM 认证标签");
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
            updateAssociatedData(cipher, associatedData);

            byte[] plaintextBytes = cipher.doFinal(ciphertextBytes);
            try {
                return new String(plaintextBytes, StandardCharsets.UTF_8);
            } finally {
                Arrays.fill(plaintextBytes, (byte) 0);
            }
        } catch (GeneralSecurityException exception) {
            // 不区分密钥错误、密文被篡改或 AAD 错误，避免向外泄露过多信息。
            throw new CryptoException("AES-GCM 解密或完整性认证失败", exception);
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
            Arrays.fill(nonce, (byte) 0);
            Arrays.fill(ciphertextBytes, (byte) 0);
        }
    }

    private byte[] decodeAndValidateKey(String base64Key) {
        byte[] keyBytes = decodeBase64(base64Key, "AES 密钥");
        if (keyBytes.length != KEY_LENGTH_BYTES) {
            Arrays.fill(keyBytes, (byte) 0);
            throw new CryptoException("AES-256 密钥解码后必须是 32 字节");
        }
        return keyBytes;
    }

    private byte[] decodeBase64(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new CryptoException(fieldName + "不能为空");
        }
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException exception) {
            throw new CryptoException(fieldName + "不是合法的 Base64 字符串", exception);
        }
    }

    private void updateAssociatedData(Cipher cipher, String associatedData) {
        if (associatedData != null && !associatedData.isEmpty()) {
            cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 一次加密需要保存的两项结果。
     *
     * @param ciphertext 对应数据库 {@code content_ciphertext}
     * @param nonce      对应数据库 {@code encryption_nonce}
     */
    public record EncryptedPayload(String ciphertext, String nonce) {
    }

    /** AES-GCM 参数错误、加密失败或认证失败时抛出的统一异常。 */
    public class CryptoException extends RuntimeException {

        public CryptoException(String message) {
            super(message);
        }

        public CryptoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
