package crab.mods.crabsspelllbooks.items;

import crab.mods.crabsspelllbooks.items.renderer.MechanicalJetpackItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Consumer;

public class MechanicalJetpackItem extends Item implements GeoItem, ICurioItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MechanicalJetpackItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MechanicalJetpackItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new MechanicalJetpackItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    // --- CURIOS JETPACK FLYING LOGIC ---
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();

        if (entity instanceof Player player) {
            // Check key state (Server checks NBT, Client checks Minecraft keybind directly)
            boolean isJumping = player.level().isClientSide()
                    ? net.minecraft.client.Minecraft.getInstance().options.keyJump.isDown()
                    : player.getPersistentData().getBoolean("JetpackJumpPressed");

            if (isJumping) {
                player.fallDistance = 0.0F;

                // Propulsion force
                double flySpeed = 0.15D;
                double maxFlySpeed = 0.5D;

                Vec3 currentVel = player.getDeltaMovement();
                double newY = Math.min(currentVel.y + flySpeed, maxFlySpeed);

                // Apply upward velocity directly on both sides
                player.setDeltaMovement(currentVel.x, newY, currentVel.z);
                player.hasImpulse = true;

                // Server handles particles
                if (!player.level().isClientSide() && player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.FLAME,
                            player.getX(),
                            player.getY() + 0.2D,
                            player.getZ(),
                            3,
                            0.1D, 0.1D, 0.1D,
                            0.05D
                    );
                }
            }
        }
    }

    private boolean isJumpPressed(Player player) {
        return player.getPersistentData().getBoolean("JetpackJumpPressed");
    }


}