package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundPaddleBoatPacket extends Packet {
    private boolean rightPaddleTurning;
    private boolean leftPaddleTurning;

    public ServerboundPaddleBoatPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundPaddleBoatPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.rightPaddleTurning = in.readBoolean();
        this.leftPaddleTurning = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeBoolean(this.rightPaddleTurning);
        out.writeBoolean(this.leftPaddleTurning);
    }
}
