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
public class ServerboundUseItemPacket extends Packet {
    private @NonNull Hand hand;
    private int sequence;
    private float yRot;
    private float xRot;

    public ServerboundUseItemPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundUseItemPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.hand = Hand.from(MinecraftTypes.readVarInt(in));
        this.sequence = MinecraftTypes.readVarInt(in);
        this.yRot = in.readFloat();
        this.xRot = in.readFloat();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.hand.ordinal());
        MinecraftTypes.writeVarInt(out, this.sequence);
        out.writeFloat(this.yRot);
        out.writeFloat(this.xRot);
    }
}
