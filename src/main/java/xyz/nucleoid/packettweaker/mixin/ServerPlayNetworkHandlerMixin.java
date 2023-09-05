package xyz.nucleoid.packettweaker.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_8791;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
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
    private class_8791 clientSettings;

    @Override
    public @Nullable ServerPlayerEntity getPlayerForPacketTweaker() {
        return this.player;
    }

    @Override
    public GameProfile getGameProfileForPacketTweaker() {
        return this.getProfile();
    }

    @Override
    public class_8791 getClientSettingsForPacketTweaker() {
        if (this.clientSettings == null) {
            this.clientSettings = this.player.method_53823();
        }
        return this.clientSettings;
    }

    @Inject(method = "onClientSettings", at = @At("TAIL"))
    private void clearCachedClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket, CallbackInfo ci) {
        this.clientSettings = null;
    }

}
