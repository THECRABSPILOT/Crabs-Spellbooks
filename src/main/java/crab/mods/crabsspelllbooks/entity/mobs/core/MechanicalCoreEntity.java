package crab.mods.crabsspelllbooks.entity.mobs.core;

import com.github.L_Ender.cataclysm.init.ModEntities;
import crab.mods.crabsspelllbooks.entity.goals.DeathLaserGoal;
import crab.mods.crabsspelllbooks.entity.goals.LastPrismGoal;
import crab.mods.crabsspelllbooks.entity.goals.MiniLaserAtPlayer;
import crab.mods.crabsspelllbooks.entity.goals.SummonMinionsGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MechanicalCoreEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public int attackCycle = 0;
    public int attackCooldown = 0;

    public MechanicalCoreEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        //this.goalSelector.addGoal(1, new DeathLaserGoal(this, 32.0D, 99999));
        this.goalSelector.addGoal(1, new LastPrismGoal(this, 32.0D, 20));
        this.goalSelector.addGoal(3, new MiniLaserAtPlayer(this, 32.0D, 10));
        this.goalSelector.addGoal(2, new SummonMinionsGoal(this, 32.0D, ModEntities.THE_WATCHER.get(),5,60));
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        this.setDeltaMovement(0, 0, 0);

        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            }
        }
    }

    public void advanceAttackCycle(int cooldown) {
        this.attackCycle = (this.attackCycle + 1) % 3;
        this.attackCooldown = cooldown;
    }

    public boolean canUseAttack(int index) {
        return this.attackCooldown <= 0 && this.attackCycle == index;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
       /* controllers.add(new AnimationController<>(this, "look", 0, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("animation.mechanical_core.look"))
        ));
        */
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}