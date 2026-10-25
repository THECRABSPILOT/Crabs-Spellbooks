package crab.mods.crabsspelllbooks.entity.model;

import crab.mods.crabsspelllbooks.entity.JudgeOfTheEndEntity;
import crab.mods.crabsspelllbooks.entity.MechanicalCoreEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MechanicalCoreModel extends GeoModel<MechanicalCoreEntity> {
    @Override
    public ResourceLocation getModelResource(MechanicalCoreEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "geo/entity/mechanicalcore.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MechanicalCoreEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "textures/entity/core.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MechanicalCoreEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "animations/entity/core.animation.json");
    }
}
