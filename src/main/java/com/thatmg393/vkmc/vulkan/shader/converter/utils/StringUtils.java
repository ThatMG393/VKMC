package com.thatmg393.vkmc.vulkan.shader.converter.utils;

public class StringUtils {
    public static String removeCharAtLast(String orig, char targetChar) {
        int last = orig.length() - 1;
        if ((orig.charAt(last)) != targetChar)
            throw new IllegalArgumentException("last char is not " + targetChar);
        
        return orig.substring(0, last);
    }
}
