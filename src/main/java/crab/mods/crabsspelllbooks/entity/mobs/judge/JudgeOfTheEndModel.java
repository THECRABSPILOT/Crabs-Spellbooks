package crab.mods.crabsspelllbooks.entity.mobs.judge;

import software.bernie.geckolib.model.GeoModel;
import net.minecraft.resources.ResourceLocation;

public class JudgeOfTheEndModel extends GeoModel<JudgeOfTheEndEntity> {
    @Override
    public ResourceLocation getModelResource(JudgeOfTheEndEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "geo/entity/judgeoftheend.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(JudgeOfTheEndEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "textures/entity/judgee.png");
    }

    @Override
    public ResourceLocation getAnimationResource(JudgeOfTheEndEntity animatable) {
        return new ResourceLocation("crabs_spellbooks", "animations/entity/judgeoftheend.animation.json");
    }
}
