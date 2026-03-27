package com.boatfly;

import java.util.concurrent.atomic.AtomicBoolean;

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
     * An {@link AtomicBoolean} is used so the toggle's read-modify-write
     * operation is always atomic and well-defined, regardless of any future
     * threading changes in the game engine.
     */
    public static final AtomicBoolean flyEnabled = new AtomicBoolean(true);

    private BoatFlyConfig() {
        throw new UnsupportedOperationException("BoatFlyConfig is a static utility class");
    }
}
