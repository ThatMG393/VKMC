package com.thatmg393.vkmc.vulkan.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.thatmg393.vkmc.vulkan.Vulkan;

public class VulkanRenderSystem {
    private static long window;

    public static void initRenderSystem() {
        RenderSystem.assertOnRenderThread();

        Vulkan.getInstance().initialize(window);
    }

    public static void setWindow(long newWindow) {
        window = newWindow;
    }
}
