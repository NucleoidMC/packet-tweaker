package xyz.nucleoid.packettweaker.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.class_8791;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nucleoid.packettweaker.ContextProvidingPacketListener;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ServerConfigurationNetworkHandler.class)
public abstract class ServerConfigurationNetworkHandlerMixin implements ContextProvidingPacketListener {
    @Shadow protected abstract GameProfile getProfile();

    @Shadow private class_8791 field_46157;
    @Override
    public GameProfile getGameProfileForPacketTweaker() {
        return this.getProfile();
    }

    @Override
    public class_8791 getClientSettingsForPacketTweaker() {
        return this.field_46157;
    }
}
