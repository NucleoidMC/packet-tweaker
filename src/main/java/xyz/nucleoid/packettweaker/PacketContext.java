package xyz.nucleoid.packettweaker;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.impl.ConnectionClientAttachment;

public final class PacketContext {
    private static final ThreadLocal<PacketContext> INSTANCE = ThreadLocal.withInitial(PacketContext::new);

    private ContextProvidingPacketListener target = ContextProvidingPacketListener.EMPTY;
    @Nullable
    private Packet<?> encodedPacket = null;
    @Nullable
    private ClientConnection connection = null;

    public static PacketContext get() {
        return INSTANCE.get();
    }

    public static void runWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Runnable runnable) {
        PacketContext context = PacketContext.get();
        var oldTarget = context.target;
        var oldPacket = context.encodedPacket;
        var oldConnection = context.connection;
        context.target = ContextProvidingPacketListener.of(networkHandler);
        context.encodedPacket = packet;
        context.connection = connection;
        runnable.run();
        context.target = oldTarget;
        context.encodedPacket = oldPacket;
        context.connection = oldConnection;
    }
    public static void runWithContext(@Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Runnable runnable) {
        runWithContext(ContextProvidingPacketListener.getClientConnection(networkHandler), networkHandler, packet, runnable);
    }

    public static void runWithContext(@Nullable PacketListener networkHandler, Runnable runnable) {
        runWithContext(networkHandler, null, runnable);
    }

    public static PacketContext of(ServerPlayerEntity player) {
        return of(player.networkHandler);
    }

    public static PacketContext of(PacketListener listener) {
        var ctx = new PacketContext();
        ctx.target = (ContextProvidingPacketListener) listener;
        ctx.connection = ctx.target.getClientConnectionForPacketTweaker();
        return ctx;
    }

    public static PacketContext of(ClientConnection connection) {
        var ctx = new PacketContext();
        ctx.target = (ContextProvidingPacketListener) connection.getPacketListener();
        ctx.connection = connection;
        return ctx;
    }

    public static PacketContext of() {
        return new PacketContext();
    }

    @ApiStatus.Internal
    public static void setContext(@Nullable ClientConnection connection, @Nullable Packet<?> packet) {
        if (connection == null) {
            clearContext();
            return;
        }

        PacketContext context = PacketContext.get();
        context.target = (ContextProvidingPacketListener) connection.getPacketListener();
        context.connection = connection;
        context.encodedPacket = packet;
    }

    @ApiStatus.Internal
    public static void clearContext() {
        PacketContext context = PacketContext.get();
        context.target = ContextProvidingPacketListener.EMPTY;
        context.connection = null;
        context.encodedPacket = null;
    }

    @Nullable
    @Deprecated
    public ServerPlayerEntity getTarget() {
        return this.getPlayer();
    }

    @Nullable
    public ServerPlayerEntity getPlayer() {
        return this.target.getPlayerForPacketTweaker();
    }
    @Nullable
    public SyncedClientOptions getClientOptions() {
        return this.target.getClientOptionsForPacketTweaker();
    }
    @Nullable
    public GameProfile getGameProfile() {
        return this.target.getGameProfileForPacketTweaker();
    }

    @Nullable
    public RegistryWrapper.WrapperLookup getRegistryWrapperLookup() {
        return this.target.getWrapperLookupForPacketTweaker();
    }

    public ContextProvidingPacketListener getPacketListener() {
        return this.target;
    }

    @Nullable
    public PacketListener getBackingPacketListener() {
        return this.target != ContextProvidingPacketListener.EMPTY ? (PacketListener) this.target : null;
    }

    @Nullable
    public ClientConnection getClientConnection() {
        return this.connection;
    }

    @Nullable
    public <T> T getData(Key<T> key) {
        return getData(this.connection, key);
    }

    @Nullable
    public <T> T setData(Key<T> key, @Nullable T data) {
        return setData(this.connection, key, data);
    }

    @Nullable
    public Packet<?> getEncodedPacket() {
        return this.encodedPacket;
    }

    public static final class Key<T> {
        private final String id;
        private Key(String id) {
            this.id = id;
        }

        public static <T> Key<T> of(String id) {
            return new Key<>(id);
        }

        @Override
        public String toString() {
            return "Key[" + this.id +']';
        }
    }

    @Nullable
    public static <T> T getData(PacketListener listener, Key<T> key) {
        return getData(((ContextProvidingPacketListener) listener).getClientConnectionForPacketTweaker(), key);
    }

    @Nullable
    public static  <T> T setData(PacketListener listener, Key<T> key, @Nullable T data) {
        return setData(((ContextProvidingPacketListener) listener).getClientConnectionForPacketTweaker(), key, data);

    }

    @Nullable
    public static  <T> T getData(ClientConnection connection, Key<T> key) {
        if (connection == null) {
            return null;
        }
        return ((ConnectionClientAttachment) connection).packetTweaker$get(key);
    }

    @Nullable
    public static  <T> T setData(ClientConnection connection, Key<T> key, @Nullable T data) {
        if (connection == null) {
            return null;
        }
        return ((ConnectionClientAttachment) connection).packetTweaker$set(key, data);
    }
}
