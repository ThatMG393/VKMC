package com.thatmg393.vkmc.vulkan.device;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties2;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan11Features;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan11Properties;

import com.thatmg393.vkmc.vulkan.Vulkan;
import com.thatmg393.vkmc.vulkan.utils.MemoryUtils;

// I will fix the formatting later!
public class PhysicalDevice {
    private String deviceName;
    public String getDeviceName() {
        return deviceName;
    }

    private int deviceType;
    public int getDeviceType() {
        return deviceType;
    }

    private int apiVersion;
    private String vendorName;
    public String getVendorName() {
        return vendorName;
    }

    private String driverName;
    private String driverVersion;
    public String getDriverVersion() {
        return driverVersion;
    }

    public String getDriverName() {
        return driverName;
    }

    private int instanceLoaderVersion;

    public int getInstanceLoaderVersion() {
        return instanceLoaderVersion;
    }

    private VkPhysicalDeviceProperties deviceProperties;
    public VkPhysicalDeviceProperties getDeviceProperties() {
        return deviceProperties;
    }

    private VkPhysicalDeviceFeatures2 deviceFeatures;
    private VkPhysicalDeviceVulkan11Features deviceFeaturesVK11;

    private VkPhysicalDevice realPhysicalDevice;
    private VkDevice logicalDevice;

    public PhysicalDevice(VkPhysicalDevice device) {
        realPhysicalDevice = device;
        MemoryUtils.executeWithStack(stack -> {
            var deviceProperties2 = VkPhysicalDeviceProperties2.calloc()
                .sType$Default();
            var devicePropertiesVK11 = VkPhysicalDeviceVulkan11Properties.malloc(stack).sType$Default();
            deviceProperties2.pNext(devicePropertiesVK11);
            VK11.vkGetPhysicalDeviceProperties2(device, deviceProperties2);
            this.deviceProperties = deviceProperties2.properties();

            this.deviceName = deviceProperties.deviceNameString();
            this.apiVersion = deviceProperties.apiVersion();
            this.vendorName = parseVendorId(deviceProperties.vendorID());
            // driverName = parseDriverId(deviceProperties.
            this.driverVersion = parseIntedVersion(deviceProperties.driverVersion());
            this.instanceLoaderVersion = DeviceManager.getInstanceLoaderVersion();
            this.deviceType = deviceProperties.deviceType();

            this.deviceFeatures = VkPhysicalDeviceFeatures2.calloc()
                .sType$Default();
            this.deviceFeaturesVK11 = VkPhysicalDeviceVulkan11Features.malloc(stack).sType$Default();
            deviceFeatures.pNext(deviceFeaturesVK11);
            VK11.vkGetPhysicalDeviceFeatures2(device, this.deviceFeatures);
        });
    }

    public int getApiVersion() {
        return this.apiVersion;
    }

    public boolean isIndirectDrawSupported() {
        return this.deviceFeatures.features().multiDrawIndirect()
            && deviceFeaturesVK11.shaderDrawParameters();
    }

    public VkPhysicalDevice getAsVkPhysicalDevice() {
        return this.realPhysicalDevice;
    }

    public VkDevice getAsLogicalDevice() {
        if (this.logicalDevice != null) return this.logicalDevice;
        return MemoryUtils.executeWithStackForResult(stack -> {
            // int[] uniqueQueueFamily = QueueFamilyIndices.unique();

            var logicalDeviceProperty = VkPhysicalDeviceVulkan11Features.calloc(stack).sType$Default();
            logicalDeviceProperty.shaderDrawParameters(isIndirectDrawSupported());

            var logicalDeviceFeatures = VkPhysicalDeviceFeatures2.calloc(stack).sType$Default();
            logicalDeviceFeatures.pNext(logicalDeviceProperty);
            logicalDeviceFeatures.features().samplerAnisotropy(deviceFeatures.features().samplerAnisotropy());
            logicalDeviceFeatures.features().logicOp(deviceFeatures.features().logicOp());
            logicalDeviceFeatures.features().multiDrawIndirect(isIndirectDrawSupported());

            var logicalDeviceCreateInfo = VkDeviceCreateInfo.calloc(stack).sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            logicalDeviceCreateInfo.pNext(logicalDeviceFeatures);
            // logicalDeviceFeatures.ppEnabledExtensionNames();
            logicalDeviceCreateInfo.ppEnabledLayerNames(null);

            PointerBuffer logicalDevicePtr = stack.pointers(VK10.VK_NULL_HANDLE);
            if (VK10.vkCreateDevice(realPhysicalDevice, logicalDeviceCreateInfo, null, logicalDevicePtr) != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create logical device.");
            }

            this.logicalDevice = new VkDevice(logicalDevicePtr.get(0), realPhysicalDevice, logicalDeviceCreateInfo, Vulkan.VK_API_VERSION);
            return this.logicalDevice;
        });
    }

    public static String parseVendorId(int id) {
        return switch (id) {
            case (0x10DE) -> "Nvidia";
            case (0x1022) -> "AMD";
            case (0x8086) -> "Intel";
            case (0x13B5) -> "ARM";
            default -> "Unknown";
        };
    }

    public static String parseDriverId(String id) {
        return switch (id) {
            case ("DRIVER_ID_ARM_PROPRIETARY") -> "ARM (PROPRIETARY)";
            default -> "Unknown (Unknown)";
        };
    }

    public static String parseIntedVersion(int version) {
        return VK10.VK_VERSION_MAJOR(version)
        + "." + VK10.VK_VERSION_MINOR(version)
        + "." + VK10.VK_VERSION_PATCH(version);
    }
}