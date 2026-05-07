package com.ricedotwho.zero.event.custom.events;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.zero.event.custom.Event;
import lombok.Getter;

@Getter
public class ServerTickEvent extends Event {
    private final long time;
    public ServerTickEvent(MinecraftClient client, long time) {
        setClient(client);
        this.time = time;
    }
}
