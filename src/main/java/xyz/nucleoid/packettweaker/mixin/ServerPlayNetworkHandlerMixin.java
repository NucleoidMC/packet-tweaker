package xyz.nucleoid.packettweaker.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.packettweaker.PlayerProvidingPacketListener;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements PlayerProvidingPacketListener {
    @Shadow public ServerPlayerEntity player;

    @Override
    public @Nullable ServerPlayerEntity getPlayerForPacketTweaker() {
        return this.player;
    }
}
