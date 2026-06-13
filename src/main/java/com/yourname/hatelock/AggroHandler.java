package com.yourname.hatelock;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.io.*;
import java.util.*;

public class AggroHandler {

    private static AggroConfig config = new AggroConfig();
    private static File file;

    // ================= INIT =================
    public static void init() {
        file = new File("config/hatelock/aggro.json");
        load();
    }

    // ================= 核心判断（AI用） =================
    public static boolean canTarget(MobEntity mob, ServerPlayerEntity player) {

        String mobId = Registries.ENTITY_TYPE.getId(mob.getType()).getPath();

        return config.rules.stream().anyMatch(r ->
                r.player.equals(player.getName().getString())
                        && r.mobId.equalsIgnoreCase(mobId)
        );
    }

    // ================= 规则 =================
    public static void addRule(String player, String mobId, long expire) {
        config.rules.add(new AggroRule(player, mobId, expire));
        save();
    }

    public static boolean removeRule(String player, String mobId) {
        boolean removed = config.rules.removeIf(r ->
                r.player.equals(player) && r.mobId.equalsIgnoreCase(mobId)
        );
        if (removed) save();
        return removed;
    }

    // ================= toggle =================
    public static boolean toggle() {
        config.enabled = !config.enabled;
        save();
        return config.enabled;
    }

    // ================= radius =================
    public static void setRadius(int r) {
        config.radius = r;
        save();
    }

    public static int getRadius() {
        return config.radius;
    }

    // ================= list =================
    public static Text listRules() {

        StringBuilder sb = new StringBuilder();
        long now = System.currentTimeMillis();

        for (AggroRule r : config.rules) {

            boolean perm = r.isPermanent();

            sb.append(r.player)
                    .append(" <- ")
                    .append(r.mobId)
                    .append(" ")
                    .append(perm ? "[永久]" : "[" + ((r.expireAt - now) / 1000) + "s]")
                    .append("\n");
        }

        return Text.literal(sb.toString());
    }

    // ================= config =================
    public static void load() {
        try {
            if (!file.exists()) {
                save();
                return;
            }

            config = new com.google.gson.Gson()
                    .fromJson(new FileReader(file), AggroConfig.class);

            if (config.rules == null) config.rules = new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            file.getParentFile().mkdirs();

            new com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(config, new FileWriter(file));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AggroConfig getConfig() {
        return config;
    }
}