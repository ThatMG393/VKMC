package com.thatmg393.vkmc.config.data;

public class ModConfigData {
	public int selectedDevice = -1;

	public int renderAheadCount = 1;

	public ChunkCullingMethod cullingMethod = ChunkCullingMethod.NORMAL;
	public boolean entityCulling = true;

	public static enum ChunkCullingMethod {
		NORMAL,
		AGGRESSIVE
	}
}
