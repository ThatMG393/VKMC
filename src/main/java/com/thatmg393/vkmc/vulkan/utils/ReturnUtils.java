package com.thatmg393.vkmc.vulkan.utils;

import org.lwjgl.vulkan.VK10;

public class ReturnUtils {
    public static boolean isVkSuccess(int output) {
        return output == VK10.VK_SUCCESS;
    }
}