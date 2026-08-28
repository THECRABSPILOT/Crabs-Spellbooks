package crab.mods.crabsspelllbooks.spells.Eldritch;

import crab.mods.crabsspelllbooks.registry.ModEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

@AutoSpellConfig
public class SensoryDeprivationSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("crabs_spellbooks", "sensory_deprivation");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(30)
            .build();

    public SensoryDeprivationSpell() {
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
        return Optional.of(io.redspace.ironsspellbooks.registries.SoundRegistry.ELDRITCH_BLAST.get());
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        // Target an entity within 25 blocks via raycast
        HitResult raycast = Utils.raycastForEntity(level, entity, 25, true);

        if (raycast instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity target) {
            int duration = 200; // 10 seconds (20 ticks * 10)

            // 1. Apply Blindness
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 0, false, false, true));

            // 2. Apply Custom MobEffect for Muting & Hiding HP
            target.addEffect(new MobEffectInstance(ModEffects.SENSORY_DEPRIVATION.get(), duration, 0, false, false, true));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}