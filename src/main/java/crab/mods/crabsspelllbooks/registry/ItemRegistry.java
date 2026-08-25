package crab.mods.crabsspelllbooks.registry;


import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrabsSpellbooks.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
