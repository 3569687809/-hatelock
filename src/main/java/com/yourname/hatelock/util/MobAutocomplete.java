package com.yourname.hatelock.util;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class MobAutocomplete {

    // 缓存所有 mob id
    public static final List<String> MOB_LIST;

    static {
        List<String> temp = new ArrayList<>();

        for (EntityType<?> type : Registries.ENTITY_TYPE) {
            Identifier id = Registries.ENTITY_TYPE.getId(type);
            if (id != null) {
                temp.add(id.toString()); // minecraft:zombie
            }
        }

        Collections.sort(temp);
        MOB_LIST = Collections.unmodifiableList(temp);
    }

    // =========================
    // TAB 显示用：中文 + (id)
    // =========================
    public static String getDisplay(String rawId) {

        try {
            Identifier id = Identifier.tryParse(rawId);
            if (id == null) return rawId;

            EntityType<?> type = Registries.ENTITY_TYPE.get(id);

            // 关键：Minecraft 自动语言系统
            Text name = type.getName();
            String translated = name.getString();

            // 如果没有翻译 → fallback
            if (translated == null || translated.isEmpty()) {
                return rawId;
            }

            return translated + " (" + id.getPath() + ")";

        } catch (Exception e) {
            return rawId;
        }
    }

    // =========================
    // 搜索（TAB / 模糊匹配）
    // =========================
    public static List<String> search(String input) {

        if (input == null || input.isEmpty()) {
            List<String> out = new ArrayList<>();
            for (String id : MOB_LIST) {
                out.add(getDisplay(id));
            }
            return out;
        }

        String lower = input.toLowerCase();
        List<String> result = new ArrayList<>();

        for (String id : MOB_LIST) {
            if (id.toLowerCase().contains(lower)) {
                result.add(getDisplay(id));
            }
        }

        return result.stream().limit(20).toList();
    }

    // =========================
    // 判断是否存在
    // =========================
    public static boolean exists(String id) {
        return MOB_LIST.contains(id);
    }
}