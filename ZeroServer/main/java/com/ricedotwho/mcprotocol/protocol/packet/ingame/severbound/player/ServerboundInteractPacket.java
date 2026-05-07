package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.player;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.Hand;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.InteractAction;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundInteractPacket extends Packet {
    private int entityId;
    private @NonNull InteractAction action;

    private float targetX;
    private float targetY;
    private float targetZ;
    private Hand hand;
    private boolean isSneaking;

    public ServerboundInteractPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundInteractPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.action = InteractAction.from(MinecraftTypes.readVarInt(in));
        if (this.action == InteractAction.INTERACT_AT) {
            this.targetX = in.readFloat();
            this.targetY = in.readFloat();
            this.targetZ = in.readFloat();
        } else {
            this.targetX = 0;
            this.targetY = 0;
            this.targetZ = 0;
        }

        if (this.action == InteractAction.INTERACT || this.action == InteractAction.INTERACT_AT) {
            this.hand = Hand.from(MinecraftTypes.readVarInt(in));
        } else {
            this.hand = null;
        }
        this.isSneaking = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeVarInt(out, this.action.ordinal());
        if (this.action == InteractAction.INTERACT_AT) {
            out.writeFloat(this.targetX);
            out.writeFloat(this.targetY);
            out.writeFloat(this.targetZ);
        }

        if (this.action == InteractAction.INTERACT || this.action == InteractAction.INTERACT_AT) {
            MinecraftTypes.writeVarInt(out, this.hand.ordinal());
        }
        out.writeBoolean(this.isSneaking);
    }
}
