package xyz.nucleoid.packettweaker.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Shadow
    private Channel channel;

    @Inject(method = "transitionOutbound", at = @At("TAIL"))
    private void packetTweaker_initChannel(NetworkState<?> newState, CallbackInfo ci) {
        var self = (ClientConnection) (Object) this;
        ConnectionHolder encoder = (ConnectionHolder) this.channel.pipeline().get("encoder");
        if (encoder != null) {
            encoder.setConnection(self);
        }
    }

    @Inject(method = "transitionInbound", at = @At("TAIL"))
    private <T extends PacketListener> void packetTweaker_initChannel(NetworkState<T> state, T packetListener, CallbackInfo ci) {
        var self = (ClientConnection) (Object) this;
        ConnectionHolder encoder = (ConnectionHolder) this.channel.pipeline().get("encoder");
        if (encoder != null) {
            encoder.setConnection(self);
        }
    }

}
