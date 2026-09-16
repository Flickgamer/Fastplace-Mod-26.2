package club.notben.fastplace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Same settings the original module exposed, saved as JSON at
 * .minecraft/config/fastplace.json.
 *
 * Delay values are in ticks (1 tick = 1/20th of a second).
 * Vanilla's placement cooldown is 4 ticks, so:
 *   0 = no cooldown at all (fastest)
 *   4 = identical to vanilla (effectively off)
 */
public class FastPlaceConfig {
    public static final FastPlaceConfig instance = new FastPlaceConfig();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("fastplace.json");

    /** Whether the module is currently on. Persisted so it survives restarts. */
    public boolean enabled = false;

    /** Placement cooldown in ticks, 0-4. 0 means no cooldown. */
    public int delay = 0;

    /**
     * When true, FastPlace only applies while you're holding a placeable block.
     * This is what keeps eating and projectile-throwing on their normal timing.
     */
    public boolean blocksOnly = true;

    /** When true, snowballs/eggs/pearls use projectileDelay instead of vanilla timing. */
    public boolean separateProjectileDelay = true;

    /** Placement cooldown in ticks for projectiles, 0-4. */
    public int projectileDelay = 2;

    /** Show FastPlace's chat messages. */
    public boolean chat = true;

    private FastPlaceConfig() {
    }

    public void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            FastPlaceConfig loaded = GSON.fromJson(reader, FastPlaceConfig.class);
            if (loaded != null) {
                this.enabled = loaded.enabled;
                this.delay = clampDelay(loaded.delay);
                this.blocksOnly = loaded.blocksOnly;
                this.separateProjectileDelay = loaded.separateProjectileDelay;
                this.projectileDelay = clampDelay(loaded.projectileDelay);
                this.chat = loaded.chat;
            }
        } catch (IOException e) {
            FastPlaceMod.LOGGER.warn("Failed to load fastplace.json, using defaults", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            FastPlaceMod.LOGGER.warn("Failed to save fastplace.json", e);
        }
    }

    public static int clampDelay(int value) {
        if (value < 0) return 0;
        if (value > 4) return 4;
        return value;
    }
}
