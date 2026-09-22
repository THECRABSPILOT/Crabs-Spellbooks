package crab.mods.crabsspelllbooks.items.renderer;

import crab.mods.crabsspelllbooks.items.MechanicalJetpackItem;
import crab.mods.crabsspelllbooks.items.model.MechanicalJetpackModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MechanicalJetpackItemRenderer extends GeoItemRenderer<MechanicalJetpackItem> {
    public MechanicalJetpackItemRenderer() {
        super(new MechanicalJetpackModel());
    }
}