package com.zxyy.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AESGCMUtilTest {

    @Autowired
    private AESGCMUtil aesgcmUtil;

    @Test
    void generateBase64Key() {
        String s = aesgcmUtil.generateBase64Key();
        System.out.println(s);
    }
}