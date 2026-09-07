package crab.mods.crabsspelllbooks.registry;

import crab.mods.crabsspelllbooks.CrabsSpellbooks;
import crab.mods.crabsspelllbooks.blocks.BeamBlock;
import crab.mods.crabsspelllbooks.blocks.RiftBlock;
import crab.mods.crabsspelllbooks.blocks.SteamPumpBlock;
import crab.mods.crabsspelllbooks.blocks.entity.BeamBlockEntity;
import crab.mods.crabsspelllbooks.blocks.entity.RiftBlockEntity;
import crab.mods.crabsspelllbooks.blocks.entity.SteamPumpBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CrabsSpellbooks.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CrabsSpellbooks.MODID);

    public static final RegistryObject<Block> BEAM_BLOCK = BLOCKS.register("beam_block",
            () -> new BeamBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .lightLevel(state -> 8)
            )
    );

    public static final RegistryObject<BlockEntityType<BeamBlockEntity>> BEAM_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("beam_block",
                    () -> BlockEntityType.Builder.of(BeamBlockEntity::new, BEAM_BLOCK.get()).build(null)
            );


    public static final RegistryObject<Block> STEAM_PUMP = BLOCKS.register("steam_pump",
            () -> new SteamPumpBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .lightLevel(state -> 8)
            )
    );

    public static final RegistryObject<BlockEntityType<SteamPumpBlockEntity>> STEAM_PUMP_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("steam_pump",
                    () -> BlockEntityType.Builder.of(SteamPumpBlockEntity::new, STEAM_PUMP.get()).build(null)
            );


    public static final RegistryObject<Block> RIFT = BLOCKS.register("rift",
            () -> new RiftBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .strength(200.0F, 6.0F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .lightLevel(state -> 8)
            )
    );

    public static final RegistryObject<BlockEntityType<RiftBlockEntity>> RIFT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rift",
                    () -> BlockEntityType.Builder.of(RiftBlockEntity::new, RIFT.get()).build(null)
            );


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }
}
