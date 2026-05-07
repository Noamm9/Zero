package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.level;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSignUpdatePacket extends Packet {
    private @NonNull Vector3i position;
    private @NonNull String[] lines;
    private boolean isFrontText;

    public ServerboundSignUpdatePacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSignUpdatePacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = MinecraftTypes.readPosition(in);
        this.isFrontText = in.readBoolean();
        this.lines = new String[4];
        for (int count = 0; count < this.lines.length; count++) {
            this.lines[count] = MinecraftTypes.readString(in);
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.position);
        out.writeBoolean(this.isFrontText);
        for (String line : this.lines) {
            MinecraftTypes.writeString(out, line);
        }
    }
}
