package club.notben.fastplace;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class FastPlaceClient implements ClientModInitializer {

    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        FastPlaceConfig.instance.load();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(FastPlaceMod.MOD_ID, "main"));

        // Default is J - picked to avoid clashing with SafeWalk's H if you run both.
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.fastplace.toggle",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_J,
                category
        ));

        FastPlaceCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register(FastPlaceClient::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null) {
            return;
        }

        while (toggleKey.consumeClick()) {
            FastPlaceConfig.instance.enabled = !FastPlaceConfig.instance.enabled;
            FastPlaceConfig.instance.save();
            sendToggle("FastPlace", FastPlaceConfig.instance.enabled);
        }

        FastPlaceHandler.onTick(client);
    }

    public static void sendChat(String message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        mc.player.sendSystemMessage(Component.literal(message));
    }

    public static void sendToggle(String name, boolean on) {
        if (!FastPlaceConfig.instance.chat) {
            return;
        }
        sendChat(ChatFormatting.RESET + "[" + ChatFormatting.LIGHT_PURPLE + name + ChatFormatting.RESET + "]"
                + ChatFormatting.YELLOW + " toggled "
                + (on ? ChatFormatting.GREEN + "on" : ChatFormatting.RED + "off"));
    }
}
