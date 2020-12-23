package xyz.nucleoid.packettweaker.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketEncoder;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nucleoid.packettweaker.ClientConnectionWithHandler;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.packettweaker.ConnectionHolder;

import java.io.IOException;

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin implements ConnectionHolder {
    @Unique
    private ClientConnection connection;

    @Override
    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    @Redirect(method = "encode", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;write(Lnet/minecraft/network/PacketByteBuf;)V"))
    private void writePacket(Packet<?> packet, PacketByteBuf buf) throws IOException {
        ClientConnection connection = this.connection;
        if (connection != null) {
            ServerPlayNetworkHandler networkHandler = ((ClientConnectionWithHandler) connection).getNetworkHandler();
            PacketContext.writeWithContext(packet, buf, networkHandler);
        } else {
            packet.write(buf);
        }
    }
}
