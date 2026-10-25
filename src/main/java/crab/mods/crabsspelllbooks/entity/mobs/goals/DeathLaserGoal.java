package crab.mods.crabsspelllbooks.entity.mobs.goals;

import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class DeathLaserGoal extends Goal {

    private final Mob mob;
    private final double attackRangeSqr;
    private final int cooldownTicks;

    private int attackTimer;
    private Death_Laser_Beam_Entity activeBeam;

    private static final int BEAM_DURATION = 100;
    private static final float SWEEP_SPEED = 3.0F;

    private static final float BEAM_DAMAGE = 5.0F;
    private static final float BEAM_HP_DAMAGE = 0.05F;

    public DeathLaserGoal(Mob mob, double attackRange, int cooldownTicks) {
        this.mob = mob;
        this.attackRangeSqr = attackRange * attackRange;
        this.cooldownTicks = BEAM_DURATION + cooldownTicks;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target instanceof Player player && player.isAlive() && !player.isSpectator()) {
            return this.mob.distanceToSqr(player) <= this.attackRangeSqr && this.mob.getSensing().hasLineOfSight(player);
        }
        return false;
    }

    @Override
    public void start() {
        this.attackTimer = 0;
        this.activeBeam = null;
        this.mob.level().playSound(null, this.mob, ModSounds.HARBINGER_DEATHLASER_PREPARE.get(), SoundSource.HOSTILE, 8.0F, 1.2F);

        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target.getX(), target.getY() - 4.0D, target.getZ(), 180.0F, 180.0F);
            this.mob.lookAt(target, 180.0F, 180.0F);
            this.mob.setXRot(45.0F);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();

        this.mob.setDeltaMovement(0, this.mob.getDeltaMovement().y, 0);

        this.attackTimer++;

        if (this.attackTimer == 1 && !this.mob.level().isClientSide) {
            float yawRad = (float) ((this.mob.yHeadRot + 90.0F) * Math.PI / 180.0D);
            float pitchRad = (float) (-this.mob.getXRot() * Math.PI / 180.0D);

            this.activeBeam = new Death_Laser_Beam_Entity(
                    (EntityType<? extends Death_Laser_Beam_Entity>) (Object) ModEntities.DEATH_LASER_BEAM.get(),
                    this.mob.level(),
                    this.mob,
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    yawRad,
                    pitchRad,
                    BEAM_DURATION,
                    BEAM_DAMAGE,
                    BEAM_HP_DAMAGE
            );

            this.activeBeam.setFire(false);
            this.mob.level().addFreshEntity(this.activeBeam);
        }

        if (target != null) {
            double dX = target.getX() - this.mob.getX();
            double dY = (target.getY() + target.getBbHeight() / 2.0D) - (this.mob.getY() + 1.0D);
            double dZ = target.getZ() - this.mob.getZ();
            double horizontalDist = Mth.sqrt((float) (dX * dX + dZ * dZ));

            float targetYaw = (float) (Mth.atan2(dZ, dX) * (180.0D / Math.PI)) - 90.0F;
            float targetPitch = (float) (-(Mth.atan2(dY, horizontalDist) * (180.0D / Math.PI)));

            this.mob.setYRot(Mth.approachDegrees(this.mob.getYRot(), targetYaw, SWEEP_SPEED));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.setXRot(Mth.approachDegrees(this.mob.getXRot(), targetPitch, SWEEP_SPEED));
        }

        if (this.activeBeam != null && this.activeBeam.isAlive() && !this.mob.level().isClientSide) {
            float yawRad = (float) ((this.mob.yHeadRot + 90.0F) * Math.PI / 180.0D);
            float pitchRad = (float) (-this.mob.getXRot() * Math.PI / 180.0D);

            this.activeBeam.setYaw(yawRad);
            this.activeBeam.setPitch(pitchRad);
            this.activeBeam.setPos(this.mob.getX(), this.mob.getY() + 1.0D, this.mob.getZ());
        }

        if (this.attackTimer >= this.cooldownTicks) {
            this.stop();
        }
    }

    @Override
    public void stop() {
        if (this.activeBeam != null && this.activeBeam.isAlive()) {
            this.activeBeam.discard();
        }
        this.activeBeam = null;
        this.attackTimer = 0;
    }
}