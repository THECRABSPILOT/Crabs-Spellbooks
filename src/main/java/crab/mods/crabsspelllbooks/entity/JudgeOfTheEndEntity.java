package crab.mods.crabsspelllbooks.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JudgeOfTheEndEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Boolean> IS_ACTIVE =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ACTIVATING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_PAUSING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RETREATING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation PREBOSS_IDLE = RawAnimation.begin().thenLoop("prebossidle");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation CHALLENGE = RawAnimation.begin()
            .then("challenge", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation SLASH = RawAnimation.begin()
            .then("slash", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    private Vec3 retreatLocation;
    private float homeYRot;
    private int activationTimer = -1;
    private int attackCooldown = 0;
    private int halfHeartPauseTimer = -1;
    private int retreatTimer = -1;
    private LivingEntity targetToHeal = null;

    private static final int CHALLENGE_ANIM_TICKS = 100;
    private static final int HALF_HEART_PAUSE_TICKS = 30;
    private static final int MIN_RETREAT_TICKS = 20;
    private static final int MAX_RETREAT_TICKS = 200;
    private static final double HOME_DIST_SQR = 9.0D;

    public JudgeOfTheEndEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    private boolean isStatue() {
        return !isActive() && !isActivating() && !isPausing() && !isRetreating();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ACTIVE, false);
        this.entityData.define(IS_ACTIVATING, false);
        this.entityData.define(IS_PAUSING, false);
        this.entityData.define(IS_RETREATING, false);
    }

    public boolean isActive() { return this.entityData.get(IS_ACTIVE); }
    public void setActive(boolean active) { this.entityData.set(IS_ACTIVE, active); }

    public boolean isActivating() { return this.entityData.get(IS_ACTIVATING); }
    public void setActivating(boolean activating) { this.entityData.set(IS_ACTIVATING, activating); }

    public boolean isPausing() { return this.entityData.get(IS_PAUSING); }
    public void setPausing(boolean pausing) { this.entityData.set(IS_PAUSING, pausing); }

    public boolean isRetreating() { return this.entityData.get(IS_RETREATING); }
    public void setRetreating(boolean retreating) { this.entityData.set(IS_RETREATING, retreating); }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new Goal() {
            @Override
            public boolean canUse() {
                return isRetreating() && retreatLocation != null
                        && JudgeOfTheEndEntity.this.distanceToSqr(retreatLocation) > HOME_DIST_SQR;
            }

            @Override
            public boolean canContinueToUse() {
                return isRetreating() && retreatLocation != null
                        && JudgeOfTheEndEntity.this.distanceToSqr(retreatLocation) > HOME_DIST_SQR;
            }

            @Override
            public boolean requiresUpdateEveryTick() {
                return true;
            }

            @Override
            public void start() {
                moveHome();
            }

            @Override
            public void tick() {
                if (retreatLocation == null) return;
                if (JudgeOfTheEndEntity.this.distanceToSqr(retreatLocation) > HOME_DIST_SQR
                        && JudgeOfTheEndEntity.this.getNavigation().isDone()) {
                    moveHome();
                }
            }

            private void moveHome() {
                JudgeOfTheEndEntity.this.getNavigation().moveTo(
                        retreatLocation.x,
                        retreatLocation.y,
                        retreatLocation.z,
                        1.5D
                );
            }

            @Override
            public void stop() {
                JudgeOfTheEndEntity.this.getNavigation().stop();
            }
        });

        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false) {
            @Override
            public boolean canUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canUse();
            }

            @Override
            protected void checkAndPerformAttack(LivingEntity target, double distanceToTargetSqr) {
                double attackReachSqr = this.getAttackReachSqr(target);
                if (distanceToTargetSqr <= attackReachSqr && JudgeOfTheEndEntity.this.attackCooldown <= 0) {
                    JudgeOfTheEndEntity.this.doHurtTarget(target);
                }
            }
        });

        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canUse();
            }
        });

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return (isActive() || isActivating() || isPausing()) && !isRetreating() && super.canUse();
            }
        });

        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canUse();
            }
        });

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canUse();
            }
        });
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.attackCooldown <= 0 && isActive() && !isPausing() && !isRetreating()) {
            this.attackCooldown = 20;

            if (target instanceof LivingEntity livingTarget) {
                float targetHealth = livingTarget.getHealth();
                float proposedDamage = this.getAttackDamage();

                if (targetHealth - proposedDamage <= 1.0F) {
                    livingTarget.setHealth(1.0F);

                    if (!this.level().isClientSide) {
                        this.setPausing(true);
                        this.halfHeartPauseTimer = HALF_HEART_PAUSE_TICKS;
                        this.targetToHeal = livingTarget;
                        this.getNavigation().stop();
                        this.bossEvent.removeAllPlayers();
                    }
                    return true;
                }
            }

            if (!this.level().isClientSide) {
                this.triggerAnim("combat", "slash");
            }
            return super.doHurtTarget(target);
        }
        return false;
    }

    private float getAttackDamage() {
        return (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
    }

    private void splashTargetWithHealing(LivingEntity target) {
        if (target == null || !target.isAlive()) return;

        ItemStack potionStack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_HEALING);
        ThrownPotion thrownPotion = new ThrownPotion(this.level(), this);
        thrownPotion.setItem(potionStack);
        thrownPotion.setPos(target.getX(), target.getY() + 1.0D, target.getZ());
        this.level().addFreshEntity(thrownPotion);
        target.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        if (this.retreatLocation == null && !this.level().isClientSide) {
            this.retreatLocation = this.position();
            this.homeYRot = this.getYRot();
        }

        if (isStatue() || isPausing()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }

        if (!this.level().isClientSide && isPausing()) {
            if (this.targetToHeal != null) {
                this.getLookControl().setLookAt(this.targetToHeal, 30.0F, 30.0F);
            }

            if (halfHeartPauseTimer < 0) {
                halfHeartPauseTimer = HALF_HEART_PAUSE_TICKS;
            }

            if (halfHeartPauseTimer > 0) {
                halfHeartPauseTimer--;
            }
            if (halfHeartPauseTimer == 0) {
                halfHeartPauseTimer = -1;
                splashTargetWithHealing(this.targetToHeal);
                this.targetToHeal = null;
                setPausing(false);
                startRetreat();
            }
        }

        if (!this.level().isClientSide && isActivating()) {
            if (activationTimer < 0) {
                activationTimer = CHALLENGE_ANIM_TICKS;
            }

            if (activationTimer > 0) {
                activationTimer--;
            }
            if (activationTimer == 0) {
                activationTimer = -1;
                setActivating(false);
                setActive(true);
                showBossbarToNearbyPlayers();
            }
        }

        if (!this.level().isClientSide && isRetreating()) {
            if (retreatTimer < 0) {
                retreatTimer = 0;
            } else {
                retreatTimer++;
            }

            boolean atHome = this.retreatLocation == null
                    || this.distanceToSqr(this.retreatLocation) <= HOME_DIST_SQR;

            if ((atHome && retreatTimer >= MIN_RETREAT_TICKS) || retreatTimer >= MAX_RETREAT_TICKS) {
                snapHome();
                resetToInactive();
            }
        }

        if (!this.level().isClientSide && isActive() && !isRetreating() && !isPausing()) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && isStatue()) {
            setActivating(true);
            activationTimer = CHALLENGE_ANIM_TICKS;
            player.sendSystemMessage(Component.literal("The Judge prepares for battle..."));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!isActive() || isActivating() || isRetreating() || isPausing()) {
            return false;
        }

        if (amount >= 100.0F) {
            return super.hurt(source, amount);
        }

        if (this.getHealth() - amount <= 1.0F) {
            this.setHealth(1.0F);
            startRetreat();
            return true;
        }

        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            this.bossEvent.removeAllPlayers();
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        if (!this.level().isClientSide) {
            this.bossEvent.removeAllPlayers();
        }
    }

    private void startRetreat() {
        setActive(false);
        setActivating(false);
        setPausing(false);
        setRetreating(true);
        this.retreatTimer = 0;
        this.setTarget(null);
        this.bossEvent.removeAllPlayers();

        if (this.retreatLocation != null
                && this.distanceToSqr(this.retreatLocation) > HOME_DIST_SQR) {
            this.getNavigation().moveTo(
                    this.retreatLocation.x,
                    this.retreatLocation.y,
                    this.retreatLocation.z,
                    1.5D
            );
        }
    }

    private void snapHome() {
        if (this.retreatLocation == null) return;
        this.teleportTo(this.retreatLocation.x, this.retreatLocation.y, this.retreatLocation.z);
        this.setYRot(this.homeYRot);
        this.yBodyRot = this.homeYRot;
        this.setYHeadRot(this.homeYRot);
        this.yRotO = this.homeYRot;
        this.yBodyRotO = this.homeYRot;
        this.yHeadRotO = this.homeYRot;
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void resetToInactive() {
        setActive(false);
        setActivating(false);
        setPausing(false);
        setRetreating(false);

        this.activationTimer = -1;
        this.halfHeartPauseTimer = -1;
        this.retreatTimer = -1;
        this.targetToHeal = null;
        this.getNavigation().stop();
        this.setHealth(this.getMaxHealth());
        this.setTarget(null);
        this.bossEvent.removeAllPlayers();
    }

    private void showBossbarToNearbyPlayers() {
        if (!this.level().isClientSide) {
            for (ServerPlayer player : this.level().getEntitiesOfClass(
                    ServerPlayer.class,
                    this.getBoundingBox().inflate(32.0D))) {
                this.bossEvent.addPlayer(player);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return isActive() && !isRetreating() && !isPausing() && super.isPushable();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "statue", 0, this::statuePredicate));
        controllers.add(new AnimationController<>(this, "combat", 5, this::combatPredicate)
                .triggerableAnim("slash", SLASH)
                .receiveTriggeredAnimations());
    }

    private PlayState statuePredicate(AnimationState<JudgeOfTheEndEntity> event) {
        if (isStatue()) {
            return event.setAndContinue(PREBOSS_IDLE);
        }
        return PlayState.STOP;
    }

    private PlayState combatPredicate(AnimationState<JudgeOfTheEndEntity> event) {
        if (isStatue()) {
            return PlayState.STOP;
        }

        if (event.getController().isPlayingTriggeredAnimation()
                && isActive()
                && !isPausing()
                && !isRetreating()) {
            return PlayState.CONTINUE;
        }

        if (isActivating()) {
            return event.setAndContinue(CHALLENGE);
        }

        if (isPausing() || !event.isMoving()) {
            return event.setAndContinue(IDLE);
        }

        return event.setAndContinue(WALK);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsActive", this.isActive());
        tag.putBoolean("IsActivating", this.isActivating());
        tag.putBoolean("IsPausing", this.isPausing());
        tag.putBoolean("IsRetreating", this.isRetreating());
        tag.putInt("ActivationTimer", this.activationTimer);
        tag.putInt("HalfHeartPauseTimer", this.halfHeartPauseTimer);
        tag.putInt("RetreatTimer", this.retreatTimer);
        tag.putFloat("HomeYRot", this.homeYRot);
        if (this.retreatLocation != null) {
            tag.putDouble("RetreatX", this.retreatLocation.x);
            tag.putDouble("RetreatY", this.retreatLocation.y);
            tag.putDouble("RetreatZ", this.retreatLocation.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setActive(tag.getBoolean("IsActive"));
        this.setActivating(tag.getBoolean("IsActivating"));
        this.setPausing(tag.getBoolean("IsPausing"));
        this.setRetreating(tag.getBoolean("IsRetreating"));
        this.activationTimer = tag.contains("ActivationTimer") ? tag.getInt("ActivationTimer") : -1;
        this.halfHeartPauseTimer = tag.contains("HalfHeartPauseTimer") ? tag.getInt("HalfHeartPauseTimer") : -1;
        this.retreatTimer = tag.contains("RetreatTimer") ? tag.getInt("RetreatTimer") : -1;
        this.homeYRot = tag.getFloat("HomeYRot");
        if (tag.contains("RetreatX")) {
            this.retreatLocation = new Vec3(
                    tag.getDouble("RetreatX"),
                    tag.getDouble("RetreatY"),
                    tag.getDouble("RetreatZ")
            );
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}