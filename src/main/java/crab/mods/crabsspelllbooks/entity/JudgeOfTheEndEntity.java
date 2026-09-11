package crab.mods.crabsspelllbooks.entity;

import crab.mods.crabsspelllbooks.entity.longgoals.JudgeShadowSlashGoal;
import crab.mods.crabsspelllbooks.registry.ItemRegistry;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.entity.spells.magic_missile.MagicMissileProjectile;
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
import net.minecraft.world.entity.item.ItemEntity;
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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JudgeOfTheEndEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Boolean> IS_ACTIVE =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_ACTIVATING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_PAUSING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RETREATING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TYPE =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_DASHING =
            SynchedEntityData.defineId(JudgeOfTheEndEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation PREBOSS_IDLE = RawAnimation.begin().thenLoop("prebossidle");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("dash");
    private static final RawAnimation CHALLENGE = RawAnimation.begin()
            .then("challenge", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation SLASH = RawAnimation.begin()
            .then("slash", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation CAST_SPELL = RawAnimation.begin()
            .then("instacast", Animation.LoopType.PLAY_ONCE);

    private static final RawAnimation LONG_CAST_SPELL = RawAnimation.begin()
            .then("longcast", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    // Hardcode retreat location directly to (16, 67, 0)
    private static final Vec3 HARDCODED_RETREAT_POS = new Vec3(16.0D, 67.0D, 0.0D);
    private Vec3 retreatLocation = HARDCODED_RETREAT_POS;
    private float homeYRot;
    private int activationTimer = -1;
    private int attackCooldown = 0;
    private int halfHeartPauseTimer = -1;
    private int retreatTimer = -1;
    private LivingEntity targetToHeal = null;
    private boolean droppedBookThisFight = false;

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

    public int getPhase() {
        float ratio = this.getHealth() / this.getMaxHealth();
        if (ratio <= 0.333F) return 3; // Phase 2 Enraged
        if (ratio <= 0.50F) return 2;  // Phase 2
        return 1;                       // Phase 1
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ACTIVE, false);
        this.entityData.define(IS_ACTIVATING, false);
        this.entityData.define(IS_PAUSING, false);
        this.entityData.define(IS_RETREATING, false);
        this.entityData.define(ATTACK_TYPE, 0);
        this.entityData.define(IS_DASHING, false);
    }

    public boolean isActive() { return this.entityData.get(IS_ACTIVE); }
    public void setActive(boolean active) { this.entityData.set(IS_ACTIVE, active); }

    public boolean isActivating() { return this.entityData.get(IS_ACTIVATING); }
    public void setActivating(boolean activating) { this.entityData.set(IS_ACTIVATING, activating); }

    public boolean isPausing() { return this.entityData.get(IS_PAUSING); }
    public void setPausing(boolean pausing) { this.entityData.set(IS_PAUSING, pausing); }

    public boolean isRetreating() { return this.entityData.get(IS_RETREATING); }
    public void setRetreating(boolean retreating) { this.entityData.set(IS_RETREATING, retreating); }

    public int getAttackType() { return this.entityData.get(ATTACK_TYPE); }
    public void setAttackType(int type) { this.entityData.set(ATTACK_TYPE, type); }

    public boolean isDashing() { return this.entityData.get(IS_DASHING); }
    public void setDashing(boolean dashing) { this.entityData.set(IS_DASHING, dashing); }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new JudgeShadowSlashGoal(this, 18.0f));
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

        // Special attacks
        this.goalSelector.addGoal(3, new TripleDashSlashGoal(this));
        this.goalSelector.addGoal(3, new ProjectileBurstGoal(this));
        this.goalSelector.addGoal(3, new BlackHoleGoal(this));

        // Fallback pursuit & standard attack when special attacks aren't active
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.25D, true) {
            @Override
            public boolean canUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canContinueToUse();
            }
        });

        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 64.0F) {
            @Override
            public boolean canUse() {
                return (isActive() || isActivating() || isPausing()) && !isRetreating() && super.canUse();
            }
        });

        // Always target nearby players when active
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false) {
            @Override
            public boolean canUse() {
                return isActive() && !isPausing() && !isRetreating() && super.canUse();
            }
        });
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.attackCooldown <= 0 && isActive() && !isPausing() && !isRetreating()) {
            this.attackCooldown = getPhase() == 3 ? 5 : 20;

            if (target instanceof Player livingTarget) {
                float targetHealth = livingTarget.getHealth();
                float proposedDamage = this.getAttackDamage();

                if (targetHealth - proposedDamage <= 1.0F) {
                    livingTarget.setHealth(1.0F);

                    if (!this.level().isClientSide) {
                        triggerHalfHeartPauseAndBookDrop(livingTarget);
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

    private void triggerHalfHeartPauseAndBookDrop(LivingEntity target) {
        this.setPausing(true);
        this.halfHeartPauseTimer = HALF_HEART_PAUSE_TICKS;
        this.targetToHeal = target;
        this.getNavigation().stop();
        this.bossEvent.removeAllPlayers();

        if (!droppedBookThisFight && this.getHealth() / this.getMaxHealth() <= 0.334F) {
            ItemEntity bookEntity = new ItemEntity(
                    this.level(),
                    this.getX(),
                    this.getY() + 0.5D,
                    this.getZ(),
                    new ItemStack(ItemRegistry.NOVA.get())
            );
            this.level().addFreshEntity(bookEntity);
            this.droppedBookThisFight = true;
        }
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

        // Always keep retreat location pinned to (16, 67, 0)
        this.retreatLocation = HARDCODED_RETREAT_POS;

        if (isStatue() || isPausing()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }

        if (!this.level().isClientSide) {
            enforceNonLethalPlayerSafety();
            updateBossBarTracking();
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
            }
        }

        if (!this.level().isClientSide && isRetreating()) {
            if (retreatTimer < 0) {
                retreatTimer = 0;
            } else {
                retreatTimer++;
            }

            boolean atHome = this.distanceToSqr(this.retreatLocation) <= HOME_DIST_SQR;

            if ((atHome && retreatTimer >= MIN_RETREAT_TICKS) || retreatTimer >= MAX_RETREAT_TICKS) {
                snapHome();
                resetToInactive();
            }
        }

        if (!this.level().isClientSide && isActive() && !isRetreating() && !isPausing()) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    private void updateBossBarTracking() {
        if (!isActive() || isPausing() || isRetreating()) {
            this.bossEvent.removeAllPlayers();
            return;
        }

        List<ServerPlayer> nearbyPlayers = this.level().getEntitiesOfClass(
                ServerPlayer.class,
                this.getBoundingBox().inflate(32.0D)
        );

        Set<ServerPlayer> currentPlayers = new HashSet<>(this.bossEvent.getPlayers());

        for (ServerPlayer player : nearbyPlayers) {
            if (!currentPlayers.contains(player)) {
                this.bossEvent.addPlayer(player);
            }
        }

        for (ServerPlayer player : currentPlayers) {
            if (!nearbyPlayers.contains(player)) {
                this.bossEvent.removePlayer(player);
            }
        }
    }

    private void enforceNonLethalPlayerSafety() {
        List<ServerPlayer> players = this.level().getEntitiesOfClass(
                ServerPlayer.class,
                this.getBoundingBox().inflate(32.0D)
        );

        for (ServerPlayer player : players) {
            if (isActive() || isPausing()) {
                player.resetFallDistance();
            }

            if (player.getHealth() <= 1.0F && (isActive() || isPausing())) {
                player.setHealth(1.0F);
                clearLethalEntitiesAroundPlayer(player);
            }
        }
    }

    private void clearLethalEntitiesAroundPlayer(ServerPlayer player) {
        List<Entity> dangerousEntities = this.level().getEntities(
                this,
                player.getBoundingBox().inflate(8.0D),
                e -> e instanceof MagicMissileProjectile || e instanceof BlackHole
        );
        for (Entity e : dangerousEntities) {
            e.discard();
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
            if (source.getEntity() instanceof LivingEntity attacker) {
                triggerHalfHeartPauseAndBookDrop(attacker);
            } else {
                startRetreat();
            }
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

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (isActive() && !isPausing() && !isRetreating()) {
            this.bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    private void startRetreat() {
        setActive(false);
        setActivating(false);
        setPausing(false);
        setRetreating(true);
        setDashing(false);
        this.retreatTimer = 0;
        this.setTarget(null);
        this.bossEvent.removeAllPlayers();

        if (this.distanceToSqr(this.retreatLocation) > HOME_DIST_SQR) {
            this.getNavigation().moveTo(
                    this.retreatLocation.x,
                    this.retreatLocation.y,
                    this.retreatLocation.z,
                    1.5D
            );
        }
    }

    private void snapHome() {
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
        setAttackType(0);
        setDashing(false);

        this.activationTimer = -1;
        this.halfHeartPauseTimer = -1;
        this.retreatTimer = -1;
        this.targetToHeal = null;
        this.droppedBookThisFight = false;
        this.getNavigation().stop();
        this.setHealth(this.getMaxHealth());
        this.setTarget(null);
        this.bossEvent.removeAllPlayers();
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
                .triggerableAnim("instacast", CAST_SPELL)
                .triggerableAnim("longcast", LONG_CAST_SPELL)
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

        if (isDashing() && isActive() && !isPausing() && !isRetreating()) {
            return event.setAndContinue(DASH);
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
        tag.putInt("AttackType", this.getAttackType());
        tag.putBoolean("IsDashing", this.isDashing());
        tag.putBoolean("DroppedBookThisFight", this.droppedBookThisFight);
        tag.putInt("ActivationTimer", this.activationTimer);
        tag.putInt("HalfHeartPauseTimer", this.halfHeartPauseTimer);
        tag.putInt("RetreatTimer", this.retreatTimer);
        tag.putFloat("HomeYRot", this.homeYRot);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setActive(tag.getBoolean("IsActive"));
        this.setActivating(tag.getBoolean("IsActivating"));
        this.setPausing(tag.getBoolean("IsPausing"));
        this.setRetreating(tag.getBoolean("IsRetreating"));
        if (tag.contains("AttackType")) {
            this.setAttackType(tag.getInt("AttackType"));
        }
        if (tag.contains("IsDashing")) {
            this.setDashing(tag.getBoolean("IsDashing"));
        }
        this.droppedBookThisFight = tag.getBoolean("DroppedBookThisFight");
        this.activationTimer = tag.contains("ActivationTimer") ? tag.getInt("ActivationTimer") : -1;
        this.halfHeartPauseTimer = tag.contains("HalfHeartPauseTimer") ? tag.getInt("HalfHeartPauseTimer") : -1;
        this.retreatTimer = tag.contains("RetreatTimer") ? tag.getInt("RetreatTimer") : -1;
        this.homeYRot = tag.getFloat("HomeYRot");
        this.retreatLocation = HARDCODED_RETREAT_POS;
    }

    public class TripleDashSlashGoal extends Goal {
        private int dashesRemaining;
        private int timer;
        private boolean isDashing;

        public TripleDashSlashGoal(JudgeOfTheEndEntity boss) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = JudgeOfTheEndEntity.this.getTarget();
            return JudgeOfTheEndEntity.this.isActive()
                    && !JudgeOfTheEndEntity.this.isPausing()
                    && !JudgeOfTheEndEntity.this.isRetreating()
                    && JudgeOfTheEndEntity.this.getAttackType() == 0
                    && target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.dashesRemaining = JudgeOfTheEndEntity.this.getPhase() >= 2 ? 3 : 1;
            this.timer = 0;
            this.isDashing = false;
        }

        @Override
        public void tick() {
            LivingEntity target = JudgeOfTheEndEntity.this.getTarget();
            if (target == null) return;

            JudgeOfTheEndEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (!isDashing) {
                timer++;
                int delay = JudgeOfTheEndEntity.this.getPhase() == 3 ? 3 : 8;
                if (timer >= delay) {
                    isDashing = true;
                    timer = 10;
                    JudgeOfTheEndEntity.this.setDashing(true);
                }
            } else {
                Vec3 dashDir = target.position().subtract(JudgeOfTheEndEntity.this.position()).normalize().scale(1.1D);
                JudgeOfTheEndEntity.this.setDeltaMovement(dashDir.x, JudgeOfTheEndEntity.this.getDeltaMovement().y, dashDir.z);

                if (JudgeOfTheEndEntity.this.distanceToSqr(target) <= 4.0D) {
                    JudgeOfTheEndEntity.this.doHurtTarget(target);
                }

                timer--;
                if (timer <= 0) {
                    isDashing = false;
                    JudgeOfTheEndEntity.this.setDashing(false);
                    dashesRemaining--;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return dashesRemaining > 0 && canUse();
        }

        @Override
        public void stop() {
            JudgeOfTheEndEntity.this.setDashing(false);
            JudgeOfTheEndEntity.this.setAttackType((JudgeOfTheEndEntity.this.getAttackType() + 1) % 3);
        }
    }

    public class ProjectileBurstGoal extends Goal {
        private int burstCount;
        private int timer;

        public ProjectileBurstGoal(JudgeOfTheEndEntity boss) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = JudgeOfTheEndEntity.this.getTarget();
            return JudgeOfTheEndEntity.this.isActive()
                    && !JudgeOfTheEndEntity.this.isPausing()
                    && !JudgeOfTheEndEntity.this.isRetreating()
                    && JudgeOfTheEndEntity.this.getAttackType() == 1
                    && target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.burstCount = JudgeOfTheEndEntity.this.getPhase() >= 2 ? 5 : 1;
            this.timer = 0;
            JudgeOfTheEndEntity.this.getNavigation().stop();
            JudgeOfTheEndEntity.this.triggerAnim("combat", "slash");
        }

        @Override
        public void tick() {
            LivingEntity target = JudgeOfTheEndEntity.this.getTarget();
            if (target == null) return;

            JudgeOfTheEndEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            timer--;

            if (timer <= 0 && burstCount > 0) {
                timer = JudgeOfTheEndEntity.this.getPhase() == 3 ? 2 : 6;
                burstCount--;
                fireProjectile(target);
            }
        }

        private void fireProjectile(LivingEntity target) {
            if (JudgeOfTheEndEntity.this.level().isClientSide) return;
            MagicMissileProjectile missile = new MagicMissileProjectile(JudgeOfTheEndEntity.this.level(), JudgeOfTheEndEntity.this);
            missile.setPos(JudgeOfTheEndEntity.this.getX(), JudgeOfTheEndEntity.this.getY(0.6D), JudgeOfTheEndEntity.this.getZ());

            Vec3 dir = target.position().subtract(JudgeOfTheEndEntity.this.position()).normalize();
            missile.shoot(dir.x, dir.y, dir.z, 1.5F, 2.0F);
            missile.setDamage(6.0F);
            JudgeOfTheEndEntity.this.level().addFreshEntity(missile);
        }

        @Override
        public boolean canContinueToUse() {
            return burstCount > 0 && canUse();
        }

        @Override
        public void stop() {
            JudgeOfTheEndEntity.this.setAttackType((JudgeOfTheEndEntity.this.getAttackType() + 1) % 3);
        }
    }

    public class BlackHoleGoal extends Goal {
        private int timer;

        public BlackHoleGoal(JudgeOfTheEndEntity boss) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = JudgeOfTheEndEntity.this.getTarget();
            return JudgeOfTheEndEntity.this.isActive()
                    && !JudgeOfTheEndEntity.this.isPausing()
                    && !JudgeOfTheEndEntity.this.isRetreating()
                    && JudgeOfTheEndEntity.this.getPhase() >= 2
                    && JudgeOfTheEndEntity.this.getAttackType() == 2
                    && target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.timer = 20;
            JudgeOfTheEndEntity.this.getNavigation().stop();
            JudgeOfTheEndEntity.this.triggerAnim("combat", "slash");
        }

        @Override
        public void tick() {
            LivingEntity target = JudgeOfTheEndEntity.this.getTarget();
            if (target != null) {
                JudgeOfTheEndEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            timer--;
            if (timer == 0 && target != null && !JudgeOfTheEndEntity.this.level().isClientSide) {
                BlackHole blackHole = new BlackHole(JudgeOfTheEndEntity.this.level(), JudgeOfTheEndEntity.this);
                blackHole.setPos(target.getX(), target.getY() + 1.0D, target.getZ());
                blackHole.setDamage(4.0F);
                JudgeOfTheEndEntity.this.level().addFreshEntity(blackHole);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return timer > 0;
        }

        @Override
        public void stop() {
            JudgeOfTheEndEntity.this.setAttackType(0);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}