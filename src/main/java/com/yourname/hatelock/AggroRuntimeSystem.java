package com.yourname.hatelock;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.List;

public class AggroRuntimeSystem {

    /**
     * 清除某个玩家相关仇恨
     */
    public static void clearPlayerAggro(ServerWorld world, String playerName) {

        List<MobEntity> mobs = world.getEntitiesByClass(
                MobEntity.class,
                new Box(-30000000, -64, -30000000, 30000000, 320, 30000000),
                mob -> true
        );

        for (MobEntity mob : mobs) {

            if (mob.getTarget() != null &&
                    mob.getTarget().getName().getString().equals(playerName)) {

                mob.setTarget(null);
            }
        }
    }

    /**
     * 清除某种怪物 + 玩家组合仇恨
     */
    public static void clearRule(ServerWorld world, String playerName, String mobId) {

        List<MobEntity> mobs = world.getEntitiesByClass(
                MobEntity.class,
                new Box(-30000000, -64, -30000000, 30000000, 320, 30000000),
                mob -> mob.getType().toString().contains(mobId)
        );

        for (MobEntity mob : mobs) {

            if (mob.getTarget() != null &&
                    mob.getTarget().getName().getString().equals(playerName)) {

                mob.setTarget(null);
            }
        }
    }
}