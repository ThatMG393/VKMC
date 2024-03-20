package com.thatmg393.vkmc.vulkan.pool.buffer;

import com.thatmg393.vkmc.vulkan.pool.VkCommandPool;

public class VkCommandBuffer {
    private VkCommandPool vkCmdPool;

    private org.lwjgl.vulkan.VkCommandBuffer vkCmdBuf;
    public org.lwjgl.vulkan.VkCommandBuffer getHandle() {
        return this.vkCmdBuf;
    }

    private long fence;
    public long getFence() {
        return this.fence;
    }

    private boolean submitted, recording;
    public boolean isRecording() {
        return this.recording;
    }

    public boolean isSubmitted() {
        return this.submitted;
    }

    public VkCommandBuffer(
        VkCommandPool vkCmdPool,
        org.lwjgl.vulkan.VkCommandBuffer vkCmdBuf,
        long fence
    ) {
        this.vkCmdPool = vkCmdPool;
        this.vkCmdBuf = vkCmdBuf;
        this.fence = fence;
    }

    public void reset() {
        this.submitted = false;
        this.recording = false;
        this.vkCmdPool.addToAvailable(this);
    }
}
