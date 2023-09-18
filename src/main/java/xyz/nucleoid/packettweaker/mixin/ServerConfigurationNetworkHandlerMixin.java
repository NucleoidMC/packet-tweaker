package xyz.nucleoid.packettweaker.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.packettweaker.ContextProvidingPacketListener;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerConfigurationNetworkHandler.class)
public abstract class ServerConfigurationNetworkHandlerMixin implements ContextProvidingPacketListener {
    @Shadow protected abstract GameProfile getProfile();

    @Shadow private SyncedClientOptions syncedOptions;

    @Override
    public GameProfile getGameProfileForPacketTweaker() {
        return this.getProfile();
    }

    @Override
    public SyncedClientOptions getClientOptionsForPacketTweaker() {
        return this.syncedOptions;
    }
}
