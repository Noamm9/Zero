package com.ricedotwho.zero.event.custom.events;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.zero.event.custom.Event;

public class ClientTickEvent extends Event {
    public ClientTickEvent(MinecraftClient client) {
        setClient(client);
    }
}
