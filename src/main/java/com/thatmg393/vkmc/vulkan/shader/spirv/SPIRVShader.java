package com.thatmg393.vkmc.vulkan.shader.spirv;

import java.nio.ByteBuffer;

import org.lwjgl.util.shaderc.Shaderc;

import com.thatmg393.vkmc.vulkan.interfaces.Freeable;
import com.thatmg393.vkmc.vulkan.shader.spirv.cache.ShaderCaches;

public record SPIRVShader(String name, long shaderHandle, long resultSize) implements Freeable {
    public SPIRVShader(String name, long shaderHandle, long resultSize) {
        this.name = name;
        this.shaderHandle = shaderHandle;
        this.resultSize = resultSize;

        ShaderCaches.getInstance().addSPIRVShaderToCache(this);
    }

    public ByteBuffer bytecode() {
        return Shaderc.shaderc_result_get_bytes(shaderHandle, resultSize);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void free() {
        ShaderCaches.getInstance().invalidateSPIRVShader(this);
        Shaderc.shaderc_result_release(shaderHandle);
    }
}
