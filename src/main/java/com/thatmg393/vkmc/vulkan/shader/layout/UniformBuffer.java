package com.thatmg393.vkmc.vulkan.shader.layout;

import java.util.function.Supplier;

import org.lwjgl.system.MemoryUtil;

import com.thatmg393.vkmc.vulkan.utils.MappedBuffer;

public class UniformBuffer {
    public static UniformBuffer createField(Info info) {
        return switch (info.getType()) {
            default -> null;
        };
    }

    private Info info;
    private long offset;
    private int size;

    private Supplier<MappedBuffer> values;

    public UniformBuffer(Info info) {
        this.info = info;
        this.offset = info.getOffset() * 4L;
        this.size = info.getSize() * 4;
        this.setDefaultValueSupplier();
    }
    
    protected void setDefaultValueSupplier() {
        setValueSupplier(
            switch (info.getType()) {
                case "mat4" -> null;

                case "vec4" -> null;
                case "vec3" -> null;
                case "vec2" -> null;

                default -> null;
            }
        );
    }

    public void update(long pointer) {
        MemoryUtil.memCopy(values.get().pointer(), pointer + this.offset, this.size);
    }

    public void setValueSupplier(Supplier<MappedBuffer> valueSupplier) {
        this.values = valueSupplier;
    }

    public String getName() {
        return this.info.getName();
    }

    public int getOffset() {
        return this.info.getOffset();
    }

    public long getRealOffset() {
        return this.offset;
    }

    public int getSize() {
        return this.info.getSize();
    }

    @Override
    public String toString() {
        return info.getType() + ": " + info.getName() + " offset: " + info.getOffset();
    }
}
