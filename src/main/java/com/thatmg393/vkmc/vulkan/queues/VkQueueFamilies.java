package com.thatmg393.vkmc.vulkan.queues;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;

public class VkQueueFamilies {
    private static int graphicsFamily, transferFamily, presentFamily = VK10.VK_QUEUE_FAMILY_IGNORED;
    public static int getPresentFamily() {
        return presentFamily;
    }

    public static int getTransferFamily() {
        return transferFamily;
    }

    public static int getGraphicsFamily() {
        return graphicsFamily;
    }

    private static boolean hasDedicatedTransferQueue = false;
    public static boolean hasDedicatedTransferQueue() {
        return hasDedicatedTransferQueue;
    }

    public static boolean findQueueFamilies(VkPhysicalDevice physicalDevice) {
        return MemoryUtils.executeWithStackForResult(stack -> {
            IntBuffer familyCountBuf = stack.ints(0);
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, familyCountBuf, null);

            int familyCount = familyCountBuf.get(0);
            if (familyCount == 1) {
                graphicsFamily = transferFamily = presentFamily = 0;
                return true;
            }

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(familyCount, stack);
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, familyCountBuf, queueFamilies);

            for (int i = 0; i < queueFamilies.capacity(); i++) {
                int queueFlags = queueFamilies.get(i).queueFlags();

                if ((queueFlags & VK10.VK_QUEUE_GRAPHICS_BIT) != 0) {
                    graphicsFamily = i;
                    if ((queueFlags & VK10.VK_QUEUE_COMPUTE_BIT) != 0) {
                        presentFamily = i;
                    }
                }

                if ((queueFlags & (VK10.VK_QUEUE_COMPUTE_BIT | VK10.VK_QUEUE_GRAPHICS_BIT)) == 0
                 && (queueFlags & VK10.VK_QUEUE_TRANSFER_BIT) != 0) {
                    transferFamily = i;
                }

                if (presentFamily == VK10.VK_QUEUE_FAMILY_IGNORED) {
                    if ((queueFlags & VK10.VK_QUEUE_COMPUTE_BIT) != 0) {
                        presentFamily = i;
                    }
                }

                if (isComplete()) break;
            }

            if (transferFamily == VK10.VK_QUEUE_FAMILY_IGNORED) {
                int fallback = VK10.VK_QUEUE_FAMILY_IGNORED;
                for (int i = 0; i < queueFamilies.capacity(); i++) {
                    int queueFlags = queueFamilies.get(i).queueFlags();
                    if ((queueFlags & VK10.VK_QUEUE_TRANSFER_BIT) != 0) {
                        if (fallback == VK10.VK_QUEUE_FAMILY_IGNORED)
                            fallback = i;
                        
                        if ((queueFlags & (VK10.VK_QUEUE_GRAPHICS_BIT)) == 0)
                            fallback = i;

                        if (fallback == VK10.VK_QUEUE_FAMILY_IGNORED)
                            throw new RuntimeException("no queue family with transfer support");

                        transferFamily = fallback;
                    }
                }
            }

            hasDedicatedTransferQueue = graphicsFamily != transferFamily;

            if (graphicsFamily == VK10.VK_QUEUE_FAMILY_IGNORED)
                throw new RuntimeException("no queue family with graphics support");
            if (presentFamily == VK10.VK_QUEUE_FAMILY_IGNORED)
                throw new RuntimeException("no queue family with present support");

            return isComplete();
        });
    }

    public static boolean isComplete() {
        return graphicsFamily != VK10.VK_QUEUE_FAMILY_IGNORED
        && presentFamily != VK10.VK_QUEUE_FAMILY_IGNORED
        && transferFamily != VK10.VK_QUEUE_FAMILY_IGNORED;              
    }

    public static int[] uniqueFamily() {
        return IntStream.of(graphicsFamily, presentFamily, transferFamily).distinct().toArray();
    }
}