package com.yourname.hatelock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class hatelock implements ModInitializer {

	public static final String MOD_ID = "hatelock";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("[Hatelock] 模组正在加载...");

		// 初始化仇恨系统（无参数版本）
		AggroHandler.init();

		// 注册命令
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			AggroCommand.register(dispatcher);
		});

		LOGGER.info("[Hatelock] 模组加载完成！");
	}
}