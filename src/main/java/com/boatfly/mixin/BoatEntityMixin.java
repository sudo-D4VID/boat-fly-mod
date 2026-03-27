package com.boatfly.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into BoatEntity to enable flying mechanics.
 *
 * This is a client-side only mixin. On vanilla servers the server is lenient
 * about boat position updates sent by the controlling player, so the flying
 * works in multiplayer without requiring a server-side mod installation.
 *
 * Compatibility notes:
 *  - Sodium: only touches rendering, no conflict.
 *  - OptiFine: does not modify BoatEntity, no conflict.
 *  - Vanilla: standard Fabric mixin, no conflict.
 */
@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {

    /** Speed gained per tick when pressing Jump (Space). */
    private static final double FLY_ACCEL = 0.08;

    /** Speed gained per tick when pressing Sneak (Shift). */
    private static final double DESCEND_ACCEL = 0.05;

    /** Maximum vertical speed cap (blocks/tick). */
    private static final double MAX_VERT_SPEED = 0.6;

    /** Vertical friction applied when hovering airborne. */
    private static final double HOVER_FRICTION = 0.8;

    /** Gravity counter applied when hovering airborne (slight descent). */
    private static final double HOVER_GRAVITY = 0.02;

    @Shadow
    protected float fallDistance;

    /**
     * Injected at the end of BoatEntity#tick() to apply fly mechanics.
     */
    @Inject(method = "tick", at = @At("RETURN"))
    private void boatFlyTick(CallbackInfo ci) {
        BoatEntity self = (BoatEntity) (Object) this;

        // Only run on the logical client - avoids any server-side interference
        if (!self.getWorld().isClient) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Only apply to the boat the local player is currently riding
        if (client.player.getVehicle() != self) return;

        Vec3d velocity = self.getVelocity();
        boolean airborne = !self.isOnGround() && !self.isTouchingWater();

        if (client.options.jumpKey.isPressed()) {
            // Ascend: add upward velocity capped at MAX_VERT_SPEED
            self.setVelocity(velocity.x,
                    Math.min(velocity.y + FLY_ACCEL, MAX_VERT_SPEED),
                    velocity.z);
            // Reset fall distance so landing doesn't deal fall damage
            fallDistance = 0.0F;
        } else if (client.options.sneakKey.isPressed()) {
            // Descend: subtract from vertical velocity, capped at -MAX_VERT_SPEED
            self.setVelocity(velocity.x,
                    Math.max(velocity.y - DESCEND_ACCEL, -MAX_VERT_SPEED),
                    velocity.z);
        } else if (airborne) {
            // Hover: slow the boat's vertical motion instead of free-falling
            self.setVelocity(velocity.x,
                    velocity.y * HOVER_FRICTION - HOVER_GRAVITY,
                    velocity.z);
            // Prevent accumulation of fall distance while hovering
            fallDistance = 0.0F;
        }
    }
}
