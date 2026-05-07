package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.object.Direction;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundUseItemOnPacket extends Packet {
    private @NonNull Vector3i position;
    private @NonNull Direction face;
    private @NonNull Hand hand;
    private float cursorX;
    private float cursorY;
    private float cursorZ;
    private boolean insideBlock;
    private boolean hitWorldBorder;
    private int sequence;

    public ServerboundUseItemOnPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundUseItemOnPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.hand = Hand.from(MinecraftTypes.readVarInt(in));
        this.position = MinecraftTypes.readPosition(in);
        this.face = MinecraftTypes.readDirection(in);
        this.cursorX = in.readFloat();
        this.cursorY = in.readFloat();
        this.cursorZ = in.readFloat();
        this.insideBlock = in.readBoolean();
        this.hitWorldBorder = in.readBoolean();
        this.sequence = MinecraftTypes.readVarInt(in);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.hand.ordinal());
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeDirection(out, this.face);
        out.writeFloat(this.cursorX);
        out.writeFloat(this.cursorY);
        out.writeFloat(this.cursorZ);
        out.writeBoolean(this.insideBlock);
        out.writeBoolean(this.hitWorldBorder);
        MinecraftTypes.writeVarInt(out, this.sequence);
    }
}
