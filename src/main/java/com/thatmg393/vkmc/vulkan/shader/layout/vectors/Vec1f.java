package com.thatmg393.vkmc.vulkan.shader.layout.vectors;

import java.util.function.Supplier;

import org.lwjgl.system.MemoryUtil;

import com.thatmg393.vkmc.vulkan.shader.UniformBuffers;
import com.thatmg393.vkmc.vulkan.shader.layout.Info;
import com.thatmg393.vkmc.vulkan.shader.layout.UniformBuffer;
import com.thatmg393.vkmc.vulkan.utils.MappedBuffer;

public class Vec1f extends UniformBuffer {
    private Supplier<Float> floats;

    public Vec1f(Info info) {
        super(info);
    }

    @Override
    protected void setDefaultValueSupplier() {
        this.floats = UniformBuffers.vec1fUniformMap.get(getName());
    }

    @Override
    public void setValueSupplier(Supplier<MappedBuffer> valueSupplier) {
        this.floats = () -> valueSupplier.get().getFloat(0);
    }

    @Override
    public void update(long pointer) {
        MemoryUtil.memPutFloat(pointer + getRealOffset(), floats.get());
    }
}
