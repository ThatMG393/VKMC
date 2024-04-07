package com.thatmg393.vkmc.vulkan.shader.spirv.compiler.releaser;

import org.lwjgl.util.shaderc.ShadercIncludeResultReleaseI;

public class ShaderReleaser implements ShadercIncludeResultReleaseI {
    @Override
    public void invoke(long user_data, long include_result) {
        // no-op
    }
}
