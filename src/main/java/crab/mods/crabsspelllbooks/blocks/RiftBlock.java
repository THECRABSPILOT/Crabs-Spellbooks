package crab.mods.crabsspelllbooks.blocks;

import crab.mods.crabsspelllbooks.blocks.entity.RiftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RiftBlock extends BaseEntityBlock implements EntityBlock {
    public static final ResourceKey<Level> ENDER_PLANE_KEY = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation("crabs_spellbooks", "ender_plane")
    );

    public RiftBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RiftBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerLevel targetLevel = serverPlayer.getServer().getLevel(ENDER_PLANE_KEY);

                if (targetLevel != null) {
                    serverPlayer.teleportTo(
                            targetLevel,
                            0.5, 70.0, 0.5,
                            serverPlayer.getYRot(),
                            serverPlayer.getXRot()
                    );

                }
            }
            return InteractionResult.SUCCESS;
        }

        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}