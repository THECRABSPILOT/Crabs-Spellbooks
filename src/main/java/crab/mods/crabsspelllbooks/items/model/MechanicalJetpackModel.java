package crab.mods.crabsspelllbooks.items.model;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.items.MechanicalJetpackItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MechanicalJetpackModel extends GeoModel<MechanicalJetpackItem> {
    @Override
    public ResourceLocation getModelResource(MechanicalJetpackItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "geo/curio/mechpack.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MechanicalJetpackItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "textures/item/pack.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MechanicalJetpackItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "animations/mechpack.animation.json");
    }

    @Override
    public void setCustomAnimations(MechanicalJetpackItem animatable, long instanceId, AnimationState<MechanicalJetpackItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        float Sf = 1.3F;

        CoreGeoBone pack = getAnimationProcessor().getBone("pack");
        if (pack != null) {

            pack.setPosY(15.0F);
            pack.setPosZ(3.0F);
            pack.setScaleX(Sf);
            pack.setScaleY(Sf);
            pack.setScaleZ(Sf);
        }
    }
}