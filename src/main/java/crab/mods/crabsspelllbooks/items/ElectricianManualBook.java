package crab.mods.crabsspelllbooks.items;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.spell_books.SimpleAttributeSpellBook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ElectricianManualBook extends SimpleAttributeSpellBook {

    public ElectricianManualBook() {
        // slots, rarity, attribute, +10% power, +200 mana
        super(
                12,
                SpellRarity.EPIC,
                AttributeRegistry.LIGHTNING_SPELL_POWER.get(),
                0.10,
                200
        );
    }
}