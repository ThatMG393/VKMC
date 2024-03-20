package com.thatmg393.vkmc.vulkan.device;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

import com.thatmg393.vkmc.config.ModConfigManager;
import com.thatmg393.vkmc.vulkan.exceptions.NoGPUWithVulkanException;
import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;

public class DeviceManager {
    private static final DeviceManager INSTANCE = new DeviceManager();

    public static DeviceManager getInstance() {
        return INSTANCE;
    }

    private ArrayList<PhysicalDevice> physicalDevices = new ArrayList<>();
    private PhysicalDevice selectedDevice = null;

    public void initialize(VkInstance instance) {
        enumerateDevices(instance);
        
        int selectedDeviceIndex = ModConfigManager.getInstance().getLoadedConfig().selectedDevice;
        if (selectedDeviceIndex == -1) {
            for (PhysicalDevice device : physicalDevices) {
                if (device.getDeviceType() == VK10.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU
                        || device.getDeviceType() == VK10.VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU) {
                    // auto-pick
                    setSelectedDevice(device);
                }
            }
        } else {
            setSelectedDevice(physicalDevices.get(selectedDeviceIndex));
            // TODO: handle IOOB cus of hotswap or something
        }
    }

    public void enumerateDevices(VkInstance instance) {
        MemoryUtils.executeWithStack(stack -> {
            IntBuffer deviceCountBuf = stack.ints(0);
            VK10.vkEnumeratePhysicalDevices(instance, deviceCountBuf, null);

            int deviceCount = deviceCountBuf.get(0);
            if (deviceCount == 0)
                throw new NoGPUWithVulkanException("Failed to find a GPU with Vulkan support");

            PointerBuffer physicalDevicesPtr = stack.mallocPointer(deviceCount);
            VK10.vkEnumeratePhysicalDevices(instance, deviceCountBuf, physicalDevicesPtr);

            for (int i = 0; i < deviceCount; i++) {
                VkPhysicalDevice physicalDevice = new VkPhysicalDevice(physicalDevicesPtr.get(i), instance);
                if (physicalDeviceMeetsRequirements(physicalDevice)) {
                    physicalDevices.add(new PhysicalDevice(physicalDevice));
                }
            }
        });
    }

    public void setSelectedDevice(PhysicalDevice device) {
        this.selectedDevice = device;
    }

    public PhysicalDevice getSelectedDevice() {
        return this.selectedDevice;
    }

    public ArrayList<PhysicalDevice> getPhysicalDevices() {
        return this.physicalDevices;
    }

    public static int getInstanceLoaderVersion() {
        return MemoryUtils.executeWithStackForResult(stack -> {
            IntBuffer v = stack.mallocInt(0);
            VK11.vkEnumerateInstanceVersion(v);

            return v.get(0);
        });
    }

    public static boolean physicalDeviceMeetsRequirements(VkPhysicalDevice device) {
        return true;
    }
}
