package com.yourname.hatelock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class MobAggroMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // 初始化 AggroHandler
        AggroHandler.init();

        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            AggroCommand.register(dispatcher);
        });

        System.out.println("[MobAggro] 模组加载完成！");
    }
}