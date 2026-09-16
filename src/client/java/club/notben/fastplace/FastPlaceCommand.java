package club.notben.fastplace;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public final class FastPlaceCommand {
    private FastPlaceCommand() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(FastPlaceCommand::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                         CommandBuildContext ctx) {
        for (String alias : new String[]{"fastplace", "fp"}) {
            LiteralArgumentBuilder<FabricClientCommandSource> root = literal(alias)
                    .executes(c -> {
                        sendStatus();
                        return 1;
                    })
                    .then(literal("help").executes(c -> {
                        sendHelp();
                        return 1;
                    }))
                    .then(literal("toggle").executes(c -> {
                        FastPlaceConfig.instance.enabled = !FastPlaceConfig.instance.enabled;
                        FastPlaceConfig.instance.save();
                        FastPlaceClient.sendToggle("FastPlace", FastPlaceConfig.instance.enabled);
                        return 1;
                    }))
                    .then(literal("on").executes(c -> {
                        FastPlaceConfig.instance.enabled = true;
                        FastPlaceConfig.instance.save();
                        FastPlaceClient.sendToggle("FastPlace", true);
                        return 1;
                    }))
                    .then(literal("off").executes(c -> {
                        FastPlaceConfig.instance.enabled = false;
                        FastPlaceConfig.instance.save();
                        FastPlaceClient.sendToggle("FastPlace", false);
                        return 1;
                    }))
                    .then(literal("delay")
                            .then(argument("ticks", IntegerArgumentType.integer(0, 4))
                                    .executes(c -> {
                                        int v = IntegerArgumentType.getInteger(c, "ticks");
                                        FastPlaceConfig.instance.delay = v;
                                        FastPlaceConfig.instance.save();
                                        FastPlaceClient.sendChat(ChatFormatting.YELLOW
                                                + "Block placement delay set to " + ChatFormatting.GREEN + v
                                                + ChatFormatting.YELLOW + " tick(s)"
                                                + (v == 4 ? " (same as vanilla)" : ""));
                                        return 1;
                                    })))
                    .then(literal("projectiledelay")
                            .then(argument("ticks", IntegerArgumentType.integer(0, 4))
                                    .executes(c -> {
                                        int v = IntegerArgumentType.getInteger(c, "ticks");
                                        FastPlaceConfig.instance.projectileDelay = v;
                                        FastPlaceConfig.instance.save();
                                        FastPlaceClient.sendChat(ChatFormatting.YELLOW
                                                + "Projectile delay set to " + ChatFormatting.GREEN + v
                                                + ChatFormatting.YELLOW + " tick(s)");
                                        return 1;
                                    })))
                    .then(literal("blocksonly")
                            .executes(c -> {
                                FastPlaceConfig.instance.blocksOnly = !FastPlaceConfig.instance.blocksOnly;
                                FastPlaceConfig.instance.save();
                                FastPlaceClient.sendToggle("Blocks only", FastPlaceConfig.instance.blocksOnly);
                                return 1;
                            })
                            .then(argument("value", BoolArgumentType.bool())
                                    .executes(c -> {
                                        FastPlaceConfig.instance.blocksOnly = BoolArgumentType.getBool(c, "value");
                                        FastPlaceConfig.instance.save();
                                        FastPlaceClient.sendToggle("Blocks only", FastPlaceConfig.instance.blocksOnly);
                                        return 1;
                                    })))
                    .then(literal("projectiles")
                            .executes(c -> {
                                FastPlaceConfig.instance.separateProjectileDelay =
                                        !FastPlaceConfig.instance.separateProjectileDelay;
                                FastPlaceConfig.instance.save();
                                FastPlaceClient.sendToggle("Separate projectile delay",
                                        FastPlaceConfig.instance.separateProjectileDelay);
                                return 1;
                            }))
                    .then(literal("chat")
                            .executes(c -> {
                                FastPlaceConfig.instance.chat = !FastPlaceConfig.instance.chat;
                                FastPlaceConfig.instance.save();
                                FastPlaceClient.sendChat(ChatFormatting.YELLOW + "Chat messages "
                                        + (FastPlaceConfig.instance.chat
                                        ? ChatFormatting.GREEN + "on" : ChatFormatting.RED + "off"));
                                return 1;
                            }));

            dispatcher.register(root);
        }
    }

    private static void sendStatus() {
        FastPlaceConfig cfg = FastPlaceConfig.instance;
        FastPlaceClient.sendChat(ChatFormatting.GRAY + "--------" + ChatFormatting.WHITE + " FastPlace "
                + FastPlaceMod.VERSION + ChatFormatting.GRAY + " --------");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "Enabled: "
                + (cfg.enabled ? ChatFormatting.GREEN + "yes" : ChatFormatting.RED + "no"));
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "Block delay: " + ChatFormatting.GOLD + cfg.delay + " tick(s)");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "Blocks only: "
                + (cfg.blocksOnly ? ChatFormatting.GREEN + "yes" : ChatFormatting.RED + "no"));
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "Separate projectile delay: "
                + (cfg.separateProjectileDelay ? ChatFormatting.GREEN + "yes" : ChatFormatting.RED + "no")
                + ChatFormatting.AQUA + " (" + ChatFormatting.GOLD + cfg.projectileDelay
                + ChatFormatting.AQUA + " tick(s))");
        String fieldName = RightClickDelayAccessor.getResolvedName();
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "Cooldown field: "
                + (fieldName != null
                ? ChatFormatting.GREEN + fieldName
                : ChatFormatting.RED + "NOT FOUND - see latest.log"));
    }

    private static void sendHelp() {
        FastPlaceClient.sendChat(ChatFormatting.GRAY + "--------" + ChatFormatting.WHITE + " FastPlace "
                + FastPlaceMod.VERSION + ChatFormatting.GRAY + " --------");
        FastPlaceClient.sendChat(ChatFormatting.RED + "Aliases: /fastplace, /fp   |   Toggle key: J");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp" + ChatFormatting.GOLD + " - show current settings");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp toggle|on|off" + ChatFormatting.GOLD + " - turn it on or off");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp delay <0-4>" + ChatFormatting.GOLD
                + " - block cooldown in ticks (0 = fastest, 4 = vanilla)");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp blocksonly" + ChatFormatting.GOLD
                + " - only speed up blocks, leave eating/projectiles vanilla");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp projectiles" + ChatFormatting.GOLD
                + " - toggle separate projectile delay");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp projectiledelay <0-4>" + ChatFormatting.GOLD
                + " - projectile cooldown in ticks");
        FastPlaceClient.sendChat(ChatFormatting.AQUA + "/fp chat" + ChatFormatting.GOLD + " - toggle these messages");
    }
}
