package crab.mods.crabsspelllbooks.items.model;

import crab.mods.crabsspelllbooks.items.TheNova;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TheNovaModel extends GeoModel<TheNova> {
    @Override
    public ResourceLocation getModelResource(TheNova animatable) {
        return new ResourceLocation("crabs_spellbooks", "geo/item/nova.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TheNova animatable) {
        return new ResourceLocation("crabs_spellbooks", "textures/item/nova.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TheNova animatable) {
        return new ResourceLocation("crabs_spellbooks", "animations/item/nova.animation.json");
    }
}