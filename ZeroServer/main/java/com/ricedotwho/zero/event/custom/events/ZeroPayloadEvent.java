package com.ricedotwho.zero.event.custom.events;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.zero.event.custom.Event;
import lombok.Getter;
import net.kyori.adventure.key.Key;

@Getter
public class ZeroPayloadEvent extends Event {
    private final Key key;
    private final byte[] data;
    public ZeroPayloadEvent(MinecraftClient client, Key key, byte[] data) {
        setClient(client);
        this.key = key;
        this.data = data;
    }
}
