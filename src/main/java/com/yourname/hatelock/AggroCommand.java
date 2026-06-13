package com.yourname.hatelock;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;

import java.util.stream.StreamSupport;

import static net.minecraft.server.command.CommandManager.*;

public class AggroCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(
                literal("aggro")

                        // ================= toggle =================
                        .then(literal("toggle")
                                .executes(ctx -> {
                                    boolean state = AggroHandler.toggle();
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("Aggro: " + (state ? "ON" : "OFF")), false);
                                    return 1;
                                })
                        )

                        // ================= reload =================
                        .then(literal("reload")
                                .executes(ctx -> {
                                    AggroHandler.loadConfig();
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("Config reloaded"), false);
                                    return 1;
                                })
                        )

                        // ================= radius =================
                        .then(literal("radius")
                                .then(argument("value", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 512))
                                        .executes(ctx -> {
                                            int r = com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "value");
                                            AggroHandler.setRadius(r);
                                            ctx.getSource().sendFeedback(() ->
                                                    Text.literal("Radius set to " + r), false);
                                            return 1;
                                        })
                                )
                        )

                        // ================= list =================
                        .then(literal("list")
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(
                                            () -> AggroHandler.listRules(), // <- 注意这里用 lambda 包装
                                            false
                                    );
                                    return 1;
                                })
                        )

                        // ================= set =================
                        .then(literal("set")
                                .then(argument("player", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            var server = ctx.getSource().getServer();
                                            server.getPlayerManager().getPlayerList()
                                                    .forEach(p -> builder.suggest(p.getName().getString()));
                                            return builder.buildFuture();
                                        })

                                        .then(argument("mobId", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {

                                                    StreamSupport.stream(Registries.ENTITY_TYPE.spliterator(), false)
                                                            .forEach(type -> {

                                                                String id = Registries.ENTITY_TYPE.getId(type).getPath();

                                                                builder.suggest(
                                                                        id,
                                                                        Text.literal(getMobDisplay(id))
                                                                );
                                                            });

                                                    return builder.buildFuture();
                                                })

                                                .then(argument("time", StringArgumentType.word())
                                                        .executes(ctx -> {

                                                            String player = StringArgumentType.getString(ctx, "player");
                                                            String mobId = StringArgumentType.getString(ctx, "mobId");
                                                            long expire = parseTime(StringArgumentType.getString(ctx, "time"));

                                                            AggroHandler.addRule(player, mobId, expire);

                                                            ctx.getSource().sendFeedback(() ->
                                                                    Text.literal("Added: " + player + " -> " + mobId), false);

                                                            return 1;
                                                        })
                                                )

                                                .executes(ctx -> {

                                                    String player = StringArgumentType.getString(ctx, "player");
                                                    String mobId = StringArgumentType.getString(ctx, "mobId");

                                                    AggroHandler.addRule(player, mobId, -1);

                                                    ctx.getSource().sendFeedback(() ->
                                                            Text.literal("Added (PERM): " + player + " -> " + mobId), false);

                                                    return 1;
                                                })
                                        )
                                )
                        )

                        // ================= remove =================
                        .then(literal("remove")
                                .then(argument("player", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            AggroHandler.getConfig().rules.stream()
                                                    .map(r -> r.player)
                                                    .distinct()
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })

                                        .then(argument("mobId", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {

                                                    String player = StringArgumentType.getString(ctx, "player");

                                                    AggroHandler.getConfig().rules.stream()
                                                            .filter(r -> r.player.equals(player))
                                                            .forEach(r -> {
                                                                builder.suggest(
                                                                        r.mobId,
                                                                        Text.literal(getMobDisplay(r.mobId))
                                                                );
                                                            });

                                                    return builder.buildFuture();
                                                })

                                                .executes(ctx -> {

                                                    String player = StringArgumentType.getString(ctx, "player");
                                                    String mobId = StringArgumentType.getString(ctx, "mobId");

                                                    boolean removed = AggroHandler.removeRule(ctx.getSource().getServer(), player, mobId);

                                                    if (removed) {

                                                        ctx.getSource().getServer().getWorlds().forEach(world -> {

                                                            world.iterateEntities().forEach(entity -> {

                                                                if (entity instanceof MobEntity mob) {

                                                                    String entityId = Registries.ENTITY_TYPE
                                                                            .getId(mob.getType())
                                                                            .getPath();

                                                                    if (!entityId.equals(mobId)) {
                                                                        return;
                                                                    }

                                                                    if (mob.getTarget() != null &&
                                                                            mob.getTarget().getName().getString().equals(player)) {

                                                                        mob.setTarget(null);
                                                                    }
                                                                }
                                                            });
                                                        });
                                                    }

                                                    ctx.getSource().sendFeedback(() ->
                                                            Text.literal(removed ? "Removed" : "Not found"), false);

                                                    return 1;
                                                })
                                        )

                                        .executes(ctx -> {

                                            String player = StringArgumentType.getString(ctx, "player");

                                            AggroHandler.getConfig().rules.removeIf(r -> r.player.equals(player));
                                            AggroHandler.saveConfig();

                                            ctx.getSource().getServer().getWorlds().forEach(world -> {

                                                world.iterateEntities().forEach(entity -> {

                                                    if (entity instanceof MobEntity mob) {

                                                        if (mob.getTarget() != null &&
                                                                mob.getTarget().getName().getString().equals(player)) {

                                                            mob.setTarget(null);
                                                        }
                                                    }
                                                });
                                            });

                                            ctx.getSource().sendFeedback(() ->
                                                    Text.literal("Removed ALL rules for " + player), false);

                                            return 1;
                                        })
                                )
                        )
        );
    }

    // ================= 时间解析 =================
    private static long parseTime(String input) {
        if (input == null || input.isEmpty()) return -1;

        input = input.toLowerCase().trim();

        long total = 0;
        StringBuilder num = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                num.append(c);
                continue;
            }

            if (num.isEmpty()) continue;

            long v = Long.parseLong(num.toString());
            num.setLength(0);

            switch (c) {
                case 'd' -> total += v * 24 * 60 * 60 * 1000;
                case 'h' -> total += v * 60 * 60 * 1000;
                case 'm' -> total += v * 60 * 1000;
                case 's' -> total += v * 1000;
            }
        }

        return total == 0 ? -1 : System.currentTimeMillis() + total;
    }

    // ================= 生物显示（中文 + 英文） =================
    private static String getMobDisplay(String id) {
        return MobNameMap.getDisplay(id);
    }
}