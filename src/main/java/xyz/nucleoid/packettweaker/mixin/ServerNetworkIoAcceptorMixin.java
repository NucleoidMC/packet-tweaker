package xyz.nucleoid.packettweaker.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.packettweaker.ConnectionHolder;

@Mixin(targets = "net/minecraft/server/ServerNetworkIo$1")
public class ServerNetworkIoAcceptorMixin {
    @Inject(
            method = "initChannel(Lio/netty/channel/Channel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;setPacketListener(Lnet/minecraft/network/listener/PacketListener;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void initChannel(Channel channel, CallbackInfo ci, ClientConnection connection) {
        ConnectionHolder encoder = (ConnectionHolder) channel.pipeline().get("encoder");
        encoder.setConnection(connection);

        ConnectionHolder decoder = (ConnectionHolder) channel.pipeline().get("decoder");
        decoder.setConnection(connection);
    }
}
