package xyz.nucleoid.packettweaker.mixin;

import net.minecraft.network.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.ClientConnectionWithHandler;
import xyz.nucleoid.packettweaker.ConnectionHolder;
import xyz.nucleoid.packettweaker.PacketContext;

import java.io.IOException;

@Mixin(DecoderHandler.class)
public class PacketDecoderMixin implements ConnectionHolder {
    @Unique
    private ClientConnection connection;

    @Override
    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    @Redirect(method = "decode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;read(Lnet/minecraft/network/PacketByteBuf;)V"))
    private void writePacket(Packet<?> packet, PacketByteBuf buf) throws IOException {
        ClientConnection connection = this.connection;
        if (connection != null) {
            ServerPlayNetworkHandler networkHandler = ((ClientConnectionWithHandler) connection).getNetworkHandler();
            PacketContext.readWithContext(packet, buf, networkHandler);
        } else {
            packet.read(buf);
        }
    }
}
