package crab.mods.crabsspelllbooks.items.renderer;

import crab.mods.crabsspelllbooks.items.TheNova;
import crab.mods.crabsspelllbooks.items.model.TheNovaModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TheNovaRenderer extends GeoItemRenderer<TheNova> {
    public TheNovaRenderer() {
        super(new TheNovaModel());
    }
}