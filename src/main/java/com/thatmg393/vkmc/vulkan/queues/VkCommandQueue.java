package com.thatmg393.vkmc.vulkan.queues;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkQueue;

import com.thatmg393.vkmc.vulkan.device.DeviceManager;
import com.thatmg393.vkmc.vulkan.interfaces.Freeable;
import com.thatmg393.vkmc.vulkan.pool.VkCommandPool;
import com.thatmg393.vkmc.vulkan.pool.buffer.VkCommandBuffer;
import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;

public class VkCommandQueue implements Freeable {
    private final VkCommandPool vkCmdPool;
    private final VkQueue vkQueue;

    public VkCommandQueue(int family) {
        this.vkQueue = MemoryUtils.executeWithStackForResult(stack -> {
            PointerBuffer queuePtr = stack.mallocPointer(1);
            VK10.vkGetDeviceQueue(
                DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice(),
                family,
                0,
                queuePtr
            );

            return new VkQueue(queuePtr.get(0), DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice());
        });
        this.vkCmdPool = new VkCommandPool(family);
    }

    public VkCommandBuffer beginCommands() {
        return this.vkCmdPool.beginCommands();
    }

    public void submitCommands(VkCommandBuffer vkCmdBuf) {
        this.vkCmdPool.submitCommands(vkCmdBuf, vkQueue);
    }

    public void waitIdle() {
        VK10.vkQueueWaitIdle(vkQueue);
    }

    public VkQueue getVkQueue() {
        return this.vkQueue;
    }

    @Override
    public void free() {
        if (vkCmdPool != null) vkCmdPool.free();
    }
}