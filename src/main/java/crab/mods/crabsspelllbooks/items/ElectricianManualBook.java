package crab.mods.crabsspelllbooks.items;

import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ElectricianManualBook extends SpellBook {

    public ElectricianManualBook() {
        super(8, new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }
}