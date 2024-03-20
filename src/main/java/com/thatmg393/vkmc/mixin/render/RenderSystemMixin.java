package com.thatmg393.vkmc.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.systems.RenderSystem;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Shadow
    private static Thread renderThread;

    @Shadow
    private static void pollEvents() { }

    @Overwrite(remap=false)
    public static void initRenderer(int i1, boolean b1) {
        renderThread.setPriority(Thread.MAX_PRIORITY - 2);
    }

    @Overwrite(remap=false)
    public static void flipFrame(long window) {
        pollEvents();
        RenderSystem.replayQueue();
        pollEvents(); // Prevent input delays on PojavLauncher
    }
}
