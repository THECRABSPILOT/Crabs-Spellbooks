package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.effect.SensoryDeprivationEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// For NeoForge, replace ForgeRegistries.MOB_EFFECTS with Registries.MOB_EFFECT
public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "crabs_spellbooks");

    public static final RegistryObject<MobEffect> SENSORY_DEPRIVATION = MOB_EFFECTS.register(
            "sensory_deprivation",
            () -> new SensoryDeprivationEffect(MobEffectCategory.HARMFUL, 0x1A0526)
    );

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}