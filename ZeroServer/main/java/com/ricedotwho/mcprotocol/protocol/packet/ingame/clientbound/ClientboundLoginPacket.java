package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.PlayerSpawnInfo;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundLoginPacket extends Packet {
    private int entityId;
    private boolean hardcore;
    private @NonNull Key[] worldNames;
    private int maxPlayers;
    private int viewDistance;
    private int simulationDistance;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean doLimitedCrafting;
    private PlayerSpawnInfo commonPlayerSpawnInfo;
    private boolean enforcesSecureChat;

    public ClientboundLoginPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundLoginPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = in.readInt();
        this.hardcore = in.readBoolean();
        int worldCount = MinecraftTypes.readVarInt(in);
        this.worldNames = new Key[worldCount];
        for (int i = 0; i < worldCount; i++) {
            this.worldNames[i] = MinecraftTypes.readResourceLocation(in);
        }
        this.maxPlayers = MinecraftTypes.readVarInt(in);
        this.viewDistance = MinecraftTypes.readVarInt(in);
        this.simulationDistance = MinecraftTypes.readVarInt(in);
        this.reducedDebugInfo = in.readBoolean();
        this.enableRespawnScreen = in.readBoolean();
        this.doLimitedCrafting = in.readBoolean();
        this.commonPlayerSpawnInfo = MinecraftTypes.readPlayerSpawnInfo(in);
        this.enforcesSecureChat = in.readBoolean();
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeInt(this.entityId);
        out.writeBoolean(this.hardcore);
        MinecraftTypes.writeVarInt(out, this.worldNames.length);
        for (Key worldName : this.worldNames) {
            MinecraftTypes.writeResourceLocation(out, worldName);
        }
        MinecraftTypes.writeVarInt(out, this.maxPlayers);
        MinecraftTypes.writeVarInt(out, this.viewDistance);
        MinecraftTypes.writeVarInt(out, this.simulationDistance);
        out.writeBoolean(this.reducedDebugInfo);
        out.writeBoolean(this.enableRespawnScreen);
        out.writeBoolean(this.doLimitedCrafting);
        MinecraftTypes.writePlayerSpawnInfo(out, this.commonPlayerSpawnInfo);
        out.writeBoolean(this.enforcesSecureChat);
    }
}
