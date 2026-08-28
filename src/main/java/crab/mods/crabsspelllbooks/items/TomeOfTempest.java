package crab.mods.crabsspelllbooks.items;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.spell_books.SimpleAttributeSpellBook;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.UUID;

public class TomeOfTempest extends SimpleAttributeSpellBook {

    private static final UUID LIGHTNING_UUID = UUID.fromString("6f3a8b12-4c2d-4e9a-8f5b-1c3d5e7f9a0b");
    private static final UUID ICE_UUID = UUID.fromString("7e2a9b23-5d3e-5f0b-9a6c-2d4e6f8a1b1c");
    private static final UUID RES_UUID = UUID.fromString("4e0a9b23-5d3e-5f0b-9a6c-2d4e6f8a1b2c");
    public TomeOfTempest() {
        super(
                12, // Spell slots
                SpellRarity.EPIC, // Rarity
                createAttributes() // Custom map containing both stats
        );
    }

    private static Multimap<Attribute, AttributeModifier> createAttributes() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(AttributeRegistry.LIGHTNING_SPELL_POWER.get(), new AttributeModifier(
                LIGHTNING_UUID,
                "Lightning Spell Power",
                0.10,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));

        builder.put(AttributeRegistry.ICE_SPELL_POWER.get(), new AttributeModifier(
                ICE_UUID,
                "Ice Spell Power",
                0.10,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));

        builder.put(AttributeRegistry.SPELL_RESIST.get(), new AttributeModifier(
                RES_UUID,
                "Spell Resistance",
                0.10,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));

        return builder.build();
    }
}
