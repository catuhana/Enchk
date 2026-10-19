package net.earthcomputer.clientcommands.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;

public final class MoreClientEntityEvents {
    public static final Event<AddEntity> POST_ADD = EventFactory.createArrayBacked(AddEntity.class, listeners -> packet -> {
        for (AddEntity listener : listeners) {
            listener.onAddEntity(packet);
        }
    });

    @FunctionalInterface
    public interface AddEntity {
        void onAddEntity(ClientboundAddEntityPacket packet);
    }
}
