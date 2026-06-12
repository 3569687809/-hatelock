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
		LOGGER.info("RodRailgun mod loaded!");

		// 初始化 AggroHandler（加载配置、启动 tick 监听）
		AggroHandler.init();

		// 注册命令
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			AggroCommand.register(dispatcher);
		});

		System.out.println("[MobAggro] 模组加载完成！");
	}
}