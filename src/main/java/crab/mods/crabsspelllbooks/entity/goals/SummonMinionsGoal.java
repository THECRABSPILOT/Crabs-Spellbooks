package crab.mods.crabsspelllbooks.entity.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class SummonMinionsGoal extends Goal {

    private final Mob mob;
    private final double attackRangeSqr;
    private final EntityType<?> minionType;
    private final int amount;
    private final int cooldownTicks;

    private int cooldownTimer = 0;

    public SummonMinionsGoal(Mob mob, double attackRange, EntityType<?> minionType, int amount, int cooldownTicks) {
        this.mob = mob;
        this.attackRangeSqr = attackRange * attackRange;
        this.minionType = minionType;
        this.amount = amount;
        this.cooldownTicks = cooldownTicks;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.cooldownTimer > 0) {
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
        this.cooldownTimer = this.cooldownTicks;

        if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = this.mob.getTarget();

        for (int i = 0; i < this.amount; i++) {
            // Spawn minions slightly offset around the caster mob
            double offsetX = (this.mob.getRandom().nextDouble() - 0.5D) * 3.0D;
            double offsetZ = (this.mob.getRandom().nextDouble() - 0.5D) * 3.0D;

            var minionEntity = this.minionType.create(serverLevel);
            if (minionEntity instanceof Mob minionMob) {
                minionMob.setPos(
                        this.mob.getX() + offsetX,
                        this.mob.getY(),
                        this.mob.getZ() + offsetZ
                );

                // Immediately aggro the minion onto the target player
                if (target != null) {
                    minionMob.setTarget(target);
                }

                serverLevel.addFreshEntity(minionMob);
            }
        }
    }

    @Override
    public void tick() {
        if (this.cooldownTimer > 0) {
            this.cooldownTimer--;
        }
    }

    @Override
    public boolean canContinueToUse() {
        // This is an instant action goal, so it stops right after starting
        return false;
    }
}