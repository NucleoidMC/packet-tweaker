package xyz.nucleoid.packettweaker.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.ContextProvidingPacketListener;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Function;
import java.util.function.Supplier;

public final class MutableContext implements PacketContext.NotNullWithPlayer {
    private static final ThreadLocal<MutableContext> INSTANCE = ThreadLocal.withInitial(MutableContext::new);

    private ContextProvidingPacketListener target = ContextProvidingPacketListener.EMPTY;
    @Nullable
    private Packet<?> encodedPacket = null;
    @Nullable
    private ClientConnection connection = null;


    public static MutableContext get() {
        return INSTANCE.get();
    }

    public static void runWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Runnable runnable) {
        var context = get();
        var oldTarget = context.target;
        var oldPacket = context.encodedPacket;
        var oldConnection = context.connection;
        context.target = ContextProvidingPacketListener.of(networkHandler);
        context.encodedPacket = packet;
        context.connection = connection;
        try {
            runnable.run();
        } finally {
            context.target = oldTarget;
            context.encodedPacket = oldPacket;
            context.connection = oldConnection;
        }
    }

    public static <T> T supplyWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Supplier<T> supplier) {
        var context = get();
        var oldTarget = context.target;
        var oldPacket = context.encodedPacket;
        var oldConnection = context.connection;
        context.target = ContextProvidingPacketListener.of(networkHandler);
        context.encodedPacket = packet;
        context.connection = connection;
        try {
            return supplier.get();
        } finally {
            context.target = oldTarget;
            context.encodedPacket = oldPacket;
            context.connection = oldConnection;
        }
    }

    public static <T> T supplyWithContext(@Nullable ClientConnection connection, @Nullable PacketListener networkHandler, @Nullable Packet<?> packet, Function<PacketContext, T> supplier) {
        var context = get();
        var oldTarget = context.target;
        var oldPacket = context.encodedPacket;
        var oldConnection = context.connection;
        context.target = ContextProvidingPacketListener.of(networkHandler);
        context.encodedPacket = packet;
        context.connection = connection;
        try {
            return supplier.apply(context);
        } finally {
            context.target = oldTarget;
            context.encodedPacket = oldPacket;
            context.connection = oldConnection;
        }
    }

    @Override
    @Nullable
    public ServerPlayerEntity getPlayer() {
        return this.target.getPlayerForPacketTweaker();
    }
    @Override
    @Nullable
    public SyncedClientOptions getClientOptions() {
        return this.target.getClientOptionsForPacketTweaker();
    }
    @Override
    @Nullable
    public GameProfile getGameProfile() {
        return this.target.getGameProfileForPacketTweaker();
    }

    @Override
    public NotNullWithPlayer copy() {
        return this.connection != null ? new StaticPacketContext(this.connection) : EmptyContext.INSTANCE;
    }

    @Override
    @Nullable
    public RegistryWrapper.WrapperLookup getRegistryWrapperLookup() {
        return this.target.getWrapperLookupForPacketTweaker();
    }

    @Override
    public ContextProvidingPacketListener getPacketListener() {
        return this.target;
    }

    @Override
    @Nullable
    public PacketListener getBackingPacketListener() {
        return this.target != ContextProvidingPacketListener.EMPTY ? (PacketListener) this.target : null;
    }

    @Override
    @Nullable
    public ClientConnection getClientConnection() {
        return this.connection;
    }

    @Override
    @Nullable
    public Packet<?> getEncodedPacket() {
        return this.encodedPacket;
    }

    @Override
    public @Nullable NotNull asNotNull() {
        return this.getClientConnection() != null && this.getRegistryWrapperLookup() != null ? this : null;
    }

    @Override
    public @Nullable PacketContext.NotNullWithPlayer asNotNullWithPlayer() {
        return this.getPlayer() != null ? this : null;
    }


    public void clear() {
        this.target = ContextProvidingPacketListener.EMPTY;
        this.connection = null;
        this.encodedPacket = null;
    }

    public void set(ClientConnection connection, Packet<?> packet) {
        if (connection == null) {
            this.clear();
            return;
        }
        this.target = (ContextProvidingPacketListener) connection.getPacketListener();
        this.connection = connection;
        this.encodedPacket = packet;
    }

    public void set(PacketListener listener, Packet<?> packet) {
        if (listener == null) {
            this.clear();
            return;
        }
        this.target = (ContextProvidingPacketListener) listener;
        this.connection = this.target.getClientConnectionForPacketTweaker();
        this.encodedPacket = packet;
    }
}
