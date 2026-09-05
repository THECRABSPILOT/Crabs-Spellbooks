package crab.mods.crabsspelllbooks.entity.model;

import crab.mods.crabsspelllbooks.entity.JudgeOfTheEndEntity;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

public class JudgeOfTheEndModel extends GeoModel<JudgeOfTheEndEntity> {
    @Override
    public ResourceLocation getModelResource(JudgeOfTheEndEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "geo/entity/judgeoftheend.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(JudgeOfTheEndEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "textures/entity/judge.png");
    }

    @Override
    public ResourceLocation getAnimationResource(JudgeOfTheEndEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "animations/entity/judgeoftheend.animation.json");
    }
}
