package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class OrbRegistry {

    public static final ResourceKey<UpgradeOrbType> ELDRITCH_SPELL_POWER = ResourceKey.create(
            UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(CrabsSpellbooks.MODID, "eldritch_power")
    );
}
