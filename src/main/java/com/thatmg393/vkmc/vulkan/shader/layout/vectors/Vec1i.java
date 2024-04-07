package com.thatmg393.vkmc.vulkan.shader.layout.vectors;

import java.util.function.Supplier;

import org.lwjgl.system.MemoryUtil;

import com.thatmg393.vkmc.vulkan.shader.UniformBuffers;
import com.thatmg393.vkmc.vulkan.shader.layout.Info;
import com.thatmg393.vkmc.vulkan.shader.layout.UniformBuffer;
import com.thatmg393.vkmc.vulkan.utils.MappedBuffer;

public class Vec1i extends UniformBuffer {
    private Supplier<Integer> ints;

    public Vec1i(Info info) {
        super(info);
    }

    @Override
    protected void setDefaultValueSupplier() {
        this.ints = UniformBuffers.vec1iUniformMap.get(getName());
    }

    @Override
    public void setValueSupplier(Supplier<MappedBuffer> valueSupplier) {
        this.ints = () -> valueSupplier.get().getInt(0);
    }

    @Override
    public void update(long pointer) {
        MemoryUtil.memPutInt(pointer + getRealOffset(), ints.get());
    }
}
