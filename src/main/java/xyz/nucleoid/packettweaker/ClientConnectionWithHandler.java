package xyz.nucleoid.packettweaker;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;

public interface ClientConnectionWithHandler {
    @Nullable
    ServerPlayNetworkHandler getNetworkHandler();
}
