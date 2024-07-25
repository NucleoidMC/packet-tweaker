package xyz.nucleoid.packettweaker;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface ContextProvidingPacketListener {
    ContextProvidingPacketListener EMPTY = new ContextProvidingPacketListener() {};

    @Nullable
    default ServerPlayerEntity getPlayerForPacketTweaker() {
        return null;
    }

    @Nullable
    default SyncedClientOptions getClientOptionsForPacketTweaker() {
        return null;
    }

    @Nullable
    default GameProfile getGameProfileForPacketTweaker() {
        return null;
    }

    @Nullable
    default RegistryWrapper.WrapperLookup getWrapperLookupForPacketTweaker() { return null; }

    @Nullable
    default ClientConnection getClientConnectionForPacketTweaker() { return null; };

    static ContextProvidingPacketListener of(@Nullable PacketListener listener) {
        return listener != null ? ((ContextProvidingPacketListener) listener) : EMPTY;
    }

    @Nullable
    static ServerPlayerEntity getPlayer(@Nullable PacketListener listener) {
        return of(listener).getPlayerForPacketTweaker();
    }

    @Nullable
    static SyncedClientOptions getClientOptions(@Nullable PacketListener listener) {
        return of(listener).getClientOptionsForPacketTweaker();
    }

    @Nullable
    static GameProfile getGameProfile(@Nullable PacketListener listener) {
        return of(listener).getGameProfileForPacketTweaker();
    }

    @Nullable
    static RegistryWrapper.WrapperLookup getWrapperLookup(@Nullable PacketListener listener) {
        return of(listener).getWrapperLookupForPacketTweaker();
    }

    @Nullable
    static ClientConnection getClientConnection(@Nullable PacketListener listener) {
        return of(listener).getClientConnectionForPacketTweaker();
    }
}
