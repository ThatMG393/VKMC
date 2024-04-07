package com.thatmg393.vkmc.breeze3d.systems;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.thatmg393.vkmc.breeze3d.platform.VkStateManager;
import com.thatmg393.vkmc.vulkan.device.DeviceManager;
import com.thatmg393.vkmc.vulkan.utils.MappedBuffer;

import net.minecraft.client.Minecraft;

public class VkRenderSystem {
    private static final int OneSixBYFour = 16 * 4;
    private static final int ThreeByFour = 3 * 4;
    private static final int FourByFour = 4 * 4;
    private static final int TwoByFour = 2 * 4;

    private static long windowHandle;

    private static GlStateManager.LogicOp logicOp;

    private static MappedBuffer MVPbuf = MappedBuffer.create(OneSixBYFour);
    private static MappedBuffer MVmat = MappedBuffer.create(OneSixBYFour);
    private static MappedBuffer Pmat = MappedBuffer.create(OneSixBYFour);
    private static MappedBuffer Tmat = MappedBuffer.create(OneSixBYFour);

    private static MappedBuffer screenSize = MappedBuffer.create(TwoByFour);

    public static void initRenderer() {
        RenderSystem.assertOnRenderThread();
    }

    public static void enableDepthTest() {
        RenderSystem.assertOnGameThreadOrInit();
        VkStateManager._enableDepthTest();
    }

    public static void disableDepthTest() {
        
    }

    public static void applyMVP(Matrix4f modelView, Matrix4f projection) {
        setMVM(modelView);
        setPM(projection);
        calculateMVP();
    }

    public static void calculateMVP() {
        Matrix4f modelView = new Matrix4f(MVmat.buffer().asFloatBuffer());
        Matrix4f projection = new Matrix4f(Pmat.buffer().asFloatBuffer());

        projection.mul(modelView).get(MVPbuf.buffer());
    }

    public static void setPM(Matrix4f mat) {
        mat.get(Pmat.buffer().asFloatBuffer());
    }

    public static void setMVM(Matrix4f mat) {
        mat.get(MVmat.buffer().asFloatBuffer());
    }

    public static void setTM(Matrix4f mat) {
        mat.get(Tmat.buffer().asFloatBuffer());
    }

    public static void setWindowHandle(long handle) {
        windowHandle = handle;
    }

    public static void setLogicOp(GlStateManager.LogicOp op) {
        logicOp = op;
    }

    public static long getWindowHandle() {
        return windowHandle;
    }

    public static GlStateManager.LogicOp getLogicOp() {
        return logicOp;
    }

    public static int getDeviceMaxTextureSize() {
        return DeviceManager.getInstance().getSelectedDevice().getDeviceProperties().limits().maxImageDimension2D();
    }

    public static MappedBuffer getModelViewProjection() {
        return MVPbuf;
    }

    public static MappedBuffer getModelViewMat() {
        return MVmat;
    }

    public static MappedBuffer getProjectionMat() {
        return Pmat;
    }

    public static MappedBuffer getTextureMatrix() {
        return Tmat;
    }

    public static MappedBuffer getScreenSize() {
        Window w = Minecraft.getInstance().getWindow();
        
        screenSize.putFloat(0, (float) w.getWidth());
        screenSize.putFloat(4, (float) w.getHeight());

        return screenSize;
    }
}
