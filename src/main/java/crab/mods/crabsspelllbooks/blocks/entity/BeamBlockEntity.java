package crab.mods.crabsspelllbooks.blocks.entity;

import crab.mods.crabsspelllbooks.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BeamBlockEntity extends BlockEntity {

    public static final int BEAM_HEIGHT = 256;

    public static final float BASE_RADIUS = 0.8F;
    public static final float RADIUS_PER_MEMBER = 0.2F;
    public static final float MAX_RADIUS = 600.0F;

    private boolean master = true;
    private BlockPos masterPos;
    private final List<BlockPos> members = new ArrayList<>();

    // Default beam color (White)
    private float[] colorMultiplier = new float[]{1.0f, 1.0f, 1.0f};

    public BeamBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.BEAM_BLOCK_ENTITY.get(), pos, state);
        this.masterPos = pos.immutable();
        this.members.add(pos.immutable());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BeamBlockEntity be) {
        if (level != null && !level.isClientSide && be.isMaster()) {
            be.updateBeamColor();
        }
    }

    public boolean isMaster() {
        return master;
    }

    public List<BlockPos> getMembers() {
        return members;
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public float[] getColorMultiplier() {
        return colorMultiplier;
    }

    /**
     * Scans upward above the master block for Stained Glass (BeaconBeamBlock)
     * and calculates tinted color multipliers like vanilla beacons.
     */
    public void updateBeamColor() {
        if (level == null) return;

        BlockPos.MutableBlockPos cursor = worldPosition.mutable();
        float[] newColor = new float[]{1.0f, 1.0f, 1.0f}; // Default white
        boolean customColorApplied = false;

        for (int y = 1; y <= BEAM_HEIGHT; y++) {
            cursor.setY(worldPosition.getY() + y);
            BlockState state = level.getBlockState(cursor);

            if (state.getBlock() instanceof BeaconBeamBlock beaconBlock) {
                float[] glassColor = beaconBlock.getColor().getTextureDiffuseColors();
                if (!customColorApplied) {
                    newColor[0] = glassColor[0];
                    newColor[1] = glassColor[1];
                    newColor[2] = glassColor[2];
                    customColorApplied = true;
                } else {
                    // Blend colors sequentially when stacked
                    newColor[0] = (newColor[0] + glassColor[0]) / 2.0f;
                    newColor[1] = (newColor[1] + glassColor[1]) / 2.0f;
                    newColor[2] = (newColor[2] + glassColor[2]) / 2.0f;
                }
            }
        }

        if (!Arrays.equals(this.colorMultiplier, newColor)) {
            this.colorMultiplier = newColor;
            setChangedAndSync();
        }
    }

    public void onPlaced() {
        if (level == null || level.isClientSide) {
            return;
        }

        Set<BlockPos> connectedNetwork = discoverConnectedNetwork();

        BlockPos trueMasterPos = connectedNetwork.stream()
                .min((a, b) -> {
                    int x = Integer.compare(a.getX(), b.getX());
                    if (x != 0) return x;
                    int y = Integer.compare(a.getY(), b.getY());
                    if (y != 0) return y;
                    return Integer.compare(a.getZ(), b.getZ());
                })
                .orElse(worldPosition.immutable());

        if (level.getBlockEntity(trueMasterPos) instanceof BeamBlockEntity trueMaster) {
            trueMaster.updateNetwork(connectedNetwork);
        } else {
            updateNetwork(connectedNetwork);
        }
    }

    public void onBroken() {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos currentPos = worldPosition.immutable();
        List<BlockPos> remaining = new ArrayList<>(members);
        remaining.remove(currentPos);

        for (BlockPos pos : remaining) {
            if (level.getBlockEntity(pos) instanceof BeamBlockEntity member) {
                member.rebuildNetwork();
            }
        }
    }

    private Set<BlockPos> discoverConnectedNetwork() {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        BlockPos start = worldPosition.immutable();
        queue.add(start);
        visited.add(start);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        cursor.set(current.getX() + x, current.getY() + y, current.getZ() + z);
                        BlockPos neighborPos = cursor.immutable();

                        if (!visited.contains(neighborPos) && level.getBlockEntity(neighborPos) instanceof BeamBlockEntity) {
                            visited.add(neighborPos);
                            queue.add(neighborPos);
                        }
                    }
                }
            }
        }
        return visited;
    }

    public void rebuildNetwork() {
        Set<BlockPos> network = discoverConnectedNetwork();

        BlockPos trueMasterPos = network.stream()
                .min((a, b) -> {
                    int x = Integer.compare(a.getX(), b.getX());
                    if (x != 0) return x;
                    int y = Integer.compare(a.getY(), b.getY());
                    if (y != 0) return y;
                    return Integer.compare(a.getZ(), b.getZ());
                })
                .orElse(worldPosition.immutable());

        if (level != null && level.getBlockEntity(trueMasterPos) instanceof BeamBlockEntity trueMaster) {
            trueMaster.updateNetwork(network);
        }
    }

    public void updateNetwork(Set<BlockPos> network) {
        this.master = true;
        this.masterPos = worldPosition.immutable();
        this.members.clear();
        this.members.addAll(network);

        for (BlockPos pos : network) {
            if (!pos.equals(worldPosition) && level.getBlockEntity(pos) instanceof BeamBlockEntity member) {
                member.master = false;
                member.masterPos = worldPosition.immutable();
                member.members.clear();
                member.setChangedAndSync();
            }
        }
        setChangedAndSync();
    }

    public Vec3 getBeamCenter() {
        if (members.isEmpty()) {
            return Vec3.atCenterOf(worldPosition);
        }

        double sumX = 0, sumY = 0, sumZ = 0;
        for (BlockPos pos : members) {
            sumX += pos.getX() + 0.5;
            sumY += pos.getY();
            sumZ += pos.getZ() + 0.5;
        }

        return new Vec3(sumX / members.size(), sumY / members.size(), sumZ / members.size());
    }

    public float getBeamRadius() {
        float unconstrainedRadius = BASE_RADIUS + (members.size() - 1) * RADIUS_PER_MEMBER;
        float targetRadius = Math.min(MAX_RADIUS, unconstrainedRadius);

        if (level == null) {
            return targetRadius;
        }

        Vec3 center = getBeamCenter();
        double obstacleLimit = targetRadius;

        for (int deg = 0; deg < 360; deg += 45) {
            double rad = Math.toRadians(deg);
            Vec3 dir = new Vec3(Math.cos(rad), 0, Math.sin(rad));
            Vec3 rayEnd = center.add(dir.scale(targetRadius));

            HitResult hit = level.clip(new ClipContext(
                    center,
                    rayEnd,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    null
            ));

            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos hitPos = new BlockPos(
                        (int) Math.floor(hit.getLocation().x),
                        (int) Math.floor(hit.getLocation().y),
                        (int) Math.floor(hit.getLocation().z)
                );

                if (!members.contains(hitPos) && !(level.getBlockEntity(hitPos) instanceof BeamBlockEntity)) {
                    double dist = hit.getLocation().distanceTo(center);
                    if (dist < obstacleLimit) {
                        obstacleLimit = dist;
                    }
                }
            }
        }

        return (float) Math.max(BASE_RADIUS, obstacleLimit);
    }

    @Override
    public AABB getRenderBoundingBox() {
        float radius = getBeamRadius();
        Vec3 center = getBeamCenter();
        return new AABB(
                center.x - radius - 2.0,
                center.y - 2.0,
                center.z - radius - 2.0,
                center.x + radius + 2.0,
                center.y + BEAM_HEIGHT,
                center.z + radius + 2.0
        );
    }

    private void setChangedAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("Master", master);
        tag.put("MasterPos", NbtUtils.writeBlockPos(masterPos));

        tag.putFloat("ColorR", colorMultiplier[0]);
        tag.putFloat("ColorG", colorMultiplier[1]);
        tag.putFloat("ColorB", colorMultiplier[2]);

        ListTag list = new ListTag();
        for (BlockPos pos : members) {
            list.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("Members", list);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        master = tag.getBoolean("Master");
        masterPos = NbtUtils.readBlockPos(tag.getCompound("MasterPos"));

        if (tag.contains("ColorR")) {
            colorMultiplier = new float[]{
                    tag.getFloat("ColorR"),
                    tag.getFloat("ColorG"),
                    tag.getFloat("ColorB")
            };
        }

        members.clear();
        ListTag list = tag.getList("Members", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            members.add(NbtUtils.readBlockPos(list.getCompound(i)));
        }
        if (members.isEmpty()) {
            members.add(worldPosition.immutable());
        }
        if (masterPos == null) {
            masterPos = worldPosition.immutable();
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            load(pkt.getTag());
        }
    }
}