package io.github.catuhana.enchk.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;

public final class ClientConnectionEvents {
    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class, listeners -> () -> {
        for (Disconnect listener : listeners) {
            listener.onDisconnect();
        }
    });

    public static final Event<SendPacket> SEND_PACKET = EventFactory.createArrayBacked(SendPacket.class, listeners -> (connection, packet) -> {
        for (SendPacket listener : listeners) {
            listener.onSendPacket(connection, packet);
        }
    });

    @FunctionalInterface
    public interface Disconnect {
        void onDisconnect();
    }

    @FunctionalInterface
    public interface SendPacket {
        void onSendPacket(ClientPacketListener connection, Packet<?> packet);
    }
}
