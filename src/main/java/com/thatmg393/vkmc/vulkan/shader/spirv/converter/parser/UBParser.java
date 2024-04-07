package com.thatmg393.vkmc.vulkan.shader.spirv.converter.parser;

import java.util.ArrayList;
import java.util.List;

import com.thatmg393.vkmc.vulkan.shader.converter.enums.ShaderStage;
import com.thatmg393.vkmc.vulkan.shader.converter.utils.StringUtils;
import com.thatmg393.vkmc.vulkan.shader.spirv.converter.GLSL2SPIRV;

public class UBParser {
    public static final record MiniUniform(String name, String type) { }

    private GLSL2SPIRV converter;

    private String type;
    private String name;

    private StageUniforms[] stageUniforms = new StageUniforms[ShaderStage.values().length];
    private StageUniforms currentStageUniforms;

    private ArrayList<MiniUniform> globalUniforms = new ArrayList<>();

    public UBParser(GLSL2SPIRV converter) {
        this.converter = converter;

        for (int i = 0; i < stageUniforms.length; i++) stageUniforms[i] = new StageUniforms();
    }

    public boolean parseToken(String token) {
        if (token.equals("uniform")) return false;

        if (this.type == null) this.type = token;
        else if (this.name == null) {
            token = StringUtils.removeCharAtLast(token, ';');
            name = token;

            MiniUniform mu = new MiniUniform(this.name, this.type);
            if (this.type.equals("sampler2D")
             && !this.currentStageUniforms.samplers.contains(mu)) {
                this.currentStageUniforms.samplers.add(mu);
            } else {
                if (!this.globalUniforms.contains(mu)) {
                    this.globalUniforms.add(mu);
                }
            }

            resetState();
            return true;
        }

        return false;
    }

    public void setCurrentUniform(ShaderStage stage) {
        this.currentStageUniforms = stageUniforms[stage.ordinal()];
    }

    private void resetState() {
        this.name = null;
        this.type = null;
    }

    public static class StageUniforms {
        private ArrayList<MiniUniform> samplers = new ArrayList<>();
    }
}
