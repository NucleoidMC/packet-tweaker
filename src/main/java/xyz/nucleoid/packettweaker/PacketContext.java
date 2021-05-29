package xyz.nucleoid.packettweaker;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public final class PacketContext {
    private static final ThreadLocal<PacketContext> INSTANCE = ThreadLocal.withInitial(PacketContext::new);

    private ServerPlayerEntity target;

    public static PacketContext get() {
        return INSTANCE.get();
    }

    public static void writeWithContext(Packet<?> packet, PacketByteBuf buffer, @Nullable ServerPlayNetworkHandler networkHandler) throws IOException {
        if (networkHandler == null) {
            packet.write(buffer);
            return;
        }

        PacketContext context = PacketContext.get();
        try {
            context.target = networkHandler.player;
            packet.write(buffer);
        } finally {
            context.target = null;
        }
    }

    public static void setReadContext(@Nullable ServerPlayNetworkHandler networkHandler) {
        if (networkHandler == null) {
            return;
        }

        PacketContext context = PacketContext.get();
        context.target = networkHandler.player;
    }

    public static void clearReadContext() {
        PacketContext context = PacketContext.get();
        context.target = null;
    }

    @Nullable
    public ServerPlayerEntity getTarget() {
        return this.target;
    }
}
