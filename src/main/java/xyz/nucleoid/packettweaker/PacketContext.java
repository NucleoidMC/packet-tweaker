package xyz.nucleoid.packettweaker;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.impl.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The general packet context. Might be full null or fully set.
 * It should NEVER be stored as a static variable (unless you copy it first), as it can be changed at any point.
 * The NotNull and NotNullWithPlayer are purely for api usage and don't guarantee they will stay that way
 * if the no storage requirement isn't fulfilled.
 */
@ApiStatus.NonExtendable
public interface PacketContext {
    static PacketContext get() {
        return MutableContext.get();
    }

    static void runWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Runnable runnable) {
        MutableContext.runWithContext(connection, networkHandler, packet, runnable);
    }

    static void runWithContext(@Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Runnable runnable) {
        runWithContext(ContextProvidingPacketListener.getClientConnection(networkHandler), networkHandler, packet, runnable);
    }

    static void runWithContext(@Nullable PacketListener networkHandler, Runnable runnable) {
        runWithContext(networkHandler, null, runnable);
    }

    static <T> T supplyWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Supplier<T> supplier) {
        return MutableContext.supplyWithContext(connection, networkHandler, packet, supplier);
    }

    static <T> T supplyWithContext(@Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Supplier<T> supplier) {
        return supplyWithContext(ContextProvidingPacketListener.getClientConnection(networkHandler), networkHandler, packet, supplier);
    }

    static <T> T supplyWithContext(@Nullable PacketListener networkHandler, Supplier<T> supplier) {
        return supplyWithContext(networkHandler, null, supplier);
    }

    static <T> T supplyWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Function<PacketContext, T> supplier) {
        return MutableContext.supplyWithContext(connection, networkHandler, packet, supplier);
    }

    static <T> T supplyWithContext(@Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Function<PacketContext, T> supplier) {
        return supplyWithContext(ContextProvidingPacketListener.getClientConnection(networkHandler), networkHandler, packet, supplier);
    }

    static <T> T supplyWithContext(@Nullable PacketListener networkHandler, Function<PacketContext, T> supplier) {
        return supplyWithContext(networkHandler, null, supplier);
    }

    static NotNullWithPlayer create(ServerPlayerEntity player) {
        return new StaticPlayContext(player.networkHandler);
    }

    static NotNullWithPlayer create(ServerPlayNetworkHandler listener) {
        return new StaticPlayContext(listener);
    }

    static NotNull create(PacketListener listener) {
        return new StaticPacketContext(ContextProvidingPacketListener.of(listener).getClientConnectionForPacketTweaker());
    }

    static NotNull create(ClientConnection connection) {
        return new StaticPacketContext(connection);
    }

    static PacketContext create(RegistryWrapper.WrapperLookup lookup) {
        return new LimitedContext(lookup, null, SyncedClientOptions.createDefault());
    }

    static PacketContext create() {
        return EmptyContext.INSTANCE;
    }

    @Nullable
    static <T> T getData(PacketListener listener, Key<T> key) {
        return getData(((ContextProvidingPacketListener) listener).getClientConnectionForPacketTweaker(), key);
    }

    @Nullable
    static <T> T setData(PacketListener listener, Key<T> key, @Nullable T data) {
        return setData(((ContextProvidingPacketListener) listener).getClientConnectionForPacketTweaker(), key, data);
    }

    @Nullable
    static <T> T getData(ClientConnection connection, Key<T> key) {
        if (connection == null) {
            return null;
        }
        return ((ConnectionClientAttachment) connection).packetTweaker$get(key);
    }

    @Nullable
    static <T> T setData(ClientConnection connection, Key<T> key, @Nullable T data) {
        if (connection == null) {
            return null;
        }
        return ((ConnectionClientAttachment) connection).packetTweaker$set(key, data);
    }
    @Nullable ServerPlayerEntity getPlayer();

    @Nullable SyncedClientOptions getClientOptions();

    @Nullable GameProfile getGameProfile();
    @Nullable RegistryWrapper.WrapperLookup getRegistryWrapperLookup();

    ContextProvidingPacketListener getPacketListener();

    @Nullable PacketListener getBackingPacketListener();

    @Nullable ClientConnection getClientConnection();

    @Nullable
    default <T> T getData(Key<T> key) {
        return PacketContext.getData(this.getClientConnection(), key);
    }

    @Nullable
    default <T> T setData(Key<T> key, @Nullable T data) {
        return PacketContext.setData(this.getClientConnection(), key, data);
    }
    @Nullable Packet<?> getEncodedPacket();


    @Nullable
    NotNull asNotNull();
    @Nullable
    PacketContext.NotNullWithPlayer asNotNullWithPlayer();

    PacketContext copy();


    /**
     * Purely for API usage, should never be storied statically unless copied beforehand.
     * Do not use instance of to check for this!
     */
    @ApiStatus.NonExtendable
    interface NotNullWithPlayer extends NotNull {
        @Override
        ServerPlayerEntity getPlayer();
        @Override
        SyncedClientOptions getClientOptions();
        @Override
        GameProfile getGameProfile();

        @Override
        NotNullWithPlayer copy();
    }

    /**
     * Purely for API usage, should never be storied statically unless copied beforehand.
     * Do not use instance of to check for this!
     */
    @ApiStatus.NonExtendable
    interface NotNull extends PacketContext {
        @Override
        RegistryWrapper.WrapperLookup getRegistryWrapperLookup();
        @Override
        ContextProvidingPacketListener getPacketListener();
        @Override
        PacketListener getBackingPacketListener();
        @Override
        ClientConnection getClientConnection();

        @Override
        NotNull copy();
    }


    final class Key<T> {
        private final String id;

        private Key(String id) {
            this.id = id;
        }

        public static <T> Key<T> of(String id) {
            return new Key<>(id);
        }

        @Override
        public String toString() {
            return "Key[" + this.id + ']';
        }
    }

    @Deprecated
    static PacketContext of(ServerPlayerEntity player) {
        return create(player.networkHandler);
    }

    @Deprecated
    static PacketContext of(PacketListener listener) {
        return create(listener);
    }

    @Deprecated
    static PacketContext of(ClientConnection connection) {
        return create(connection);
    }

    @Deprecated
    static PacketContext of() {
        return create();
    }

    @Nullable
    @Deprecated
    default ServerPlayerEntity getTarget() {
        return this.getPlayer();
    }
}
