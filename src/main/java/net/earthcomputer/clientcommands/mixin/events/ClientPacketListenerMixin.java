package net.earthcomputer.clientcommands.mixin.events;

import net.earthcomputer.clientcommands.event.MoreClientEntityEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleAddEntity", at = @At("TAIL"))
    public void onHandleAddEntity(ClientboundAddEntityPacket packet, CallbackInfo ci) {
        MoreClientEntityEvents.POST_ADD.invoker().onAddEntity(packet);
    }
}
