package xyz.nucleoid.packettweaker.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.ContextProvidingPacketListener;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements ContextProvidingPacketListener {
    @Shadow public ServerPlayerEntity player;

    @Shadow protected abstract GameProfile getProfile();

    @Unique
    private SyncedClientOptions clientSettings;

    @Override
    public @Nullable ServerPlayerEntity getPlayerForPacketTweaker() {
        return this.player;
    }

    @Override
    public GameProfile getGameProfileForPacketTweaker() {
        return this.getProfile();
    }

    @Override
    public SyncedClientOptions getClientOptionsForPacketTweaker() {
        if (this.clientSettings == null) {
            this.clientSettings = this.player.getClientOptions();
        }
        return this.clientSettings;
    }

    @Inject(method = "onClientOptions", at = @At("TAIL"))
    private void clearCachedClientSettings(ClientOptionsC2SPacket clientSettingsC2SPacket, CallbackInfo ci) {
        this.clientSettings = null;
    }

}
