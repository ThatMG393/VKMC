package com.thatmg393.vkmc.vulkan.utils;

import java.util.function.Consumer;
import java.util.function.Function;

import org.lwjgl.system.MemoryStack;

public class MemoryUtils {
    public static void executeWithStack(Consumer<MemoryStack> consumer) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            consumer.accept(stack);
        }
    }

    public static <T> T executeWithStackForResult(Function<MemoryStack, T> function) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return function.apply(stack);
        }
    }
}
