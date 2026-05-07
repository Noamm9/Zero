package com.ricedotwho.mcprotocol.protocol.event;

import com.ricedotwho.mcprotocol.protocol.event.events.PacketReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PacketReceiveBus {
    private final List<Consumer<PacketReceivedEvent>> listeners = new ArrayList<>();

    public void register(Consumer<PacketReceivedEvent> listener) {
        listeners.add(listener);
    }
    public void unregister(Consumer<PacketReceivedEvent> listener) {
        listeners.remove(listener);
    }

    public void post(PacketReceivedEvent event) {
        for (Consumer<PacketReceivedEvent> listener : this.listeners) {
            listener.accept(event);
        }
    }
}