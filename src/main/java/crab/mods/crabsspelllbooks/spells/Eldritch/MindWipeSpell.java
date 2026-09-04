package crab.mods.crabsspelllbooks.spells.Eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AutoSpellConfig
public class MindWipeSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("crabs_spellbooks", "mind_wipe");

    private static final UUID MIND_WIPE_UUID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(60)
            .build();

    public MindWipeSpell() {
        this.manaCostPerLevel = 500;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 50;
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
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.LIGHTNING_LANCE_CAST.get());
    }

    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .35f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
            var targetEntity = targetData.getTarget((ServerLevel) level );
            if (targetEntity != null) {
                List<Attribute> attributesToZero = List.of(
                        Attributes.ATTACK_DAMAGE,
                        Attributes.MOVEMENT_SPEED,
                        Attributes.ARMOR,
                        Attributes.KNOCKBACK_RESISTANCE,
                        Attributes.ATTACK_SPEED,
                        AttributeRegistry.SPELL_POWER.get()
                );

                for (Attribute attribute : attributesToZero) {
                    AttributeInstance instance = targetEntity.getAttribute(attribute);
                    if (instance != null) {
                        instance.removeModifier(MIND_WIPE_UUID);
                        instance.addTransientModifier(new AttributeModifier(
                                MIND_WIPE_UUID,
                                "Mind Wipe",
                                -1.0D,
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        ));
                    }
                }

                // Safe server-thread tick delay
                scheduleDelayedTask(100, () -> {
                    if (targetEntity.isAlive()) {
                        for (Attribute attribute : attributesToZero) {
                            AttributeInstance instance = targetEntity.getAttribute(attribute);
                            if (instance != null) {
                                instance.removeModifier(MIND_WIPE_UUID);
                            }
                        }
                    }
                });
            }
        }
    }

    // Helper method to execute tasks synchronously on the main thread after a set number of ticks
    private static void scheduleDelayedTask(int delayTicks, Runnable task) {
        MinecraftForge.EVENT_BUS.register(new Object() {
            private int ticks = 0;

            @SubscribeEvent
            public void onServerTick(TickEvent.ServerTickEvent event) {
                if (event.phase == TickEvent.Phase.END) {
                    ticks++;
                    if (ticks >= delayTicks) {
                        task.run();
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                }
            }
        });
    }


}