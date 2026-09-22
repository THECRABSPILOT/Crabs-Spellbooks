package crab.mods.crabsspelllbooks.client.model;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.items.JudgeArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class JudgeArmorModel extends GeoModel<JudgeArmorItem> {
    @Override
    public ResourceLocation getModelResource(JudgeArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "geo/armor/nova_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(JudgeArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "textures/armor/novaset.png");
    }

    @Override
    public ResourceLocation getAnimationResource(JudgeArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "animations/armor/judge_armor.animation.json");
    }
}