package crab.mods.crabsspelllbooks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JetpackInputPacket {
    private final boolean jumpPressed;

    public JetpackInputPacket(boolean jumpPressed) {
        this.jumpPressed = jumpPressed;
    }

    public JetpackInputPacket(FriendlyByteBuf buf) {
        this.jumpPressed = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.jumpPressed);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getPersistentData().putBoolean("JetpackJumpPressed", this.jumpPressed);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}