package crab.mods.crabsspelllbooks.entity.mobs.goals;

import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LastPrismGoal extends Goal {

    private final Mob mob;
    private final double attackRangeSqr;
    private final int cooldownTicks;

    private int attackTimer;
    private final List<Death_Laser_Beam_Entity> beams = new ArrayList<>();
    private final List<ArmorStand> beamCasters = new ArrayList<>();

    private static final int CONVERGE_DURATION = 50;
    private static final int HOLD_DURATION = 120;
    private static final float SWEEP_SPEED = 2.5F;

    private static final float BEAM_DAMAGE = 5.0F;
    private static final float BEAM_HP_DAMAGE = 0.05F;

    public LastPrismGoal(Mob mob, double attackRange, int extraCooldownTicks) {
        this.mob = mob;
        this.attackRangeSqr = attackRange * attackRange;
        this.cooldownTicks = CONVERGE_DURATION + HOLD_DURATION + extraCooldownTicks;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
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
        this.beams.clear();
        this.beamCasters.clear();
        this.mob.level().playSound(null, this.mob,
                ModSounds.HARBINGER_DEATHLASER_PREPARE.get(),
                SoundSource.HOSTILE, 8.0F, 1.5F);
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();

        this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);
        this.attackTimer++;

        if (this.attackTimer == 1 && !this.mob.level().isClientSide) {
            EntityType<? extends Death_Laser_Beam_Entity> beamType =
                    (EntityType<? extends Death_Laser_Beam_Entity>) (Object) ModEntities.DEATH_LASER_BEAM.get();

            int totalLife = CONVERGE_DURATION + HOLD_DURATION + 40;

            for (int i = 0; i < 6; i++) {
                AlliedArmorStand caster = new AlliedArmorStand(this.mob.level(), this.mob);
                caster.setPos(this.mob.getX(), this.mob.getY() + 1.8D, this.mob.getZ());
                caster.setInvisible(true);
                caster.setNoGravity(true);
                caster.setSilent(true);
                caster.setInvulnerable(true);
                caster.setCustomName(this.mob.getDisplayName());
                this.mob.level().addFreshEntity(caster);
                this.beamCasters.add(caster);

                Death_Laser_Beam_Entity beam = new Death_Laser_Beam_Entity(
                        beamType,
                        this.mob.level(),
                        caster,
                        this.mob.getX(),
                        this.mob.getY() + 1.8D,
                        this.mob.getZ(),
                        0.0F, 0.0F,
                        totalLife,
                        BEAM_DAMAGE,
                        BEAM_HP_DAMAGE
                );

                // Ensure the beam credits your mob as the owner/shooter if the method exists
                // beam.setOwner(this.mob);

                beam.setFire(false);
                this.mob.level().addFreshEntity(beam);
                this.beams.add(beam);
            }
        }

        if (target != null) {
            double dX = target.getX() - this.mob.getX();
            double dY = (target.getY() + target.getBbHeight() / 2.0D) - (this.mob.getY() + 1.8D);
            double dZ = target.getZ() - this.mob.getZ();
            double horizontalDist = Mth.sqrt((float) (dX * dX + dZ * dZ));

            float targetYaw = (float) (Mth.atan2(dZ, dX) * (180.0D / Math.PI)) - 90.0F;
            float targetPitch = (float) (-(Mth.atan2(dY, horizontalDist) * (180.0D / Math.PI)));

            this.mob.setYRot(Mth.approachDegrees(this.mob.getYRot(), targetYaw, SWEEP_SPEED));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.setXRot(Mth.approachDegrees(this.mob.getXRot(), targetPitch, SWEEP_SPEED));
        }

        float baseYaw = this.mob.yHeadRot;
        float basePitch = this.mob.getXRot();

        if (!this.mob.level().isClientSide && this.attackTimer <= CONVERGE_DURATION + HOLD_DURATION) {
            float spread;
            if (this.attackTimer <= CONVERGE_DURATION) {
                float progress = (float) this.attackTimer / CONVERGE_DURATION;
                spread = 90.0F * (1.0F - progress);
            } else {
                spread = 0.0F;
            }

            for (int i = 0; i < this.beams.size() && i < this.beamCasters.size(); i++) {
                Death_Laser_Beam_Entity beam = this.beams.get(i);
                ArmorStand caster = this.beamCasters.get(i);

                if (beam == null || !beam.isAlive() || caster == null || !caster.isAlive()) continue;

                double angle = (Math.PI * 2.0 / 6.0) * i + (this.attackTimer * 0.12);
                float yawOffset = (float) (Math.cos(angle) * spread);
                float pitchOffset = (float) (Math.sin(angle) * spread);

                caster.setYRot(baseYaw + yawOffset);
                caster.yHeadRot = baseYaw + yawOffset;
                caster.yBodyRot = baseYaw + yawOffset;
                caster.setXRot(basePitch + pitchOffset);
                caster.setPos(this.mob.getX(), this.mob.getY() + 1.8D, this.mob.getZ());

                float yawRad = (float) ((caster.yHeadRot + 90.0F) * Math.PI / 180.0D);
                float pitchRad = (float) (-caster.getXRot() * Math.PI / 180.0D);
                beam.setYaw(yawRad);
                beam.setPitch(pitchRad);
                beam.setPos(this.mob.getX(), this.mob.getY() + 1.8D, this.mob.getZ());
            }
        }

        if (this.attackTimer >= this.cooldownTicks) {
            this.stop();
        }
    }

    @Override
    public void stop() {
        for (Death_Laser_Beam_Entity beam : this.beams) {
            if (beam != null && beam.isAlive()) beam.discard();
        }
        this.beams.clear();

        for (ArmorStand caster : this.beamCasters) {
            if (caster != null && caster.isAlive()) caster.discard();
        }
        this.beamCasters.clear();

        this.attackTimer = 0;
    }

    private static class AlliedArmorStand extends ArmorStand {
        private final LivingEntity owner;

        public AlliedArmorStand(net.minecraft.world.level.Level level, LivingEntity owner) {
            super(EntityType.ARMOR_STAND, level);
            this.owner = owner;
        }

        @Override
        public boolean isAlliedTo(Entity other) {
            return other == owner || super.isAlliedTo(other);
        }
    }
}