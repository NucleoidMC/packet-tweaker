package xyz.nucleoid.packettweaker.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.handler.PacketSizeLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(
            method = "addFlowControlHandler",
            at = @At("TAIL")
    )
    private void packetTweaker_initChannel(ChannelPipeline pipeline, CallbackInfo ci) {
        var self = (ClientConnection) (Object) this;
        ConnectionHolder encoder = (ConnectionHolder) pipeline.get("encoder");
        if (encoder != null) {
            encoder.setConnection(self);
        }
        ConnectionHolder decoder = (ConnectionHolder) pipeline.get("decoder");
        if (decoder != null) {
            decoder.setConnection(self);
        }
    }
}
