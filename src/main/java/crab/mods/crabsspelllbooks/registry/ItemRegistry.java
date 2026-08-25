package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.items.ElectricianManualBook;
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
            ITEMS.register("eldritch_grimoire", ElectricianManualBook::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}