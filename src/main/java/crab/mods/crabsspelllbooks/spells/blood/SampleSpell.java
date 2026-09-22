package crab.mods.crabsspelllbooks.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class SampleSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("crabs_spellbooks", "sample");

    private static final String TARGET_UUID_KEY = "CrabsSampledTargetUUID";
    private static final String TARGET_NAME_KEY = "CrabsSampledTargetName";
    private static final float MAX_HP_SAMPLING_LIMIT = 30.0f;

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(10)
            .build();

    public SampleSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 50;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.CLEANSE_CAST.get());
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        CompoundTag persistentData = entity.getPersistentData();

        if (!persistentData.contains(TARGET_UUID_KEY)) {
            return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .35f);
        }

        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (level.isClientSide) {
            super.onCast(level, spellLevel, entity, castSource, playerMagicData);
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        CompoundTag tag = entity.getPersistentData();

        // STAGE 2: TELEPORT STORED TARGET
        if (tag.contains(TARGET_UUID_KEY)) {
            UUID targetUUID = tag.getUUID(TARGET_UUID_KEY);

            Entity targetEntity = null;

            for (ServerLevel searchLevel : serverLevel.getServer().getAllLevels()) {
                Entity found = searchLevel.getEntity(targetUUID);
                if (found != null) {
                    targetEntity = found;
                    break;
                }
            }

            if (targetEntity instanceof LivingEntity livingTarget && targetEntity.isAlive()) {
                livingTarget.teleportTo(entity.getX(), entity.getY(), entity.getZ());
                serverLevel.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            }

            // Always clear data on Stage 2 cast attempt
            tag.remove(TARGET_UUID_KEY);
            tag.remove(TARGET_NAME_KEY);

            // Play finish sound manually and trigger mandatory cooldown
            playSound(getCastFinishSound(), entity);
            if (entity instanceof ServerPlayer serverPlayer) {
                MagicHelper.MAGIC_MANAGER.addCooldown(serverPlayer, this, castSource);
            }

            // Return immediately so default onCast flow does not clear/reset cooldown states
            return;

        } else {
            // STAGE 1: SAMPLE TARGET
            if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
                LivingEntity targetEntity = targetData.getTarget(serverLevel);

                if (targetEntity != null && targetEntity.isAlive()) {

                    // Reject entities exceeding HP limit
                    if (targetEntity.getMaxHealth() > MAX_HP_SAMPLING_LIMIT) {
                        if (entity instanceof Player player) {
                            player.displayClientMessage(
                                    Component.literal("You cannot sample something like this").withStyle(ChatFormatting.RED),
                                    true
                            );
                        }
                        // Early return skips cooldown triggers, allowing instant spammable retries
                        return;
                    }

                    // Successful Sample: store entity and allow player to cast Stage 2 next
                    targetEntity.hurt(level.damageSources().magic(), 1.0f);

                    tag.putUUID(TARGET_UUID_KEY, targetEntity.getUUID());
                    tag.putString(TARGET_NAME_KEY, targetEntity.getName().getString());

                    serverLevel.playSound(null, targetEntity.blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 1.0f, 0.5f);
                } else {
                    // No valid target in crosshair
                    return;
                }
            } else {
                return;
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}