package com.thatmg393.vkmc.vulkan.shader;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

public class UniformBuffers {
    public static final Object2ReferenceOpenHashMap<String, Supplier<Integer>> vec1iUniformMap = new Object2ReferenceOpenHashMap<>();
    public static final Object2ReferenceOpenHashMap<String, Supplier<Float>> vec1fUniformMap = new Object2ReferenceOpenHashMap<>();

    public static void initDefaultUniformMaps() {
        // Vector 1 Int
        vec1iUniformMap.put("EndPortalLayers", () -> 15);

        // Vector 1 Float
        // vec1fUniformMap.put("", null);
    }
}
