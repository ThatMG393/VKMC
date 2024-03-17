package com.thatmg393.vkmc;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VKMC implements ModInitializer {
	public static final String MOD_ID = "vkmc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Config");
		ModConfigManager.getInstance().loadConfig();
	}
}
