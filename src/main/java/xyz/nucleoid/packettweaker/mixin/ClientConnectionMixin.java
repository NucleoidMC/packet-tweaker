package xyz.nucleoid.packettweaker.mixin;

import net.minecraft.network.*;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nucleoid.packettweaker.ClientConnectionWithHandler;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin implements ClientConnectionWithHandler {

    private ServerPlayNetworkHandler networkHandler;

    @Inject(method = "setPacketListener", at = @At("HEAD"))
    private void packetTweaker_setPacketListener(PacketListener listener, CallbackInfo ci) {
        if (listener instanceof ServerPlayNetworkHandler) {
            this.networkHandler = ((ServerPlayNetworkHandler) listener);
        } else {
            this.networkHandler = null;
        }
    }

    @Nullable
    @Override
    public ServerPlayNetworkHandler getNetworkHandler() {
        return this.networkHandler;
    }
}
