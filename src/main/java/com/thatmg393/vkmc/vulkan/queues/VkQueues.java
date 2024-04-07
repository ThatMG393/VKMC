package com.thatmg393.vkmc.vulkan.queues;

public enum VkQueues {
    GRAPHICS_QUEUE(VkQueueFamilies.getGraphicsFamily()),
    TRANSFER_QUEUE(VkQueueFamilies.getTransferFamily());

    private int family;
    private VkCommandQueue vkCmdQueue;

    VkQueues(int family) {
        this.family = family;
        this.vkCmdQueue = new VkCommandQueue(family);
    }

    public int getFamily() {
        return this.family;
    }

    public VkCommandQueue getCommandQueue() {
        return this.vkCmdQueue;
    }
}
