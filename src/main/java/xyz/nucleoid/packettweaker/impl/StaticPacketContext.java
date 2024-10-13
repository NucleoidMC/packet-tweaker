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

public record StaticPacketContext(ClientConnection connection) implements PacketContext.NotNullWithPlayer {
    @Override
    public ServerPlayerEntity getPlayer() {
        return this.getPacketListener().getPlayerForPacketTweaker();
    }

    @Override
    public SyncedClientOptions getClientOptions() {
        return this.getPacketListener().getClientOptionsForPacketTweaker();
    }

    @Override
    public @Nullable GameProfile getGameProfile() {
        return this.getPacketListener().getGameProfileForPacketTweaker();
    }

    @Nullable
    @Override
    public RegistryWrapper.WrapperLookup getRegistryWrapperLookup() {
        return this.getPacketListener().getWrapperLookupForPacketTweaker();
    }

    @Override
    public ContextProvidingPacketListener getPacketListener() {
        return (ContextProvidingPacketListener) this.connection.getPacketListener();
    }

    @Override
    public @Nullable PacketListener getBackingPacketListener() {
        return this.connection.getPacketListener();
    }

    @Override
    public @Nullable ClientConnection getClientConnection() {
        return this.getPacketListener().getClientConnectionForPacketTweaker();
    }

    @Override
    public @Nullable Packet<?> getEncodedPacket() {
        return null;
    }

    @Override
    public @Nullable NotNull asNotNull() {
        return this.getRegistryWrapperLookup() != null ? this : null;
    }

    @Override
    public @Nullable PacketContext.NotNullWithPlayer asNotNullWithPlayer() {
        return this.getPlayer() != null ? this : null;
    }

    @Override
    public NotNullWithPlayer copy() {
        return this;
    }
}
