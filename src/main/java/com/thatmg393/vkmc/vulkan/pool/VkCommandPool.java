package com.thatmg393.vkmc.vulkan.pool;

import java.nio.LongBuffer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;

import com.thatmg393.vkmc.vulkan.device.DeviceManager;
import com.thatmg393.vkmc.vulkan.interfaces.Freeable;
import com.thatmg393.vkmc.vulkan.pool.buffer.VkCommandBuffer;
import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class VkCommandPool implements Freeable {
    private long id;

    private final List<VkCommandBuffer> commandBuffers = new ObjectArrayList<>();
    private final Queue<VkCommandBuffer> availableBuffers = new ArrayDeque<>();

    public VkCommandPool(int family) {
        MemoryUtils.executeWithStack(stack -> {
            var cmdPoolCreateInfo = VkCommandPoolCreateInfo.calloc(stack);
            cmdPoolCreateInfo.sType(VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            cmdPoolCreateInfo.flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

            LongBuffer cmdPoolPtr = stack.mallocLong(1);
            if (VK10.vkCreateCommandPool(
                    DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice(),
                    cmdPoolCreateInfo,
                    null,
                    cmdPoolPtr
            ) != VK10.VK_SUCCESS) {
                throw new RuntimeException("creating command pool failed");
            }

            this.id = cmdPoolPtr.get(0);
        });
    }

    public VkCommandBuffer beginCommands() {
        return MemoryUtils.executeWithStackForResult(stack -> {
            VkCommandBuffer cmdBuf = availableBuffers.poll();

            var cmdBufBeginInfo = VkCommandBufferBeginInfo.calloc(stack);
            cmdBufBeginInfo.sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            cmdBufBeginInfo.flags(VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            VK10.vkBeginCommandBuffer(
                cmdBuf.getHandle(),
                cmdBufBeginInfo
            );

            return cmdBuf;
        });
    }

    public long submitCommands(VkCommandBuffer cmdBuf, VkQueue queue) {
        return MemoryUtils.executeWithStackForResult(stack -> {
            long fence = cmdBuf.getFence();

            VK10.vkEndCommandBuffer(cmdBuf.getHandle());
            VK10.vkResetFences(
                DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice(),
                fence
            );

            var submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType(VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(cmdBuf.getHandle()));

            VK10.vkQueueSubmit(queue, submitInfo, fence);

            return fence;
        });
    }

    public void addToAvailable(VkCommandBuffer vkCmdBuf) {
        this.availableBuffers.add(vkCmdBuf);
    }

    @Override
    public void free() {
        for (var cmdBuf : commandBuffers) {
            VK10.vkDestroyFence(
                DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice(),
                cmdBuf.getFence(),
                null
            );
        }

        VK10.vkResetCommandPool(
            DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice(),
            id,
            VK10.VK_COMMAND_POOL_RESET_RELEASE_RESOURCES_BIT
        );

        VK10.vkDestroyCommandPool(
            DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice(),
            id,
            null
        );
    }
}
