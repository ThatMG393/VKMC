package com.thatmg393.vkmc.vulkan.shader.spirv;

import org.lwjgl.util.shaderc.Shaderc;

public enum ShaderType {
    VERTEX(Shaderc.shaderc_vertex_shader),
    FRAGMENT(Shaderc.shaderc_fragment_shader);

    private final int shaderType;

    ShaderType(int shaderType) {
        this.shaderType = shaderType;
    }

    public int getShaderType() {
        return this.shaderType;
    }
}
