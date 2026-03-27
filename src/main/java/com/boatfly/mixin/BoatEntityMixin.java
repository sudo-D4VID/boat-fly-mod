package com.boatfly.mixin;

import com.boatfly.BoatFlyConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
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
 * Client-side only. On vanilla servers the server is lenient about boat
 * position updates sent by the controlling player, so flying works in
 * multiplayer without a server-side installation.
 *
 * Toggle: Ctrl+Alt+Shift+1 (configurable via {@link BoatFlyConfig#flyEnabled}).
 *
 * Compatibility:
 *  - Sodium:   rendering-only mod, no conflict.
 *  - OptiFine: does not touch BoatEntity, no conflict.
 *  - Vanilla:  standard Fabric mixin, no conflict.
 */
@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {

    /** Upward velocity added per tick when Space is held (blocks/tick). */
    private static final double FLY_ACCEL = 0.08;

    /** Downward velocity added per tick when Shift is held (blocks/tick). */
    private static final double DESCEND_ACCEL = 0.05;

    /** Vertical speed ceiling in either direction (blocks/tick). */
    private static final double MAX_VERT_SPEED = 0.6;

    /**
     * Multiplier applied to vertical velocity each tick while airborne and
     * no vertical key is pressed. Values below 1.0 bleed off momentum,
     * creating a soft hover instead of free-fall.
     */
    private static final double HOVER_FRICTION = 0.8;

    /**
     * Constant subtracted from vertical velocity each tick while hovering
     * so the boat drifts very slowly downward instead of floating forever.
     */
    private static final double HOVER_GRAVITY = 0.02;

    /**
     * Shadowed from {@link net.minecraft.entity.Entity}.
     * Reset to zero whenever the mod controls vertical movement so that
     * touching down after flying never triggers fall damage.
     */
    @Shadow
    protected float fallDistance;

    /**
     * Injected at the end of {@code BoatEntity#tick()} to apply fly
     * mechanics. Using {@code RETURN} ensures vanilla physics run first;
     * we then override only the vertical component as needed.
     */
    @Inject(method = "tick", at = @At("RETURN"))
    private void boatFlyTick(CallbackInfo ci) {
        // Respect the runtime toggle (Ctrl+Alt+Shift+1)
        if (!BoatFlyConfig.flyEnabled) return;

        BoatEntity self = (BoatEntity) (Object) this;

        // Only run on the logical client — server instances are ignored
        if (!self.getWorld().isClient) return;

        // Retrieve the player once and reuse the reference
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        // Bail out if there is no local player or they are not in this boat
        if (player == null || player.getVehicle() != self) return;

        // Cache velocity — Vec3d is immutable, so one read is sufficient
        Vec3d velocity = self.getVelocity();

        if (client.options.jumpKey.isPressed()) {
            // Ascend: push vertical velocity upward, capped at MAX_VERT_SPEED
            self.setVelocity(velocity.x, Math.min(velocity.y + FLY_ACCEL, MAX_VERT_SPEED), velocity.z);
            // Zero out fall distance so landing does not deal fall damage
            fallDistance = 0.0F;

        } else if (client.options.sneakKey.isPressed()) {
            // Descend: pull vertical velocity downward, capped at -MAX_VERT_SPEED
            self.setVelocity(velocity.x, Math.max(velocity.y - DESCEND_ACCEL, -MAX_VERT_SPEED), velocity.z);

        } else if (!self.isOnGround() && !self.isTouchingWater()) {
            // Hover: dampen vertical motion to resist gravity while airborne
            self.setVelocity(velocity.x, velocity.y * HOVER_FRICTION - HOVER_GRAVITY, velocity.z);
            // Keep fall distance at zero so players can descend and land safely
            fallDistance = 0.0F;
        }
    }
}
