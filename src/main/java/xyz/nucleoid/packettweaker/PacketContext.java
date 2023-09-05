package xyz.nucleoid.packettweaker;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_8791;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class PacketContext {
    private static final ThreadLocal<PacketContext> INSTANCE = ThreadLocal.withInitial(PacketContext::new);

    private ContextProvidingPacketListener target = ContextProvidingPacketListener.EMPTY;

    public static PacketContext get() {
        return INSTANCE.get();
    }

    public static void writeWithContext(Packet<?> packet, PacketByteBuf buffer, @Nullable PacketListener networkHandler) throws IOException {
        if (networkHandler == null) {
            packet.write(buffer);
            return;
        }

        PacketContext context = PacketContext.get();
        context.target = (ContextProvidingPacketListener) networkHandler;
    }
    @ApiStatus.Internal
    public static void setContext(@Nullable PacketListener listener) {
        if (listener == null) {
            return;
        }

        PacketContext context = PacketContext.get();
        context.target = (ContextProvidingPacketListener) listener;
    }

    public static void clearReadContext() {
        clearContext();
    }

    public static void clearContext() {
        PacketContext context = PacketContext.get();
        context.target = ContextProvidingPacketListener.EMPTY;
    }

    @Nullable
    @Deprecated
    public ServerPlayerEntity getTarget() {
        return this.getTargetPlayer();
    }

    @Nullable
    public ServerPlayerEntity getTargetPlayer() {
        return this.target.getPlayerForPacketTweaker();
    }

    public class_8791 getTargetSettings() {
        return this.target.getClientSettingsForPacketTweaker();
    }

    public GameProfile getTargetGameProfile() {
        return this.target.getGameProfileForPacketTweaker();
    }
}
