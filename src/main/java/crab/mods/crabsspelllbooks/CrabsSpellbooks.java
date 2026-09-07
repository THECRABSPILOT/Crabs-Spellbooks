package crab.mods.crabsspelllbooks;

import com.mojang.logging.LogUtils;
import crab.mods.crabsspelllbooks.registry.BlockRegistry;
import crab.mods.crabsspelllbooks.registry.EntityRegistry;
import crab.mods.crabsspelllbooks.registry.ItemRegistry;
import crab.mods.crabsspelllbooks.registry.ModEffects;
import crab.mods.crabsspelllbooks.spells.CSSpellRegistries;
import crab.mods.crabsspelllbooks.spells.CSSpellRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import software.bernie.geckolib.event.GeoRenderEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CrabsSpellbooks.MODID)
public class CrabsSpellbooks {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "crabs_spellbooks";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "crabs_spellbooks" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "crabs_spellbooks" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "crabs_spellbooks:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // Creates a new BlockItem with the id "crabs_spellbooks:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    public static final RegistryObject<CreativeModeTab> CRABS_SPELLBOOKS_TAB = CREATIVE_MODE_TABS.register("crabs_spellbooks_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.crabs_spellbooks.crabs_spellbooks_tab"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.HASTUR_HELMET.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.ELDRITCH_GRIMOIRE.get());
                output.accept(ItemRegistry.ELECTRICIAN_MANUAL.get());
               // output.accept(ItemRegistry.TOME_OF_TEMPEST.get());
                output.accept(ItemRegistry.DICTIONARY.get());
                output.accept(ItemRegistry.HASTUR_HELMET.get());
                output.accept(ItemRegistry.HASTUR_CHESTPLATE.get());
                output.accept(ItemRegistry.HASTUR_LEGGINGS.get());
                output.accept(ItemRegistry.HASTUR_BOOTS.get());
                output.accept(ItemRegistry.GOLDEN_WEAVE.get());
                output.accept(ItemRegistry.YELLOW_RUNE.get());
                output.accept(ItemRegistry.ELDRITCH_UPGRADE_ORB_ITEM.get());
                output.accept(ItemRegistry.BEAM_BLOCK_ITEM.get());
                output.accept(ItemRegistry.STEAM_PUMP_ENTITY.get());
                output.accept(ItemRegistry.RIFT.get());
                output.accept(ItemRegistry.NOVA.get());
            }).build());

    public CrabsSpellbooks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        ModEffects.register(modEventBus);
        CSSpellRegistries.register(modEventBus);

        ItemRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);


        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }


}
