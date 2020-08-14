package com.example.xs.utils;

import java.util.UUID;

public class GenerateId {


    public static String getUUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
