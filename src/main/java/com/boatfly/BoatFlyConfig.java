package com.boatfly;

/**
 * Runtime configuration for Boat Fly Mod.
 *
 * All fields are {@code static} so both the entity mixin and the keyboard mixin
 * can read/write them without any cross-mixin coupling.
 */
public final class BoatFlyConfig {

    /**
     * Whether the boat-fly feature is currently active.
     *
     * Toggled at runtime by pressing Ctrl+Alt+Shift+1 (default keybind).
     * Both mixins execute on Minecraft's single client thread, so a plain
     * {@code boolean} is sufficient — no synchronization is needed.
     */
    public static boolean flyEnabled = true;

    private BoatFlyConfig() {
        throw new UnsupportedOperationException("BoatFlyConfig is a static utility class");
    }
}
