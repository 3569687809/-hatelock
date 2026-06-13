package com.yourname.hatelock;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.stream.StreamSupport;

import static net.minecraft.server.command.CommandManager.*;

public class AggroCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(literal("aggro")

                .then(literal("toggle")
                        .executes(ctx -> {
                            boolean state = AggroHandler.toggle();
                            ctx.getSource().sendFeedback(
                                    () -> Text.literal("Aggro: " + state),
                                    false
                            );
                            return 1;
                        })
                )

                .then(literal("reload")
                        .executes(ctx -> {
                            AggroHandler.load();
                            ctx.getSource().sendFeedback(
                                    () -> Text.literal("Reloaded"),
                                    false
                            );
                            return 1;
                        })
                )

                .then(literal("radius")
                        .then(argument("value", IntegerArgumentType.integer(1, 512))
                                .executes(ctx -> {
                                    int r = IntegerArgumentType.getInteger(ctx, "value");
                                    AggroHandler.setRadius(r);
                                    return 1;
                                })
                        )
                )

                .then(literal("list")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(AggroHandler::listRules, false);
                            return 1;
                        })
                )

                // ================= SET（核心修复） =================
                .then(literal("set")
                        .then(argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {

                                    ctx.getSource().getServer()
                                            .getPlayerManager()
                                            .getPlayerList()
                                            .forEach(p -> builder.suggest(p.getName().getString()));

                                    return builder.buildFuture();
                                })

                                .then(argument("mobId", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {

                                            StreamSupport.stream(Registries.ENTITY_TYPE.spliterator(), false)
                                                    .forEach(type -> {

                                                        String id = Registries.ENTITY_TYPE
                                                                .getId(type)
                                                                .getPath();

                                                        builder.suggest(
                                                                id,
                                                                Text.literal(MobNameMap.getDisplay(id))
                                                        );
                                                    });

                                            return builder.buildFuture();
                                        })

                                        // ===== 无时间（永久）=====
                                        .executes(ctx -> {

                                            String player = StringArgumentType.getString(ctx, "player");
                                            String mobId = StringArgumentType.getString(ctx, "mobId");

                                            AggroHandler.addRule(player, mobId, -1);

                                            ctx.getSource().sendFeedback(
                                                    () -> Text.literal("Added PERM: " + player + " -> " + mobId),
                                                    false
                                            );

                                            return 1;
                                        })

                                        // ===== 有时间 =====
                                        .then(argument("time", StringArgumentType.word())
                                                .executes(ctx -> {

                                                    String player = StringArgumentType.getString(ctx, "player");
                                                    String mobId = StringArgumentType.getString(ctx, "mobId");
                                                    String time = StringArgumentType.getString(ctx, "time");

                                                    long expire = parseTime(time);

                                                    AggroHandler.addRule(player, mobId, expire);

                                                    ctx.getSource().sendFeedback(
                                                            () -> Text.literal("Added TEMP: " + player + " -> " + mobId),
                                                            false
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
                        )
                )

                .then(literal("remove")
                        .then(argument("player", StringArgumentType.word())

                                // 玩家补全
                                .suggests((ctx, builder) -> {

                                    AggroHandler.getConfig().rules.stream()
                                            .map(r -> r.player)
                                            .distinct()
                                            .forEach(builder::suggest);

                                    return builder.buildFuture();
                                })

                                // /aggro remove 玩家
                                .executes(ctx -> {

                                    String player =
                                            StringArgumentType.getString(ctx, "player");

                                    int before =
                                            AggroHandler.getConfig().rules.size();

                                    AggroHandler.getConfig().rules.removeIf(
                                            r -> r.player.equalsIgnoreCase(player)
                                    );

                                    AggroHandler.save();

                                    int removed =
                                            before - AggroHandler.getConfig().rules.size();

                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal(
                                                    "Removed " + removed +
                                                            " rules for " + player
                                            ),
                                            false
                                    );

                                    return 1;
                                })

                                // /aggro remove 玩家 怪物
                                .then(argument("mobId", StringArgumentType.word())

                                        // 怪物补全
                                        .suggests((ctx, builder) -> {

                                            String player =
                                                    StringArgumentType.getString(ctx, "player");

                                            AggroHandler.getConfig().rules.stream()
                                                    .filter(r ->
                                                            r.player.equalsIgnoreCase(player))
                                                    .forEach(r ->
                                                            builder.suggest(r.mobId));

                                            return builder.buildFuture();
                                        })

                                        .executes(ctx -> {

                                            String p =
                                                    StringArgumentType.getString(ctx, "player");

                                            String m =
                                                    StringArgumentType.getString(ctx, "mobId");

                                            boolean ok =
                                                    AggroHandler.removeRule(p, m);

                                            ctx.getSource().sendFeedback(
                                                    () -> Text.literal(
                                                            ok ? "Removed" : "Not found"
                                                    ),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                )
        );
    }

    // ================= TIME PARSE =================
    private static long parseTime(String input) {

        if (input == null || input.isEmpty()) return -1;

        try {
            input = input.toLowerCase();

            long total = 0;
            StringBuilder num = new StringBuilder();

            for (char c : input.toCharArray()) {

                if (Character.isDigit(c)) {
                    num.append(c);
                    continue;
                }

                if (num.length() == 0) continue;

                long v = Long.parseLong(num.toString());
                num.setLength(0);

                switch (c) {
                    case 's' -> total += v * 1000;
                    case 'm' -> total += v * 60 * 1000;
                    case 'h' -> total += v * 60 * 60 * 1000;
                    case 'd' -> total += v * 24 * 60 * 60 * 1000;
                }
            }

            return total == 0 ? -1 : System.currentTimeMillis() + total;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}