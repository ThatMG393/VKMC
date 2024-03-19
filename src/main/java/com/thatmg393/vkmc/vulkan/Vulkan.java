package com.thatmg393.vkmc.vulkan;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import com.thatmg393.vkmc.VKMC;
import com.thatmg393.vkmc.vulkan.device.DeviceManager;
import com.thatmg393.vkmc.vulkan.exceptions.FailedToCreateVulkanInstance;
import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;
import com.thatmg393.vkmc.vulkan.utils.ReturnUtils;

public class Vulkan {
    private static final Vulkan INSTANCE = new Vulkan();
    public static Vulkan getInstance() {
        return INSTANCE;
    }

    public static final int VK_API_VERSION = VK11.VK_API_VERSION_1_1;

    private VkInstance vkInstance;
    public VkInstance getVkInstance() {
        return vkInstance;
    }

    private long surface;
    public long getSurface() {
        return surface;
    }

    private long vmaPointer;
    public long getVmaPointer() {
        return vmaPointer;
    }

    public void initialize(long window) {
        initializeVulkan();
        initializeWindowSurface(window);
        
        DeviceManager.getInstance().initialize(vkInstance);

        initializeVMA();
    }

    private void initializeVulkan() {
        MemoryUtils.executeWithStack(stack -> {
            var vkAppInfo = VkApplicationInfo.calloc(stack);
            vkAppInfo.sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO);
            vkAppInfo.pApplicationName(stack.UTF8Safe(VKMC.MOD_ID));
            vkAppInfo.applicationVersion(VK10.VK_MAKE_VERSION(1, 0, 0));
            vkAppInfo.pEngineName(stack.UTF8Safe(VKMC.MOD_ID + " engine"));
            vkAppInfo.engineVersion(VK10.VK_MAKE_VERSION(1, 0, 0));
            vkAppInfo.apiVersion(VK_API_VERSION);

            var vkInstanceCreateInfo = VkInstanceCreateInfo.calloc(stack);
            vkInstanceCreateInfo.sType(VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO);
            vkInstanceCreateInfo.pApplicationInfo(vkAppInfo);
            vkInstanceCreateInfo.ppEnabledExtensionNames(GLFWVulkan.glfwGetRequiredInstanceExtensions());

            PointerBuffer vkInstancePtr = stack.mallocPointer(1);
            if (VK10.vkCreateInstance(vkInstanceCreateInfo, null, vkInstancePtr) != VK10.VK_SUCCESS) {
                throw new FailedToCreateVulkanInstance();
            }

            vkInstance = new VkInstance(vkInstancePtr.get(0), vkInstanceCreateInfo);
        });
    }

    private void initializeWindowSurface(long window) {
        MemoryUtils.executeWithStack(stack -> {
            LongBuffer surfacePtr = stack.longs(VK10.VK_NULL_HANDLE);

            if (GLFWVulkan.glfwCreateWindowSurface(vkInstance, window, null, surfacePtr) != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create window surface");
            }

            surface = surfacePtr.get(0);
        });
    }

    private void initializeVMA() {
        MemoryUtils.executeWithStack(stack -> {
            var vmaVkFunctions = VmaVulkanFunctions.calloc(stack);
            vmaVkFunctions.set(vkInstance, DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice());

            var vmaAllocCreateInfo = VmaAllocatorCreateInfo.calloc(stack);
            vmaAllocCreateInfo.physicalDevice(DeviceManager.getInstance().getSelectedDevice().getAsVkPhysicalDevice());
            vmaAllocCreateInfo.device(DeviceManager.getInstance().getSelectedDevice().getAsLogicalDevice());
            vmaAllocCreateInfo.pVulkanFunctions(vmaVkFunctions);
            vmaAllocCreateInfo.instance(vkInstance);
            vmaAllocCreateInfo.vulkanApiVersion(VK_API_VERSION);

            PointerBuffer vmaPtr = stack.pointers(VK10.VK_NULL_HANDLE);
            if (!ReturnUtils.isVkSuccess(
                Vma.vmaCreateAllocator(vmaAllocCreateInfo, vmaPtr)
            )) {
                throw new RuntimeException("no allocation lool");
            }

            this.vmaPointer = vmaPtr.get(0);
        });
    }
}
