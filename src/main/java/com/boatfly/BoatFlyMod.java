package com.boatfly;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Boat Fly Mod - Allows players to fly in boats.
 * Controls:
 *   Space (Jump)  - Ascend
 *   Shift (Sneak) - Descend
 * Compatible with vanilla Minecraft, OptiFine, and Sodium.
 * No server-side installation required.
 */
public class BoatFlyMod implements ModInitializer {

    public static final String MOD_ID = "boatfly";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[BoatFly] Boat Fly Mod loaded! Space to ascend, Shift to descend. Press Ctrl+Alt+Shift+1 to toggle.");
    }
}
