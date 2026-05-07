package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.Holder;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundShowDialogueGamePacket extends Packet {
    private Holder<NbtMap> dialog;

    public ClientboundShowDialogueGamePacket(ByteBuf data) {
        super(data);
    }

    public ClientboundShowDialogueGamePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.dialog = MinecraftTypes.readHolder(in, MinecraftTypes::readCompoundTag);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeHolder(out, this.dialog, MinecraftTypes::writeAnyTag);
    }
}
