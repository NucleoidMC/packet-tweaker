package xyz.nucleoid.packettweaker.mixin;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.state.NetworkState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.packettweaker.impl.ConnectionClientAttachment;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;

import java.util.IdentityHashMap;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements ConnectionClientAttachment {
    @Unique
    private final IdentityHashMap<PacketContext.Key<?>, Object> dataMap = new IdentityHashMap<>();

    @Shadow
    private Channel channel;

    @Inject(method = "transitionOutbound", at = @At("TAIL"))
    private void packetTweaker_initChannel(NetworkState<?> newState, CallbackInfo ci) {
        var self = (ClientConnection) (Object) this;
        ConnectionHolder encoder = (ConnectionHolder) this.channel.pipeline().get("encoder");
        if (encoder != null) {
            encoder.packet_tweaker$setConnection(self);
        }
    }

    @Inject(method = "transitionInbound", at = @At("TAIL"))
    private <T extends PacketListener> void packetTweaker_initChannel(NetworkState<T> state, T packetListener, CallbackInfo ci) {
        var self = (ClientConnection) (Object) this;
        ConnectionHolder decoder = (ConnectionHolder) this.channel.pipeline().get("decoder");
        if (decoder != null) {
            decoder.packet_tweaker$setConnection(self);
        }
    }

    @Override
    public <T> T packetTweaker$get(PacketContext.Key<T> key) {
        //noinspection unchecked
        return (T) this.dataMap.get(key);
    }

    @Override
    public <T> T packetTweaker$set(PacketContext.Key<T> key, T data) {
        if (data == null) {
            //noinspection unchecked
            return (T) this.dataMap.remove(key);
        }
        //noinspection unchecked
        return (T) this.dataMap.put(key, data);
    }
}
