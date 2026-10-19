package io.github.catuhana.enchk.mixin.events;

import io.netty.channel.ChannelFutureListener;
import io.github.catuhana.enchk.event.ClientConnectionEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Shadow
    private volatile @Nullable PacketListener packetListener;

    @Shadow
    private PacketFlow receiving;

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void onSendPacket(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        if (this.receiving == PacketFlow.CLIENTBOUND && this.packetListener instanceof ClientPacketListener connection) {
            unpackBundle(packet, subPacket -> ClientConnectionEvents.SEND_PACKET.invoker().onSendPacket(connection, subPacket));
        }
    }

    @Unique
    private static void unpackBundle(Packet<?> packet, Consumer<Packet<?>> action) {
        if (packet instanceof BundlePacket<?> bundle) {
            bundle.subPackets().forEach(subPacket -> unpackBundle(subPacket, action));
        } else {
            action.accept(packet);
        }
    }
}
