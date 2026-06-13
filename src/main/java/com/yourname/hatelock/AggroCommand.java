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

                // ================= toggle =================
                .then(literal("toggle")
                        .executes(ctx -> {
                            boolean state = AggroHandler.toggle();
                            ctx.getSource().sendFeedback(
                                    () -> Text.literal("Aggro: " + (state ? "ON" : "OFF")),
                                    false
                            );
                            return 1;
                        })
                )

                // ================= radius =================
                .then(literal("radius")
                        .then(argument("value", IntegerArgumentType.integer(1, 512))
                                .executes(ctx -> {
                                    int r = IntegerArgumentType.getInteger(ctx, "value");
                                    AggroHandler.setRadius(r);

                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("Radius set: " + r),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )

                // ================= list =================
                .then(literal("list")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(AggroHandler::listRules, false);
                            return 1;
                        })
                )

                // ================= set（恢复补全） =================
                .then(literal("set")
                        .then(argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> {

                                    // ✔ 玩家补全
                                    ctx.getSource().getServer()
                                            .getPlayerManager()
                                            .getPlayerList()
                                            .forEach(p -> builder.suggest(p.getName().getString()));

                                    return builder.buildFuture();
                                })

                                .then(argument("mobId", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {

                                            // ✔ 生物补全 + 中文提示
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

                                        .executes(ctx -> {

                                            String player = StringArgumentType.getString(ctx, "player");
                                            String mobId = StringArgumentType.getString(ctx, "mobId");

                                            AggroHandler.addRule(player, mobId, -1);

                                            ctx.getSource().sendFeedback(
                                                    () -> Text.literal("Added: " + player + " -> " + mobId),
                                                    false
                                            );
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
                                                    .forEach(r -> builder.suggest(
                                                            r.mobId,
                                                            Text.literal(MobNameMap.getDisplay(r.mobId))
                                                    ));

                                            return builder.buildFuture();
                                        })

                                        .executes(ctx -> {

                                            String p = StringArgumentType.getString(ctx, "player");
                                            String m = StringArgumentType.getString(ctx, "mobId");

                                            boolean ok = AggroHandler.removeRule(p, m);

                                            ctx.getSource().sendFeedback(
                                                    () -> Text.literal(ok ? "Removed" : "Not found"),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}