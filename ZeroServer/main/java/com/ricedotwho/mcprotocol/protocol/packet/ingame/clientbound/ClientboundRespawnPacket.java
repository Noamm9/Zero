package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerSpawnInfo;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundRespawnPacket extends Packet {
    private static final byte KEEP_ATTRIBUTE_MODIFIERS = 1;
    private static final byte KEEP_ENTITY_DATA = 2;

    private PlayerSpawnInfo commonPlayerSpawnInfo;
    // The following two are the dataToKeep byte
    private boolean keepMetadata;
    private boolean keepAttributeModifiers;

    public ClientboundRespawnPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundRespawnPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.commonPlayerSpawnInfo = MinecraftTypes.readPlayerSpawnInfo(in);
        byte dataToKeep = in.readByte();
        this.keepAttributeModifiers = (dataToKeep & KEEP_ATTRIBUTE_MODIFIERS) != 0;
        this.keepMetadata = (dataToKeep & KEEP_ENTITY_DATA) != 0;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePlayerSpawnInfo(out, this.commonPlayerSpawnInfo);
        byte dataToKeep = 0;
        if (this.keepMetadata) {
            dataToKeep += KEEP_ENTITY_DATA;
        }
        if (this.keepAttributeModifiers) {
            dataToKeep += KEEP_ATTRIBUTE_MODIFIERS;
        }
        out.writeByte(dataToKeep);
    }
}
