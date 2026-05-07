package com.ricedotwho.mcprotocol.protocol.packet.configuration.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.nbt.NbtMap;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundShowDialogueConfigurationPacket extends Packet {
    private NbtMap dialog;

    public ClientboundShowDialogueConfigurationPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundShowDialogueConfigurationPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.dialog = MinecraftTypes.readCompoundTag(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeAnyTag(out, this.dialog);
    }
}
