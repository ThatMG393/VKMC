package com.thatmg393.vkmc.vulkan.utils;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

public record MappedBuffer(
    ByteBuffer buffer,
    long pointer
) {
    public static MappedBuffer create(ByteBuffer buffer) {
        return new MappedBuffer(buffer, MemoryUtil.memAddress0(buffer));
    }

    public static MappedBuffer create(int size) {
        ByteBuffer buffer = MemoryUtil.memAlloc(size);
        return create(buffer);
    }

    public void putInt(int idx, int i) {
        UnsafeUtil.UNSAFE.putInt(pointer + idx, i);
    }

    public void putFloat(int idx, float f) {
        UnsafeUtil.UNSAFE.putFloat(pointer + idx, f);
    }

    public int getInt(int idx) {
        return UnsafeUtil.UNSAFE.getInt(pointer + idx);
    }

    public float getFloat(int idx) {
        return UnsafeUtil.UNSAFE.getFloat(pointer + idx);
    }
}
