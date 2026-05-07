package com.ricedotwho.zero.event.packet;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import com.ricedotwho.mcprotocol.protocol.net.registry.PacketDirection;
import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PacketContext<T extends Packet> {
    private final T packet;
    private final MinecraftClient proxy;
    private final PacketDirection direction;
    private final List<Runnable> after = new ArrayList<>();
    @Setter
    private boolean cancelled = false;

    public PacketContext(T packet, MinecraftClient proxy, PacketDirection direction) {
        this.packet = packet;
        this.proxy = proxy;
        this.direction = direction;
    }

    public void after(Runnable ... runnable) {
        after.addAll(List.of(runnable));
    }

    public void complete() {
        after.forEach(Runnable::run);
    }
}