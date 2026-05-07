package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSwingPacket extends Packet {
    private @NonNull Hand hand;

    public ServerboundSwingPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSwingPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.hand = Hand.from(MinecraftTypes.readVarInt(in));

    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.hand.ordinal());
    }
}
