package club.notben.fastplace;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Minecraft keeps its right-click cooldown in a private int field on the
 * Minecraft class. Vanilla sets it to 4 after each placement and counts it
 * down one per tick; while it's above 0, no further placement happens.
 * FastPlace works by writing a smaller number into it.
 *
 * The original 1.8.9 module reached this field with Forge's ReflectionHelper,
 * searching for both the obfuscated name ("field_71467_ac") and the readable
 * one ("rightClickDelayTimer"). We do the same thing here - modern Minecraft
 * ships unobfuscated, so the readable name is all we need, but the field has
 * been renamed once historically (rightClickDelayTimer -> rightClickDelay)
 * so we try both, plus a last-resort scan.
 *
 * Using reflection instead of a Mixin keeps this mod's build setup identical
 * to a plain Fabric mod, with no mixin config file to get wrong.
 */
public final class RightClickDelayAccessor {

    private static Field field;
    private static boolean searched = false;
    private static String resolvedName = null;

    private RightClickDelayAccessor() {
    }

    /** Candidate names, newest first. */
    private static final String[] CANDIDATES = {
            "rightClickDelay",        // current Mojang mapping name (1.16+)
            "rightClickDelayTimer",   // older MCP name (1.8 - 1.12 era)
            "field_71467_ac"          // 1.8.9 obfuscated name, harmless to try
    };

    private static void locate() {
        if (searched) {
            return;
        }
        searched = true;

        for (String name : CANDIDATES) {
            try {
                Field f = Minecraft.class.getDeclaredField(name);
                if (f.getType() == int.class) {
                    f.setAccessible(true);
                    field = f;
                    resolvedName = name;
                    FastPlaceMod.LOGGER.info("Found right-click cooldown field: {}", name);
                    return;
                }
            } catch (NoSuchFieldException ignored) {
                // try the next candidate
            } catch (Exception e) {
                FastPlaceMod.LOGGER.warn("Could not access field '{}'", name, e);
            }
        }

        // Nothing matched by name. Log every non-static int field on Minecraft so
        // the right one can be identified quickly if Mojang renames it again.
        StringBuilder sb = new StringBuilder();
        for (Field f : Minecraft.class.getDeclaredFields()) {
            if (f.getType() == int.class && !Modifier.isStatic(f.getModifiers())) {
                sb.append(f.getName()).append(' ');
            }
        }
        FastPlaceMod.LOGGER.error(
                "FastPlace could not find the right-click cooldown field. "
                        + "Candidate int fields on Minecraft are: {}", sb.toString().trim());
    }

    public static boolean isAvailable() {
        locate();
        return field != null;
    }

    public static String getResolvedName() {
        locate();
        return resolvedName;
    }

    public static int get(Minecraft mc) {
        locate();
        if (field == null) {
            return -1;
        }
        try {
            return field.getInt(mc);
        } catch (IllegalAccessException e) {
            return -1;
        }
    }

    public static void set(Minecraft mc, int value) {
        locate();
        if (field == null) {
            return;
        }
        try {
            field.setInt(mc, value);
        } catch (IllegalAccessException ignored) {
            // nothing useful to do per-tick; the error was already logged once
        }
    }
}
