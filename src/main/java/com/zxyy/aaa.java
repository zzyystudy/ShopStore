package com.zxyy;

import org.springframework.aop.scope.ScopedProxyUtils;

public class aaa {
    public static void main(String[] args){
        byte x = 64;
        byte y = 64;
        byte z = (byte) (x+y);
        System.out.println(z);
        System.out.println(x+y);
    }
}
