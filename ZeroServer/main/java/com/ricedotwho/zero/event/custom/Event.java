package com.ricedotwho.zero.event.custom;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    private boolean cancelled;
    private MinecraftClient client;
}
