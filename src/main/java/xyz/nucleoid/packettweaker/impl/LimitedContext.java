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

public record LimitedContext(@Nullable RegistryWrapper.WrapperLookup wrapperLookup, @Nullable GameProfile profile, @Nullable SyncedClientOptions options) implements PacketContext {

    @Override
    public @Nullable ServerPlayerEntity getPlayer() {
        return null;
    }

    @Override
    public @Nullable SyncedClientOptions getClientOptions() {
        return this.options;
    }

    @Override
    public @Nullable GameProfile getGameProfile() {
        return null;
    }

    @Override
    public @Nullable RegistryWrapper.WrapperLookup getRegistryWrapperLookup() {
        return this.wrapperLookup;
    }

    @Override
    public ContextProvidingPacketListener getPacketListener() {
        return ContextProvidingPacketListener.EMPTY;
    }

    @Override
    public @Nullable PacketListener getBackingPacketListener() {
        return null;
    }

    @Override
    public @Nullable ClientConnection getClientConnection() {
        return null;
    }

    @Override
    public @Nullable Packet<?> getEncodedPacket() {
        return null;
    }

    @Override
    public @Nullable NotNull asNotNull() {
        return null;
    }

    @Override
    public @Nullable PacketContext.NotNullWithPlayer asNotNullWithPlayer() {
        return null;
    }

    @Override
    public PacketContext copy() {
        return this;
    }
}
