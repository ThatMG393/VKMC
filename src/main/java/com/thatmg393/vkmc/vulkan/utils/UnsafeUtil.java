package com.thatmg393.vkmc.vulkan.utils;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeUtil {
    public static final Unsafe UNSAFE;

    static {
        Field f = null;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
