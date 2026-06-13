package com.yourname.hatelock;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class AggroHandler {

    private static AggroConfig config = new AggroConfig();
    private static File file;

    private static boolean enabled = true;
    private static int radius = 64;

    // ================= INIT =================
    public static void init() {

        file = new File("config/hatelock/aggro.json");
        load();

        ServerTickEvents.END_SERVER_TICK.register(AggroHandler::tick);
    }

    // ================= TOGGLE =================
    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    // ================= RADIUS =================
    public static void setRadius(int r) {
        radius = r;
    }

    public static int getRadius() {
        return radius;
    }

    // ================= RULE =================
    public static void addRule(String player, String mobId, long expire) {

        if (config == null) config = new AggroConfig();

        if (config.rules == null)
            config.rules = new java.util.ArrayList<>();

        config.rules.add(new AggroRule(player, mobId, expire));

        System.out.println("[Hatelock] addRule -> " + player + " " + mobId);

        save();
    }

    public static boolean removeRule(String player, String mobId) {
        boolean removed = config.rules.removeIf(r ->
                r.player.equals(player) && r.mobId.equalsIgnoreCase(mobId)
        );
        if (removed) save();
        return removed;
    }

    // ================= CORE CHECK（Mixin用） =================
    public static boolean isAllowed(MobEntity mob, ServerPlayerEntity player) {

        if (!enabled) return false;

        if (config.rules == null) return false;

        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).getPath();

        return config.rules.stream().anyMatch(r ->
                r.player.equals(player.getName().getString())
                        && r.mobId.equalsIgnoreCase(mobId)
        );
    }

    // ================= LIST =================
    public static Text listRules() {

        StringBuilder sb = new StringBuilder();
        sb.append("Aggro Rules:\n");

        long now = System.currentTimeMillis();

        for (AggroRule r : config.rules) {

            sb.append(r.player)
                    .append(" -> ")
                    .append(r.mobId);

            if (r.isPermanent()) {
                sb.append(" [PERM]");
            } else {
                sb.append(" [")
                        .append((r.expireAt - now) / 1000)
                        .append("s]");
            }

            sb.append("\n");
        }

        return Text.literal(sb.toString());
    }

    // ================= LOAD =================
    public static void load() {
        try {
            // ⚠️ 关键修复：先保证 config 一定存在
            if (config == null) {
                config = new AggroConfig();
            }

            if (!file.exists()) {
                save();
                return;
            }

            AggroConfig loaded = new com.google.gson.Gson()
                    .fromJson(new FileReader(file), AggroConfig.class);

            // ⚠️ 防空替换，而不是直接覆盖
            if (loaded != null) {
                config = loaded;
            }

            // ⚠️ 保底 rules
            if (config.rules == null) {
                config.rules = new java.util.ArrayList<>();
            }

        } catch (Exception e) {
            e.printStackTrace();

            // ⚠️ 崩溃兜底，保证服务器能启动
            if (config == null) {
                config = new AggroConfig();
            }
            if (config.rules == null) {
                config.rules = new java.util.ArrayList<>();
            }
        }
    }

    // ================= SAVE =================
    public static void save() {
        try {
            file.getParentFile().mkdirs();

            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(config, writer);
                writer.flush();
            }

            System.out.println("[Hatelock] saved rules = " + config.rules.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tick(MinecraftServer server) {

        if (config == null || !config.enabled) return;
        if (config.rules == null || config.rules.isEmpty()) return;

        for (ServerWorld world : server.getWorlds()) {

            for (ServerPlayerEntity player : world.getPlayers()) {

                Box box = player.getBoundingBox().expand(config.radius);

                for (MobEntity mob : world.getEntitiesByClass(
                        MobEntity.class,
                        box,
                        m -> true
                )) {

                    if (isAllowed(mob, player)) {
                        mob.setTarget(player);
                    }
                }
            }
        }
    }

    public static AggroConfig getConfig() {
        return config;
    }
}