package xyz.nucleoid.packettweaker;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerProvidingPacketListener {
    @Nullable
    default ServerPlayerEntity getPlayerForPacketTweaker() {
        return null;
    }

    @Nullable
    static ServerPlayerEntity getPlayer(PacketListener listener) {
        return ((PlayerProvidingPacketListener) listener).getPlayerForPacketTweaker();
    }
}
