package com.yourname.hatelock;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AggroHandler {

    private static AggroConfig config = new AggroConfig();
    private static int tickCounter = 0;
    private static File configFile;

    // ================= INIT =================
    public static void init() {

        configFile = new File("config/mobaggro/aggro.json");
        loadConfig();

        ServerTickEvents.END_SERVER_TICK.register(AggroHandler::onTick);
    }

    // ================= TICK =================
    private static void onTick(MinecraftServer server) {

        if (!config.enabled) return;

        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        long now = System.currentTimeMillis();

        config.rules.removeIf(r -> r.isExpired(now));

        for (ServerWorld world : server.getWorlds()) {

            for (ServerPlayerEntity player : world.getPlayers()) {

                if (player.isSpectator() || player.isCreative()) continue;

                Box box = player.getBoundingBox().expand(config.radius);

                List<MobEntity> mobs = world.getEntitiesByClass(
                        MobEntity.class,
                        box,
                        mob -> isRuleMatch(player, mob)
                );

                for (MobEntity mob : mobs) {
                    mob.setTarget(player);
                }
            }
        }
    }

    // ================= RULE MATCH =================
    private static boolean isRuleMatch(ServerPlayerEntity player, MobEntity mob) {

        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).getPath();

        return config.rules.stream().anyMatch(r ->
                r.player.equals(player.getName().getString())
                        && r.mobId.equalsIgnoreCase(mobId)
        );
    }

    // ================= API =================
    public static boolean toggle() {
        config.enabled = !config.enabled;
        saveConfig();
        return config.enabled;
    }

    public static void setRadius(int r) {
        config.radius = r;
        saveConfig();
    }

    public static int getRadius() {
        return config.radius;
    }

    public static void addRule(String player, String mobId, long expire) {
        config.rules.add(new AggroRule(player, mobId, expire));
        saveConfig();
    }

    // ================= REMOVE RULE =================
    public static boolean removeRule(MinecraftServer server, String player, String mobId) {

        boolean removed = config.rules.removeIf(r ->
                r.player.equals(player) && r.mobId.equalsIgnoreCase(mobId)
        );

        if (removed) {
            saveConfig();
            clearTargets(server, player, mobId);
        }

        return removed;
    }

    // ================= CLEAR TARGETS (FIXED) =================
    private static void clearTargets(MinecraftServer server, String player, String mobId) {

        for (ServerWorld world : server.getWorlds()) {

            world.iterateEntities().forEach(entity -> {

                if (!(entity instanceof MobEntity mob)) return;

                String id = Registries.ENTITY_TYPE.getId(mob.getType()).getPath();

                if (!id.equalsIgnoreCase(mobId)) return;

                if (mob.getTarget() == null) return;

                if (mob.getTarget() instanceof ServerPlayerEntity sp) {

                    if (sp.getName().getString().equals(player)) {
                        mob.setTarget(null);
                    }
                }
            });
        }
    }

    // ================= CONFIG =================
    public static void loadConfig() {
        try {
            if (!configFile.exists()) {
                saveConfig();
                return;
            }

            FileReader reader = new FileReader(configFile);
            config = new com.google.gson.Gson().fromJson(reader, AggroConfig.class);
            reader.close();

            if (config.rules == null) config.rules = new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            configFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(configFile);
            new com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AggroConfig getConfig() {
        return config;
    }

    // ================= LIST =================
    public static Text listRules() {

        MutableText text = Text.literal("MobAggro Rules:\n");
        long now = System.currentTimeMillis();

        for (AggroRule r : config.rules) {

            boolean perm = r.isPermanent();

            text.append(Text.literal(r.player + " <- " + r.mobId + " "));
            text.append(Text.literal(
                            perm ? "[永久]" : "[" + ((r.expireAt - now) / 1000) + "s]"
                    ).formatted(perm ? Formatting.RED : Formatting.YELLOW)
            );

            text.append(Text.literal("\n"));
        }

        return text;
    }
}