package crab.mods.crabsspelllbooks.items;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.spell_books.SimpleAttributeSpellBook;

public class EldritchGrimoireBook extends SimpleAttributeSpellBook {

    public EldritchGrimoireBook() {
        // slots, rarity, attribute, +10% power, +200 mana
        super(
                12,
                SpellRarity.EPIC,
                AttributeRegistry.ELDRITCH_SPELL_POWER.get(),
                0.10,
                200
        );
    }
}