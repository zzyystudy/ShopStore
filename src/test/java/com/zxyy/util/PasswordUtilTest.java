package com.zxyy.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void encrypt() {
        String encrypt = PasswordUtil.encrypt("123456");
        System.out.println(encrypt);
    }

    @Test
    void matches() {
        boolean matched = PasswordUtil.matches("123456","$2a$10$5T.1xzqcTB5haHhblZxAw.hGoGLbo1cFruUWWWRQq7b6WCUbYdZD2");
        System.out.println(matched);
    }
}