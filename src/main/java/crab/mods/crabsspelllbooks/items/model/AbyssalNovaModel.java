package crab.mods.crabsspelllbooks.items.model;

import crab.mods.crabsspelllbooks.items.AbyssalNova;
import crab.mods.crabsspelllbooks.items.TheNova;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AbyssalNovaModel extends GeoModel<AbyssalNova> {
    @Override
    public ResourceLocation getModelResource(AbyssalNova animatable) {
        return new ResourceLocation("crabs_spellbooks", "geo/item/nova.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AbyssalNova animatable) {
        return new ResourceLocation("crabs_spellbooks", "textures/item/abyysalnova.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AbyssalNova animatable) {
        return new ResourceLocation("crabs_spellbooks", "animations/item/nova.animation.json");
    }
}