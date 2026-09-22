package crab.mods.crabsspelllbooks.items.renderer;

import crab.mods.crabsspelllbooks.client.model.JudgeArmorModel;
import crab.mods.crabsspelllbooks.items.JudgeArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JudgeArmorRenderer extends GeoArmorRenderer<JudgeArmorItem> {
    public JudgeArmorRenderer() {
        super(new JudgeArmorModel());
    }
}