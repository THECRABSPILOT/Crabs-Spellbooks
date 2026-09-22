package crab.mods.crabsspelllbooks.items.renderer;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.client.model.MechArmorModel;
import crab.mods.crabsspelllbooks.items.MechArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MechArmorRender extends GeoArmorRenderer<MechArmorItem> {
    public MechArmorRender() {
        super(new MechArmorModel());
    }

    // This overrides GeckoLib's automatic generator and forces it to use a legal lowercase path.
    // It doesn't matter if this file exists or not; Minecraft just needs the string to be valid lowercase syntax.
    @Override
    public ResourceLocation getTextureLocation(MechArmorItem animatable) {
        return new ResourceLocation(CrabsSpellbooks.MODID, "textures/armor/mecharmor.png");
    }
}
