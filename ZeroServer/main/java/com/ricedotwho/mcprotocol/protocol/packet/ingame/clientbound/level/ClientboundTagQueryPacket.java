package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundTagQueryPacket extends Packet {
    private int transactionId;
    private @Nullable NbtMap nbt;

    public ClientboundTagQueryPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundTagQueryPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.transactionId = MinecraftTypes.readVarInt(in);
        this.nbt = MinecraftTypes.readCompoundTag(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.transactionId);
        MinecraftTypes.writeAnyTag(out, this.nbt);
    }
}
