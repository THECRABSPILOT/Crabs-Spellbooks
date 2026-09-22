package crab.mods.crabsspelllbooks.client.model;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.items.JudgeArmorItem;
import crab.mods.crabsspelllbooks.items.MechArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MechArmorModel extends GeoModel<MechArmorItem> {
    @Override
    public ResourceLocation getModelResource(MechArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "geo/armor/mecharmor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MechArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "textures/armor/mecharmor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MechArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "animations/armor/mecharmor.animation.json");
    }
}