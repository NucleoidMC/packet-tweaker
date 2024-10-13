package xyz.nucleoid.packettweaker.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraft.network.handler.DecoderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;
import xyz.nucleoid.packettweaker.impl.MutableContext;

import java.util.List;

@Mixin(DecoderHandler.class)
public class PacketDecoderMixin implements ConnectionHolder {
    @Unique
    private ClientConnection connection;

    @Override
    public void packet_tweaker$setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    @Inject(method = "decode", at = @At("HEAD"))
    private void packetTweaker_setPacketContext(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        if (this.connection != null) {
            MutableContext.get().set(this.connection, null);
        }
    }

    @Inject(method = "decode", at = @At("RETURN"))
    private void packetTweaker_clearPacketContext(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list, CallbackInfo ci) {
        MutableContext.get().clear();
    }
}
