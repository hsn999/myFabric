package com.myfabric.myutil;

import java.util.UUID;

public class UUIDUtil {

    public static String get() {
        return UUID.randomUUID().toString();
    }
}
