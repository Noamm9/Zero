package com.ricedotwho.mcprotocol.protocol.net.registry;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.IdentityHashMap;
import java.util.Map;

public class PacketRegistry {
    private final Int2ObjectMap<PacketDefinition<? extends Packet>> serverbound = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<PacketDefinition<? extends Packet>> clientbound = new Int2ObjectOpenHashMap<>();

    private final Map<Class<? extends Packet>, Integer> clientboundIds = new IdentityHashMap<>();
    private final Map<Class<? extends Packet>, Integer> serverboundIds = new IdentityHashMap<>();

    public final <T extends Packet> void register(int id, Class<T> packet, PacketFactory<T> constructor) {
        this.registerServerbound(id, packet, constructor);
        this.registerClientbound(id, packet, constructor);
    }

    public final void register(PacketDefinition<? extends Packet> definition) {
        this.registerServerbound(definition);
        this.registerClientbound(definition);
    }

    public final <T extends Packet> void registerServerbound(int id, Class<T> packet, PacketFactory<T> constructor) {
        this.registerServerbound(new PacketDefinition<>(id, packet, constructor));
    }

    public final void registerServerbound(PacketDefinition<? extends Packet> definition) {
        this.serverbound.put(definition.id(), definition);
        this.serverboundIds.put(definition.packetClass(), definition.id());
    }

    public final <T extends Packet> void registerClientbound(int id, Class<T> packet, PacketFactory<T> constructor) {
        this.registerClientbound(new PacketDefinition<>(id, packet, constructor));
    }

    public final void registerClientbound(PacketDefinition<? extends Packet> definition) {
        this.clientbound.put(definition.id(), definition);
        this.clientboundIds.put(definition.packetClass(), definition.id());
    }

    @SuppressWarnings("unchecked")
    public Packet createClientboundPacket(int id, ByteBuf buf) {
        PacketDefinition<?> definition = this.clientbound.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        }

        return definition.newInstance(buf);
    }

    public int getClientboundId(Class<? extends Packet> packetClass) {
        Integer packetId = this.clientboundIds.get(packetClass);
        if (packetId == null) {
            throw new IllegalArgumentException("Unregistered clientbound packet class: " + packetClass.getName());
        }

        return packetId;
    }

    public int getPacketId(PacketDirection direction, Packet packet) {
        if (direction.equals(PacketDirection.CLIENTBOUND)) {
            return getClientboundId(packet);
        }
        return getServerboundId(packet);
    }

    public int getClientboundId(Packet packet) {
        return getClientboundId(packet.getClass());
    }

    public Class<? extends Packet> getClientboundClass(int id) {
        PacketDefinition<?> definition = this.clientbound.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        }

        return definition.packetClass();
    }

    public Packet createPacket(PacketDirection direction, int id, ByteBuf buf) {
        if (direction.equals(PacketDirection.CLIENTBOUND)) {
            return createClientboundPacket(id, buf);
        }
        return createServerboundPacket(id, buf);
    }

    @SuppressWarnings("unchecked")
    public Packet createServerboundPacket(int id, ByteBuf buf) {
        PacketDefinition<?> definition = (PacketDefinition<?>) this.serverbound.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Invalid packet id: " + id);
        }

        return definition.newInstance(buf);
    }

    public int getServerboundId(Class<? extends Packet> packetClass) {
        Integer packetId = this.serverboundIds.get(packetClass);
        if (packetId == null) {
            throw new IllegalArgumentException("Unregistered serverbound packet class: " + packetClass.getName());
        }

        return packetId;
    }

    public int getServerboundId(Packet packet) {
        return getServerboundId(packet.getClass());
    }

}