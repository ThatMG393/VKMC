package com.thatmg393.vkmc.vulkan.shader.spirv.cache;

import java.util.HashMap;

import com.thatmg393.vkmc.vulkan.shader.spirv.SPIRVShader;

public class ShaderCaches {
    private static final ShaderCaches INSTANCE = new ShaderCaches();
    public static ShaderCaches getInstance() {
        return INSTANCE;
    }

    // private ObjectArraySet<ByteBuffer> bytecodeCache = new ObjectArraySet<>();
    private HashMap<String, SPIRVShader> shaderCache = new HashMap<>();

    public void addSPIRVShaderToCache(SPIRVShader shader) {
        shaderCache.put(shader.getName(), shader);
    }

    public void invalidateSPIRVShader(SPIRVShader shader) {
        shaderCache.remove(shader.getName());
    }

    public boolean isShaderInCache(String fileName) {
        return shaderCache.containsKey(fileName);
    }

    public SPIRVShader getCachedShader(String fileName) {
        return shaderCache.get(fileName);
    }
}
