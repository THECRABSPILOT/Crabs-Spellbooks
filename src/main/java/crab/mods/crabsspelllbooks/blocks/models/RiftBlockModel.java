package crab.mods.crabsspelllbooks.blocks.models;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.blocks.entity.RiftBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RiftBlockModel extends GeoModel<RiftBlockEntity> {
    @Override
    public ResourceLocation getModelResource(RiftBlockEntity animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "geo/block/rift.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RiftBlockEntity animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "textures/block/rift.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RiftBlockEntity animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "animations/block/rift.animation.json");
    }
}
