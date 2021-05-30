package xyz.nucleoid.packettweaker.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.*;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.ClientConnectionWithHandler;
import xyz.nucleoid.packettweaker.PacketContext;

import java.io.IOException;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements ClientConnectionWithHandler {
    @Shadow
    private Channel channel;
    @Shadow
    @Final
    private NetworkSide side;

    private ServerPlayNetworkHandler networkHandler;

    @Shadow
    public abstract boolean isLocal();

    @Inject(method = "setPacketListener", at = @At("HEAD"))
    private void setPacketListener(PacketListener listener, CallbackInfo ci) {
        if (listener instanceof ServerPlayNetworkHandler) {
            this.networkHandler = ((ServerPlayNetworkHandler) listener);
        } else {
            this.networkHandler = null;
        }
    }

    @Nullable
    @Override
    public ServerPlayNetworkHandler getNetworkHandler() {
        return this.networkHandler;
    }

    /**
     * Here we ensure that packets sent on the integrated server are serialized/deserialized. This is usually skipped
     * for optimization purposes. In the case of tweaking packets, however, we depend on the ability to hook and swap
     * values as they are sent to the client.
     */
    @ModifyVariable(method = "sendImmediately", at = @At("HEAD"), argsOnly = true, index = 1)
    private Packet<?> rewritePacket(Packet<?> packet) throws IOException {
        if (!this.isLocal() || this.side == NetworkSide.CLIENTBOUND) {
            return packet;
        }

        NetworkState protocol = this.channel.attr(ClientConnection.PROTOCOL_ATTRIBUTE_KEY).get();
        Integer packetId = protocol.getPacketId(this.side, packet);
        if (packetId == null) {
            return packet;
        }

        PacketByteBuf buffer = new PacketByteBuf(this.channel.alloc().buffer());
        try {
            PacketContext.writeWithContext(packet, buffer, this.networkHandler);
            buffer.resetReaderIndex();

            Packet<?> rewrittenPacket = protocol.getPacketHandler(this.side, packetId, buffer);
            if (rewrittenPacket == null) {
                return packet;
            }

            return rewrittenPacket;
        } finally {
            buffer.release();
        }
    }
}
