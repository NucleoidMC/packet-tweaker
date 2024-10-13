package xyz.nucleoid.packettweaker.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.ContextProvidingPacketListener;
import xyz.nucleoid.packettweaker.PacketContext;

public record StaticPlayContext(ServerPlayNetworkHandler handler) implements PacketContext.NotNullWithPlayer {
    @Override
    public ServerPlayerEntity getPlayer() {
        return this.handler.player;
    }

    @Override
    public SyncedClientOptions getClientOptions() {
        return this.handler.player.getClientOptions();
    }

    @Override
    public @Nullable GameProfile getGameProfile() {
        return this.handler.player.getGameProfile();
    }

    @Nullable
    @Override
    public RegistryWrapper.WrapperLookup getRegistryWrapperLookup() {
        return this.handler.player.getRegistryManager();
    }

    @Override
    public ContextProvidingPacketListener getPacketListener() {
        return (ContextProvidingPacketListener) this.handler;
    }

    @Override
    public @Nullable PacketListener getBackingPacketListener() {
        return this.handler;
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
    public NotNull asNotNull() {
        return this;
    }

    @Override
    public PacketContext.NotNullWithPlayer asNotNullWithPlayer() {
        return this;
    }

    @Override
    public NotNullWithPlayer copy() {
        return this;
    }
}
