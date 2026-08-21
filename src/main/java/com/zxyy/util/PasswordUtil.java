package com.zxyy.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码加密与验证工具类
 * 使用 BCrypt 算法进行单向哈希加密，内置盐值，每次加密结果不同
 */
public class PasswordUtil {

    /**
     * 加密密码
     * @param plainPassword 明文密码（用户输入的原始密码）
     * @return 加密后的密文（BCrypt格式，包含盐值）
     */
    public static String encrypt(String plainPassword) {
        // 生成盐值并加密，第二个参数是强度（4-31，默认10，值越大越安全但越慢）
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * 校验密码是否匹配
     * @param plainPassword 明文密码（用户输入的）
     * @param hashedPassword 数据库中存储的加密密文
     * @return true-匹配成功，false-匹配失败
     */
    public static boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}
