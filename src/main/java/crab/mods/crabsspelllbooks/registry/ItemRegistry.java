package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.items.*;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CrabsSpellbooks.MODID);

    public static final RegistryObject<Item> ELECTRICIAN_MANUAL =
            ITEMS.register("electrician_manual", ElectricianManualBook::new);
    public static final RegistryObject<Item> ELDRITCH_GRIMOIRE =
            ITEMS.register("eldritch_grimoire", EldritchGrimoireBook::new);
    public static final RegistryObject<Item> TOME_OF_TEMPEST =
            ITEMS.register("tome_of_the_tempest", TomeOfTempest::new);

    public static final RegistryObject<Item> DICTIONARY =
            ITEMS.register("garbled_text", Dictionary::new);

    public static final RegistryObject<Item> ELDRITCH_UPGRADE_ORB_ITEM = ITEMS.register("eldritch_upgrade_orb",
            () -> new UpgradeOrbItem(new Item.Properties().stacksTo(16), OrbRegistry.ELDRITCH_SPELL_POWER)
    );

    public static final RegistryObject<Item> HASTUR_HELMET = ITEMS.register("hastur_helmet",
            () -> new HasturArmorItem(ModArmorMaterials.HASTUR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> HASTUR_CHESTPLATE = ITEMS.register("hastur_chestplate",
            () -> new HasturArmorItem(ModArmorMaterials.HASTUR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> HASTUR_LEGGINGS = ITEMS.register("hastur_leggings",
            () -> new HasturArmorItem(ModArmorMaterials.HASTUR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> HASTUR_BOOTS = ITEMS.register("hastur_boots",
            () -> new HasturArmorItem(ModArmorMaterials.HASTUR, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> GOLDEN_WEAVE = ITEMS.register("golden_weave",
            ()-> new Item(new Item.Properties()));

    public static final RegistryObject<Item> YELLOW_RUNE = ITEMS.register("yellow_rune",
            ()-> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOKEN_OF_THE_THIRD = ITEMS.register("third_token",
            ()-> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COSMIC_GLASS = ITEMS.register("cosmic_glass",
            ()-> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BEAM_BLOCK_ITEM = ITEMS.register("beam_block",
            () -> new BlockItem(BlockRegistry.BEAM_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item>    STEAM_PUMP_ENTITY = ITEMS.register("steam_pump",
            () -> new BlockItem(BlockRegistry.STEAM_PUMP.get(), new Item.Properties()));

    public static final RegistryObject<Item>    RIFT = ITEMS.register("rift",
            () -> new BlockItem(BlockRegistry.RIFT.get(), new Item.Properties()));

    public static final RegistryObject<Item> NOVA = ITEMS.register("nova",
            ()-> new TheNova());

    public static final RegistryObject<Item> ABYSSALNOVA = ITEMS.register("abyssal_nova",
            ()-> new AbyssalNova());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}