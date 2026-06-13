package com.yourname.hatelock;

import java.util.HashMap;
import java.util.Map;

public class MobNameMap {

    private static final Map<String, String> ID_TO_CN = new HashMap<>();
    private static final Map<String, String> CN_TO_ID = new HashMap<>();

    static {
        add("zombie", "僵尸");
        add("creeper", "苦力怕");
        add("skeleton", "骷髅");
        add("spider", "蜘蛛");
        add("cave_spider", "洞穴蜘蛛");
        add("enderman", "末影人");
        add("witch", "女巫");
        add("slime", "史莱姆");
        add("magma_cube", "岩浆怪");
        add("blaze", "烈焰人");
        add("ghast", "恶魂");
        add("piglin", "猪灵");
        add("piglin_brute", "猪灵蛮兵");
        add("zombified_piglin", "僵尸猪灵");
        add("hoglin", "疣猪兽");
        add("zoglin", "僵尸疣猪兽");
        add("warden", "监守者");
        add("ravager", "劫掠兽");
        add("evoker", "唤魔者");
        add("vindicator", "卫道士");
        add("pillager", "掠夺者");
        add("illusioner", "幻术师");
        add("phantom", "幻翼");
        add("silverfish", "蠹虫");
        add("endermite", "末影螨");
        add("guardian", "守卫者");
        add("elder_guardian", "远古守卫者");
        add("drowned", "溺尸");
        add("husk", "尸壳");
        add("stray", "流浪者");
        add("bogged", "沼骸");
        add("breeze", "旋风人");
        add("creaking", "嘎枝");
    }

    private static void add(String id, String cn) {
        ID_TO_CN.put(id, cn);
        CN_TO_ID.put(cn, id);
    }

    public static String getChinese(String id) {
        return ID_TO_CN.getOrDefault(id, id);
    }

    public static String getId(String input) {
        return CN_TO_ID.getOrDefault(input, input);
    }

    public static String getDisplay(String id) {
        return getChinese(id) + " (" + id + ")";
    }
}