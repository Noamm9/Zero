package com.ricedotwho.mcprotocol.protocol.net.registry;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;


public class MinecraftPacketRegistry {
    private final Int2ObjectMap<PacketDefinition<? extends Packet>> clientboundPackets = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<PacketDefinition<? extends Packet>> serverboundPackets = new Int2ObjectOpenHashMap<>();

    private int nextClientboundId = 0x00;
    private int nextServerboundId = 0x00;

    public static MinecraftPacketRegistry builder() {
        return new MinecraftPacketRegistry();
    }

    public <T extends Packet> MinecraftPacketRegistry registerClientboundPacket(Class<T> packetClass, PacketFactory<T> factory) {
        this.clientboundPackets.put(nextClientboundId, new PacketDefinition<>(nextClientboundId, packetClass, factory));
        this.nextClientboundId++;
        return this;
    }

    public <T extends Packet> MinecraftPacketRegistry registerServerboundPacket(Class<T> packetClass, PacketFactory<T> factory) {
        this.serverboundPackets.put(nextServerboundId, new PacketDefinition<>(nextServerboundId, packetClass, factory));
        this.nextServerboundId++;
        return this;
    }

    public PacketRegistry build() {
        PacketRegistry codec = new PacketRegistry();
        for (Int2ObjectMap.Entry<PacketDefinition<? extends Packet>> entry : this.clientboundPackets.int2ObjectEntrySet()) {
            codec.registerClientbound(entry.getValue());
        }

        for (Int2ObjectMap.Entry<PacketDefinition<? extends Packet>> entry : this.serverboundPackets.int2ObjectEntrySet()) {
            codec.registerServerbound(entry.getValue());
        }

        return codec;
    }
}