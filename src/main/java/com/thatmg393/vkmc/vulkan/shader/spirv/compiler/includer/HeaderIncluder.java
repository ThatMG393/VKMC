package com.thatmg393.vkmc.vulkan.shader.spirv.compiler.includer;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.ShadercIncludeResolveI;
import org.lwjgl.util.shaderc.ShadercIncludeResult;

import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class HeaderIncluder implements ShadercIncludeResolveI {
    // private final int MAX_PATH_SIZE_LINUX = 4096;
    private final ObjectArrayList<String> includePaths = new ObjectArrayList<>();

    @Override
    public long invoke(long user_data, long requested_source, int type, long requesting_source, long include_depth) {
        String requesting = MemoryUtil.memASCII(requesting_source);
        String requested = MemoryUtil.memASCII(requested_source);

        return MemoryUtils.executeWithStackForResult(stack -> {
            try {
                for (String path : includePaths) {
                    Path filePath = Paths.get(path + requested);

                    if (Files.exists(filePath)) {
                        return ShadercIncludeResult.malloc(stack)
                                .source_name(stack.ASCII(requested))
                                .content(stack.bytes(Files.readAllBytes(filePath)))
                                .user_data(user_data)
                                .address();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e);
            }

            throw new RuntimeException(
                requesting + ": not found " + requested + " in paths"
            );
        });
    }

    public void addToIncludePath(String path) {
        path = getClass().getResource(path).toExternalForm();

        if (includePaths.contains(path)) return;
        includePaths.add(path);
    }

    public ObjectArrayList<String> getIncludePaths() {
        return this.includePaths;
    }
}