package crab.mods.crabsspelllbooks.entity.goals;

import com.github.L_Ender.cataclysm.entity.projectile.Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MiniLaserAtPlayer extends Goal {

    private final Mob mob;
    private final double attackRangeSqr;
    private final int cooldownTicks;

    // Internal cooldown so any generic mob doesn't spam this goal continuously
    private int internalCooldown = 0;
    private int attackTimer;

    public MiniLaserAtPlayer(Mob mob, double attackRange, int cooldownTicks) {
        this.mob = mob;
        this.attackRangeSqr = attackRange * attackRange;
        this.cooldownTicks = cooldownTicks;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // If the goal is still on cooldown, it cannot be used
        if (this.internalCooldown > 0) {
            return false;
        }

        LivingEntity target = this.mob.getTarget();
        if (target instanceof Player player && player.isAlive() && !player.isSpectator()) {
            return this.mob.distanceToSqr(player) <= this.attackRangeSqr
                    && this.mob.getSensing().hasLineOfSight(player);
        }
        return false;
    }

    @Override
    public void start() {
        this.attackTimer = 0;
    }

    @Override
    public void tick() {
        // Count down internal cooldown if ticking outside active use
        if (this.internalCooldown > 0) {
            this.internalCooldown--;
        }

        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        this.attackTimer++;
        if (this.attackTimer >= this.cooldownTicks) {
            if (!this.mob.level().isClientSide) {
                shootLaserBeams(target);
            }
            this.attackTimer = 0;

            // Trigger internal cooldown after firing
            this.internalCooldown = 40; // Adjust recovery ticks between mini-laser bursts if desired
        }
    }

    private void shootLaserBeams(LivingEntity target) {
        Vec3 spawnPos = this.mob.getEyePosition();
        Vec3 targetPos = new Vec3(target.getX(), target.getY(0.5D), target.getZ());
        Vec3 baseDir = targetPos.subtract(spawnPos).normalize();

        float beamRange = 32.0F;
        float spread = 0.08F; // small angular spread

        // Center + left + right
        shootOne(spawnPos, baseDir, beamRange);
        shootOne(spawnPos, offsetDirection(baseDir, spread, 0), beamRange);
        shootOne(spawnPos, offsetDirection(baseDir, -spread, 0), beamRange);
    }

    private void shootOne(Vec3 spawnPos, Vec3 direction, float range) {
        Laser_Beam_Entity laser = new Laser_Beam_Entity(
                ModEntities.LASER_BEAM.get(),
                this.mob,
                spawnPos.x,
                spawnPos.y,
                spawnPos.z,
                direction,
                range,
                this.mob.level()
        );
        this.mob.level().addFreshEntity(laser);
    }

    private Vec3 offsetDirection(Vec3 dir, float yawOffset, float pitchOffset) {
        // Simple perpendicular offset
        Vec3 right = new Vec3(-dir.z, 0, dir.x).normalize();
        Vec3 up = new Vec3(0, 1, 0);
        return dir.add(right.scale(yawOffset)).add(up.scale(pitchOffset)).normalize();
    }
}