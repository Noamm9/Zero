package com.ricedotwho.mcprotocol.protocol.packet.common.clientbound;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundResourcePushPacket extends Packet {
    private @NonNull UUID resourceId;
    private @NonNull String url;
    private @NonNull String hash;
    private boolean required;
    private @Nullable Component prompt;

    public ClientboundResourcePushPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundResourcePushPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.resourceId = MinecraftTypes.readUUID(in);
        this.url = MinecraftTypes.readString(in);
        this.hash = MinecraftTypes.readString(in);
        this.required = in.readBoolean();
        this.prompt = MinecraftTypes.readNullable(in, MinecraftTypes::readComponent);
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeUUID(out, this.resourceId);
        MinecraftTypes.writeString(out, this.url);
        MinecraftTypes.writeString(out, this.hash);
        out.writeBoolean(this.required);
        MinecraftTypes.writeNullable(out, this.prompt, MinecraftTypes::writeComponent);
    }
}
