package xyz.nucleoid.packettweaker.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.PacketContext;
import xyz.nucleoid.packettweaker.PlayerProvidingPacketListener;
import xyz.nucleoid.packettweaker.impl.ConnectionHolder;


@Mixin(PacketEncoder.class)
public class PacketEncoderMixin implements ConnectionHolder {
    @Unique
    private ClientConnection connection;

    @Override
    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;write(Lnet/minecraft/network/PacketByteBuf;)V", shift = At.Shift.BEFORE))
    private void packetTweaker_setPacketContext(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf, CallbackInfo ci) {
        if (this.connection != null) {
            PacketContext.setContext(PlayerProvidingPacketListener.getPlayer(connection.getPacketListener()));
        }
    }

    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;write(Lnet/minecraft/network/PacketByteBuf;)V", shift = At.Shift.AFTER))
    private void packetTweaker_clearPacketContext(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf, CallbackInfo ci) {
        PacketContext.clearContext();
    }
}
