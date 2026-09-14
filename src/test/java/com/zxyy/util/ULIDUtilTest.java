package com.zxyy.util;

import org.junit.jupiter.api.Test;
import org.springframework.aop.scope.ScopedProxyUtils;

import static org.junit.jupiter.api.Assertions.*;

class ULIDUtilTest {

    @Test
    void generateULID() {
        for (int i = 0;i<10;i++){
            String s = ULIDUtil.generateULID();
            System.out.println("第"+i+"次:"+s);
        }
    }
}