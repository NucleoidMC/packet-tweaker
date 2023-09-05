package xyz.nucleoid.packettweaker;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_8791;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public interface ContextProvidingPacketListener {
    ContextProvidingPacketListener EMPTY = new ContextProvidingPacketListener() {};

    @Nullable
    default ServerPlayerEntity getPlayerForPacketTweaker() {
        return null;
    }

    default class_8791 getClientSettingsForPacketTweaker() {
        return class_8791.method_53821();
    }

    default GameProfile getGameProfileForPacketTweaker() {
        return new GameProfile(Util.NIL_UUID, "");
    }

    @Nullable
    static ServerPlayerEntity getPlayer(PacketListener listener) {
        return ((ContextProvidingPacketListener) listener).getPlayerForPacketTweaker();
    }

    static class_8791 getClientSettings(PacketListener listener) {
        return ((ContextProvidingPacketListener) listener).getClientSettingsForPacketTweaker();
    }

    static GameProfile getGameProfile(PacketListener listener) {
        return ((ContextProvidingPacketListener) listener).getGameProfileForPacketTweaker();
    }
}
