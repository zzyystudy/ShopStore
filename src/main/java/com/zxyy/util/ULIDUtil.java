package com.zxyy.util;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

public final class ULIDUtil {

    //静态类禁止生成实体
    private ULIDUtil(){}

    //生成ULID
    public static String generateULID(){
        Ulid monotonicUlid = UlidCreator.getMonotonicUlid();
        return monotonicUlid.toString();
    }
}
