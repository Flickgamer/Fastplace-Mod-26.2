package club.notben.fastplace;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint. FastPlace is entirely client-side - it only changes a
 * counter inside your own game client - so there is no server logic here.
 */
public class FastPlaceMod implements ModInitializer {
    public static final String MOD_ID = "fastplace";
    public static final String MOD_NAME = "FastPlace";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("{} {} loaded (common init)", MOD_NAME, VERSION);
    }
}
