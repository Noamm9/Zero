package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.BlockChangeEntry;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundBlockUpdatePacket extends Packet {
    private @NonNull BlockChangeEntry entry;

    public ClientboundBlockUpdatePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundBlockUpdatePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entry = new BlockChangeEntry(MinecraftTypes.readPosition(in), MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.entry.getPosition());
        MinecraftTypes.writeVarInt(out, this.entry.getBlock());
    }
}
