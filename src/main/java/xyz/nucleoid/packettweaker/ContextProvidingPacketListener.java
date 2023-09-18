package xyz.nucleoid.packettweaker;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
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
    static ServerPlayerEntity getPlayer(PacketListener listener) {
        return ((ContextProvidingPacketListener) listener).getPlayerForPacketTweaker();
    }

    @Nullable
    static SyncedClientOptions getClientOptions(PacketListener listener) {
        return ((ContextProvidingPacketListener) listener).getClientOptionsForPacketTweaker();
    }

    @Nullable
    static GameProfile getGameProfile(PacketListener listener) {
        return ((ContextProvidingPacketListener) listener).getGameProfileForPacketTweaker();
    }
}
