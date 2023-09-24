package me.rufia.fightorflight;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CobblemonFightOrFlight implements ModInitializer {
    public static final String MODID = "fightorflight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world from Fight or Flight!");
    }
}
