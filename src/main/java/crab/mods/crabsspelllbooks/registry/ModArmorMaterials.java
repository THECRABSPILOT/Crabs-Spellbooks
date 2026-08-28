package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    HASTUR("hastur",26 ,new int[] {10,12,13,10}, 25,
            SoundEvents.ARMOR_EQUIP_ELYTRA,1f,10f,null);

    private final String name;
    private final int durbilitymultiplier;
    private final int[] protamounts;
    private final int enchantmentvalue;
    private final SoundEvent equipsound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairmaterial;

    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

    ModArmorMaterials(String name, int durbilitymultiplier, int[] protamounts, int enchantmentvalue, SoundEvent equipsound, float toughness, float knockbackResistance, @Nullable Supplier<Ingredient> repairmaterial) {
        this.name = name;
        this.durbilitymultiplier = durbilitymultiplier;
        this.protamounts = protamounts;
        this.enchantmentvalue = enchantmentvalue;
        this.equipsound = equipsound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairmaterial = repairmaterial;
    }


    @Override
    public int getDurabilityForType(ArmorItem.Type p_266807_) {
        return BASE_DURABILITY[p_266807_.getSlot().getIndex()] * this.durbilitymultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type p_267168_) {
        return protamounts[p_267168_.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentvalue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipsound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairmaterial.get();
    }

    @Override
    public String getName() {
        return CrabsSpellbooks.MODID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
