package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.ClientCommand;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundClientCommandPacket extends Packet {
    private @NonNull ClientCommand request;

    public ServerboundClientCommandPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundClientCommandPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.request = ClientCommand.from(MinecraftTypes.readVarInt(in));
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.request.ordinal());
    }
}
