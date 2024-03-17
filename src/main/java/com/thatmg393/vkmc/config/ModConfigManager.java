package com.thatmg393.vkmc.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thatmg393.vkmc.VKMC;
import com.thatmg393.vkmc.config.data.ModConfigData;

import net.fabricmc.loader.api.FabricLoader;

public class ModConfigManager {
	private static ModConfigManager INSTANCE = new ModConfigManager();
	private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

	public static final File CONFIG_PATH = new File(Paths.get(
		FabricLoader.getInstance().getConfigDir().toString(),
		VKMC.MOD_ID + ".json").toString());

	public static final ModConfigData DEFAULT_CONFIG = new ModConfigData();

	public static ModConfigManager getInstance() {
		return INSTANCE;
	}

	private ModConfigData loadedConfig;

	public void loadConfig() {
		if (loadedConfig != null)
			VKMC.LOGGER.info("There is already a loaded config, overriding it!");

		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_PATH))) {
			loadedConfig = GSON.fromJson(reader, ModConfigData.class);
		} catch (Exception e) {
			VKMC.LOGGER.error("Failed to load config, loading with default values!");
			loadedConfig = new ModConfigData();
			saveConfig();
		}
	}

	public boolean saveConfig() {
		try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
			writer.write(GSON.toJson(loadedConfig));

			return true;
		} catch (Exception e) {
			VKMC.LOGGER.error("Failed to save config!");
			VKMC.LOGGER.error(e.toString());
		}

		return false;
	}

	public ModConfigData getLoadedConfig() {
		return this.loadedConfig;
	}
}
