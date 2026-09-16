package club.notben.fastplace;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The actual FastPlace behavior, ported 1:1 from the original module's onTick.
 *
 * Vanilla's cooldown is 4 ticks. The original logic was:
 *   delay == 0 -> force the counter to 0 every tick (no cooldown at all)
 *   delay == 4 -> do nothing (same as vanilla)
 *   otherwise  -> whenever vanilla has just set the counter to 4, overwrite it
 *                 with the smaller chosen value
 */
public final class FastPlaceHandler {

    /** Vanilla's placement cooldown, in ticks. */
    private static final int VANILLA_DELAY = 4;

    private FastPlaceHandler() {
    }

    public static void onTick(Minecraft mc) {
        FastPlaceConfig config = FastPlaceConfig.instance;

        if (!config.enabled || mc.player == null || mc.level == null) {
            return;
        }
        // Don't interfere while a screen (inventory, chat, pause menu) is open.
        if (mc.gui.screen() != null) {
            return;
        }
        if (!RightClickDelayAccessor.isAvailable()) {
            return;
        }

        ItemStack held = mc.player.getMainHandItem();

        if (!config.blocksOnly) {
            // Applies to everything you can right-click with.
            applyDelay(mc, config.delay);
            return;
        }

        if (held.isEmpty()) {
            return;
        }

        if (held.getItem() instanceof BlockItem) {
            applyDelay(mc, config.delay);
        } else if (config.separateProjectileDelay && isProjectile(held.getItem())) {
            applyDelay(mc, config.projectileDelay);
        }
        // Anything else - food, potions, bows, buckets, etc. - is left completely
        // alone, which is what keeps eating and normal item use feeling vanilla.
    }

    private static boolean isProjectile(Item item) {
        return item == Items.SNOWBALL
                || item == Items.EGG
                || item == Items.ENDER_PEARL;
    }

    private static void applyDelay(Minecraft mc, int desiredDelay) {
        if (desiredDelay >= VANILLA_DELAY) {
            return; // nothing to do, this is vanilla behavior
        }

        if (desiredDelay <= 0) {
            RightClickDelayAccessor.set(mc, 0);
            return;
        }

        // Only step in right after vanilla has reset the counter, so we shorten
        // the cooldown rather than continuously holding it at a fixed value.
        if (RightClickDelayAccessor.get(mc) == VANILLA_DELAY) {
            RightClickDelayAccessor.set(mc, desiredDelay);
        }
    }
}
