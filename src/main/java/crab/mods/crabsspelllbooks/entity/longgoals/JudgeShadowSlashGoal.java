package crab.mods.crabsspelllbooks.entity.longgoals;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.mixin.AbstractArrowAccessor;
import io.redspace.ironsspellbooks.particle.EnderSlashParticleOptions;
import io.redspace.ironsspellbooks.particle.TraceParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.EnumSet;

public class JudgeShadowSlashGoal extends Goal {
        private final Mob judge;
        private final float attackDamage;
        private int cooldown = 0;
        private int attackTicks = 0;

        public JudgeShadowSlashGoal(Mob judge, float attackDamage) { // Update constructor parameter too
            this.judge = judge;
            this.attackDamage = attackDamage;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        LivingEntity target = this.judge.getTarget();
        return target != null && target.isAlive() && this.judge.distanceToSqr(target) <= 144.0D; // 12 blocks squared
    }

    @Override
    public void start() {
        this.attackTicks = 0;
        if (this.judge.getTarget() != null) {
            this.judge.getLookControl().setLookAt(this.judge.getTarget(), 30.0F, 30.0F);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.judge.getTarget();
        if (target != null) {
            this.judge.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        // Execute the slash on tick 5 (allows animation startup time)
        if (this.attackTicks == 5) {
            performShadowSlash();
        }

        this.attackTicks++;
    }

    @Override
    public boolean canContinueToUse() {
        return this.attackTicks < 15 && this.judge.getTarget() != null;
    }

    @Override
    public void stop() {
        this.cooldown = 100; // 5 second cooldown between slashes
    }

    private void performShadowSlash() {
        var level = this.judge.level();
        if (level.isClientSide) return;

        // Play cast/slash sound at Judge position
        level.playSound(null, this.judge.getX(), this.judge.getY(), this.judge.getZ(),
                SoundRegistry.SHADOW_SLASH.get(), this.judge.getSoundSource(), 1.0f, 1.0f);

        float distance = 12f;
        Vec3 forward = this.judge.getForward();
        Vec3 end = Utils.raycastForBlock(level, this.judge.getEyePosition(),
                this.judge.getEyePosition().add(forward.scale(distance)), ClipContext.Fluid.NONE).getLocation();

        AABB hitbox = this.judge.getBoundingBox().expandTowards(end.subtract(this.judge.getEyePosition())).inflate(2);

        var targetableEntities = level.getEntities(this.judge, hitbox, e ->
                !e.isSpectator() &&
                        (e instanceof LivingEntity || e instanceof Projectile) &&
                        e.getBoundingBox().getCenter().subtract(this.judge.getBoundingBox().getCenter()).normalize().dot(this.judge.getForward()) >= 0.85);

        targetableEntities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this.judge)));

        if (!targetableEntities.isEmpty() && targetableEntities.get(0).distanceToSqr(this.judge) < distance * distance) {
            var closestEntity = targetableEntities.get(0);

            float radius = 2.5f;
            AABB damageBox = AABB.ofSize(closestEntity.getBoundingBox().getCenter(), radius, radius + 1, radius)
                    .move(forward.scale(radius / 2));

            end = damageBox.getCenter().add(end).scale(0.5);
            var damageEntities = level.getEntities(this.judge, damageBox);
            var damageSource = level.damageSources().mobAttack(this.judge);

            boolean projectileEffects = false;

            for (Entity targetEntity : damageEntities) {
                // Deflect projectiles away from the Judge
                if (targetEntity instanceof Projectile projectile &&
                        !projectile.noPhysics &&
                        !(projectile instanceof AbstractArrow arrow && ((AbstractArrowAccessor) arrow).isInGround()) &&
                        !projectile.getType().is(ModTags.CANT_PARRY)) {

                    projectileEffects = true;
                    projectile.setOwner(this.judge);
                    projectile.shoot(forward.x, forward.y, forward.z, (float) projectile.getDeltaMovement().length(), 0f);

                } else if (targetEntity.isAlive() &&
                        this.judge.isPickable() &&
                        Utils.hasLineOfSight(level, this.judge.getEyePosition(), targetEntity.getBoundingBox().getCenter(), true)) {

                    if (DamageSources.applyDamage(targetEntity, this.attackDamage, damageSource)) {
                        MagicManager.spawnParticles(level, ParticleHelper.ENDER_SPARKS,
                                targetEntity.getX(), targetEntity.getY() + targetEntity.getBbHeight() * 0.5f, targetEntity.getZ(),
                                15, targetEntity.getBbWidth() * 0.5f, targetEntity.getBbHeight() * 0.5f, targetEntity.getBbWidth() * 0.5f, 0.25, false);


                        // Knockback logic
                        Vec3 knockback = targetEntity.position().subtract(this.judge.position()).normalize().add(0, 0.5, 0).normalize();
                        knockback = knockback.scale(Utils.random.nextIntBetweenInclusive(70, 100) / 100f *
                                Utils.clampedKnockbackResistanceFactor(targetEntity, 0.2f, 1f) * 0.1f);
                        targetEntity.setDeltaMovement(targetEntity.getDeltaMovement().add(knockback));

                        targetEntity.hurtMarked = true;
                    }
                }
            }

            if (projectileEffects) {
                level.playSound(null, closestEntity.getX(), closestEntity.getY(), closestEntity.getZ(),
                        SoundRegistry.FIRE_DAGGER_PARRY.get(), this.judge.getSoundSource(), 1.0F, 1.0F);
                MagicManager.spawnParticles(level, ParticleHelper.ENDER_SPARKS,
                        closestEntity.getX(), closestEntity.getY() + closestEntity.getBbHeight() * 0.5f, closestEntity.getZ(),
                        25, 0, 0, 0, 0.4, false);
            }
        }

        Vec3 rayVector = end.subtract(this.judge.getEyePosition());
        Vec3 impulse = rayVector.scale(1 / 6f).add(0, 0.1, 0);
        this.judge.setDeltaMovement(this.judge.getDeltaMovement().scale(0.2).add(impulse));
        this.judge.hurtMarked = true;

        // Spawn Visual Slash Mesh
        forward = impulse.normalize();
        Vec3 up = new Vec3(0, 1, 0);
        if (forward.dot(up) > 0.999) {
            up = new Vec3(1, 0, 0);
        }
        Vec3 right = up.cross(forward);
        Vec3 particlePos = end.subtract(forward.scale(3)).add(right.scale(-0.3));

        MagicManager.spawnParticles(level,
                new EnderSlashParticleOptions((float) forward.x, (float) forward.y, (float) forward.z,
                        (float) right.x, (float) right.y, (float) right.z, 1f),
                particlePos.x, particlePos.y + 0.3, particlePos.z, 1, 0, 0, 0, 0, true);

        // Spawn Trail Particles
        int trailParticles = 15;
        double speed = rayVector.length() / 12.0 * 0.75;
        for (int i = 0; i < trailParticles; i++) {
            Vec3 particleStart = this.judge.getBoundingBox().getCenter().add(Utils.getRandomVec3(1 + this.judge.getBbWidth()));
            Vec3 particleEnd = particleStart.add(rayVector);
            MagicManager.spawnParticles(level,
                    new TraceParticleOptions(Utils.v3f(particleEnd), new Vector3f(1f, 0.333f, 1f)),
                    particleStart.x, particleStart.y, particleStart.z, 1, 0, 0, 0, speed, false);
        }
    }
}