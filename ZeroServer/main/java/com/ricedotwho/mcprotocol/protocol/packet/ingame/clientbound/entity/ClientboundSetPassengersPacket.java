package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundSetPassengersPacket extends Packet {
    private int entityId;
    private int @NonNull [] passengerIds;

    public ClientboundSetPassengersPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundSetPassengersPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.passengerIds = new int[MinecraftTypes.readVarInt(in)];
        for (int index = 0; index < this.passengerIds.length; index++) {
            this.passengerIds[index] = MinecraftTypes.readVarInt(in);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeVarInt(out, this.passengerIds.length);
        for (int entityId : this.passengerIds) {
            MinecraftTypes.writeVarInt(out, entityId);
        }
    }
}
