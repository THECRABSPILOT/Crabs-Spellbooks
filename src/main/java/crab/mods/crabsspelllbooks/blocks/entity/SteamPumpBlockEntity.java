package crab.mods.crabsspelllbooks.blocks.entity;

import crab.mods.crabsspelllbooks.blocks.SteamPumpBlock;
import crab.mods.crabsspelllbooks.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SteamPumpBlockEntity extends BlockEntity {

    public SteamPumpBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.STEAM_PUMP_BLOCK_ENTITY.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SteamPumpBlockEntity be) {
        if (!state.getValue(SteamPumpBlock.POWERED)) {
            return;
        }

        RandomSource random = level.getRandom();

        // Adjust particle spawn frequency (spawns ~80% of ticks)
        if (random.nextFloat() < 0.8F) {
            Direction facing = state.getValue(SteamPumpBlock.FACING);

            // Calculate emission point based on facing direction offset
            double x = pos.getX() + 0.5D + facing.getStepX() * 0.55D;
            double y = pos.getY() + 0.5D + facing.getStepY() * 0.55D;
            double z = pos.getZ() + 0.5D + facing.getStepZ() * 0.55D;

            // Small velocity variation pushing outward from the nozzle direction
            double speedX = facing.getStepX() * 0.05D + (random.nextDouble() - 0.5D) * 0.02D;
            double speedY = facing.getStepY() * 0.05D + (random.nextDouble() - 0.5D) * 0.02D;
            double speedZ = facing.getStepZ() * 0.05D + (random.nextDouble() - 0.5D) * 0.02D;

            // Uses campfire smoke (use ParticleTypes.CAMPFIRE_SIGNAL_SMOKE for taller smoke columns)
            level.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    x, y, z,
                    speedX, speedY, speedZ
            );
        }
    }
}