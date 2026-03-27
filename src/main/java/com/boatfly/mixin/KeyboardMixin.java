package com.boatfly.mixin;

import com.boatfly.BoatFlyConfig;
import com.boatfly.BoatFlyMod;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts raw GLFW key events so we can detect the user-configured
 * toggle combination (default: Ctrl+Alt+Shift+1) without depending on
 * Fabric API's KeyBindingHelper.
 *
 * The check uses a bitmask instead of exact equality so that Caps Lock /
 * Num Lock state does not accidentally block the combo.
 */
@Mixin(Keyboard.class)
public class KeyboardMixin {

    /** The three modifier keys that must all be held for the toggle. */
    private static final int TOGGLE_MODS =
            GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT | GLFW.GLFW_MOD_SHIFT;

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onBoatFlyToggle(long window, int key, int scancode,
                                 int action, int modifiers, CallbackInfo ci) {
        // Only react on key-press events (ignore repeats and releases)
        if (action != GLFW.GLFW_PRESS) return;
        // Key 1 must be pressed
        if (key != GLFW.GLFW_KEY_1) return;
        // All three required modifiers must be held; ignore Caps/Num-Lock bits
        if ((modifiers & TOGGLE_MODS) != TOGGLE_MODS) return;

        boolean newState = !BoatFlyConfig.flyEnabled.get();
        BoatFlyConfig.flyEnabled.set(newState);
        BoatFlyMod.LOGGER.info("[BoatFly] Flying {}.",
                newState ? "enabled" : "disabled");
    }
}
