package com.zxyy.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * HMAC-SHA256 工具类
 * 提供生成签名、验证签名等常用方法。
 */
public final class HmacSha256Util {
    //静态类禁止创建实例
    private HmacSha256Util(){}

    private static final String ALGORITHM = "HmacSHA256";
    private static final String CHARSET = "UTF-8";
    private static String key = "js51idibyW9rPAT5ffEOgD62aTcMhZywkryJCPCJCv8=";

    /**
     * 使用 HMAC-SHA256 算法生成签名（返回 Base64 编码字符串）
     *
     * @param data 待签名数据
     * @return Base64 编码的签名字符串
     */
    public static String sign(String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), ALGORITHM);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(CHARSET));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC-SHA256 签名计算失败", e);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 签名计算失败", e);
        }
    }



    /**
     * 验证签名是否匹配（Base64 编码）
     *
     * @param data      原始数据
     * @param signature 待验证的 Base64 签名
     * @return 验证通过返回 true，否则 false
     */
    public static boolean verify(String data, String signature) {
        String expected = sign(data);
        return constantTimeEquals(expected, signature);
    }



    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 常量时间比较，防止时序攻击
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        if (aBytes.length != bBytes.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }


}