package com.thatmg393.vkmc.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.thatmg393.vkmc.breeze3d.systems.VkRenderSystem;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Shadow
    private static Thread renderThread;

    @Shadow
    private static void pollEvents() { }

    @Overwrite(remap=false)
    public static void initRenderer(int i1, boolean b1) {
        renderThread.setPriority(Thread.MAX_PRIORITY - 1);
        VkRenderSystem.initRenderer();

    }

    @Overwrite(remap=false)
    public static void flipFrame(long window) {
        pollEvents();
        RenderSystem.replayQueue();
        Tesselator.getInstance().getBuilder().clear();
        pollEvents(); // Prevent input delays on PojavLauncher
    }
}
