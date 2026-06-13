package com.yourname.hatelock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HatelockMod implements ModInitializer {

	public static final String MOD_ID = "hatelock";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Hatelock v3 loading...");

		// ✅ 只初始化一次
		AggroHandler.init();

		// ✅ 只注册一次命令
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			AggroCommand.register(dispatcher);
		});

		LOGGER.info("Hatelock v3 loaded.");
	}
}