package com.thatmg393.vkmc.vulkan.shader.spirv.compiler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.util.shaderc.Shaderc;

import com.thatmg393.vkmc.vulkan.Vulkan;
import com.thatmg393.vkmc.vulkan.shader.spirv.SPIRVShader;
import com.thatmg393.vkmc.vulkan.shader.spirv.ShaderType;
import com.thatmg393.vkmc.vulkan.shader.spirv.cache.ShaderCaches;
import com.thatmg393.vkmc.vulkan.shader.spirv.compiler.includer.HeaderIncluder;
import com.thatmg393.vkmc.vulkan.shader.spirv.compiler.releaser.ShaderReleaser;

public class SPIRVCompiler {
    public static final boolean OPTIMIZE_SHADER = true;

    private static final SPIRVCompiler INSTANCE = new SPIRVCompiler();

    private static boolean compilerInitialized = false;

    public static SPIRVCompiler getInstance() {
        if (!compilerInitialized) initCompiler();
        return INSTANCE;
    }

    private static void initCompiler() {
        SPIRVCompiler me = INSTANCE;

        me.compilerHandle = Shaderc.shaderc_compiler_initialize();
        if (me.compilerHandle == 0) throw new RuntimeException("Failed to create ShaderC Compiler.");

        me.optionsHandle = Shaderc.shaderc_compile_options_initialize();
        if (me.optionsHandle == 0) throw new RuntimeException("Failed to create compiler options.");

        if (OPTIMIZE_SHADER) Shaderc.shaderc_compile_options_set_optimization_level(me.optionsHandle, Shaderc.shaderc_optimization_level_performance);
        // TODO: add debug

        Shaderc.shaderc_compile_options_set_target_env(me.optionsHandle, Shaderc.shaderc_env_version_vulkan_1_1, Vulkan.VK_API_VERSION);
        Shaderc.shaderc_compile_options_set_include_callbacks(me.optionsHandle, me.headerIncluder, me.shaderReleaser, 0);

        compilerInitialized = true;

        me.headerIncluder.addToIncludePath("assets/vkmc/shaders/include");
    }

    private long compilerHandle = 0;
    private long optionsHandle = 0;

    private HeaderIncluder headerIncluder = new HeaderIncluder();
    private ShaderReleaser shaderReleaser = new ShaderReleaser();

    public SPIRVShader compileShader(String filePath, String src, ShaderType type) {
        if (ShaderCaches.getInstance().isShaderInCache(filePath))
            return ShaderCaches.getInstance().getCachedShader(filePath);

        long result = Shaderc.shaderc_compile_into_spv(
            compilerHandle,
            src,
            type.getShaderType(),
            filePath,
            "main",
            optionsHandle
        );

        if (result == 0) throw new RuntimeException("Failed to compile shader.");
        if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success)
            throw new RuntimeException("An error occurred while compiling shader! " + Shaderc.shaderc_result_get_error_message(result));

        // name might have a duplicate
        return new SPIRVShader(filePath, result, Shaderc.shaderc_result_get_length(result));
    }

    public SPIRVShader compileShader(String filePath, ShaderType type) {
        try {
            String src = Files.readString(Paths.get(new URI(filePath)));
            return compileShader(filePath, src, type);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
