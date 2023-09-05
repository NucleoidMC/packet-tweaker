package xyz.nucleoid.packettweaker.mixin;

import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import xyz.nucleoid.packettweaker.ContextProvidingPacketListener;

/*
 * It's intended to be empty, as that allows for quick casts
 */
@Mixin(PacketListener.class)
public interface PacketListenerMixin extends ContextProvidingPacketListener {
}
