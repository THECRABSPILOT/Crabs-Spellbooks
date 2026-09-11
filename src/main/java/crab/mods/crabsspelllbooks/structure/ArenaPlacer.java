package crab.mods.crabsspelllbooks.structure;

import com.mojang.logging.LogUtils;
import crab.mods.crabsspelllbooks.blocks.RiftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber
public class ArenaPlacer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation ARENA =
            new ResourceLocation("crabs_spellbooks", "arena1");

    private static final int PLATFORM_THICKNESS = 1;
    private static final int DEFAULT_VOID_Y = 64;

    // Hard memory lock to strictly prevent multi-trigger stacking in ticks
    private static boolean HAS_PLACED = false;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || HAS_PLACED) return;

        if (event.level instanceof ServerLevel level) {
            placeIfNeeded(level);
        }
    }

    public static void placeIfNeeded(ServerLevel level) {
        if (HAS_PLACED) return;
        if (!level.dimension().equals(RiftBlock.ENDER_PLANE_KEY)) return;

        // Immediately lock execution so it CANNOT re-enter on the next tick
        HAS_PLACED = true;

        StructureTemplate template = level.getStructureManager()
                .get(ARENA)
                .orElse(null);

        if (template == null) {
            sendDebug(level, "§c[ArenaPlacer Error] Could not load template: " + ARENA);
            LOGGER.error("[ArenaPlacer] Failed to load structure template: {}", ARENA);
            return;
        }

        Vec3i size = template.getSize();

        // 1. Detect ground height dynamically at (0, 0)
        int detectedY = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, BlockPos.ZERO).getY();
        int originY = (detectedY <= level.getMinBuildHeight() + 1) ? DEFAULT_VOID_Y : detectedY;

        int originX = -size.getX() / 2;
        int originZ = -size.getZ() / 2;
        BlockPos origin = new BlockPos(originX, originY, originZ);

        // 2. Check if already built in world state
        if (level.getBlockState(origin.below()).is(Blocks.OBSIDIAN)) {
            sendDebug(level, "§e[ArenaPlacer] Platform already detected in world, skipping generation.");
            return;
        }

        sendDebug(level, "§e[ArenaPlacer] Starting single auto-placement...");

        // 3. Generate obsidian platform directly underneath
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int blocksPlaced = 0;

        for (int layer = 1; layer <= PLATFORM_THICKNESS; layer++) {
            int platformY = originY - layer;
            for (int x = 0; x < size.getX(); x++) {
                for (int z = 0; z < size.getZ(); z++) {
                    mutablePos.set(originX + x, platformY, originZ + z);
                    level.setBlock(mutablePos, Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_CLIENTS);
                    blocksPlaced++;
                }
            }
        }

        // 4. Place structure template
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        boolean success = template.placeInWorld(level, origin, origin, settings, level.random, Block.UPDATE_CLIENTS);

        if (success) {
            sendDebug(level, "§a[ArenaPlacer] Arena placed exactly ONCE successfully!");
        } else {
            sendDebug(level, "§c[ArenaPlacer] Failed to place structure template.");
        }
    }

    private static void sendDebug(ServerLevel level, String message) {
        level.players().forEach(player -> player.sendSystemMessage(Component.literal(message)));
    }
}