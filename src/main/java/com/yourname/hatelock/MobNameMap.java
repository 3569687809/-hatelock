package com.yourname.hatelock;

import java.util.HashMap;
import java.util.Map;

public class MobNameMap {

    private static final Map<String, String> MAP = new HashMap<>();

    static {

        // ===== Minecraft 常见生物 =====
        MAP.put("zombie", "僵尸");
        MAP.put("skeleton", "骷髅");
        MAP.put("creeper", "苦力怕");
        MAP.put("spider", "蜘蛛");
        MAP.put("enderman", "末影人");
        MAP.put("witch", "女巫");
        MAP.put("slime", "史莱姆");
        MAP.put("blaze", "烈焰人");
        MAP.put("ghast", "恶魂");
        MAP.put("piglin", "猪灵");
        MAP.put("hoglin", "疣猪兽");
        MAP.put("warden", "监守者");

        // ===== 动物 =====
        MAP.put("cow", "牛");
        MAP.put("pig", "猪");
        MAP.put("sheep", "羊");
        MAP.put("chicken", "鸡");
        MAP.put("rabbit", "兔子");
        MAP.put("horse", "马");

        // ===== 海洋生物 =====
        MAP.put("drowned", "溺尸");
        MAP.put("guardian", "守卫者");
        MAP.put("elder_guardian", "远古守卫者");
        MAP.put("squid", "鱿鱼");

        // ===== 其他 =====
        MAP.put("villager", "村民");
        MAP.put("iron_golem", "铁傀儡");
    }

    // ================= 核心方法 =================
    public static String getDisplay(String id) {

        if (id == null || id.isEmpty()) {
            return "unknown";
        }

        String cn = MAP.get(id);

        if (cn != null) {
            return cn + " (" + id + ")";
        }

        // fallback：避免服务器端崩溃
        return id;
    }

    // ================= 可扩展注册 =================
    public static void register(String id, String name) {
        MAP.put(id, name);
    }
}