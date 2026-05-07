package com.ricedotwho.mcprotocol.protocol.packet.ingame.clientbound.entity;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.entity.attribute.Attribute;
import org.geysermc.mcprotocollib.protocol.data.game.entity.attribute.AttributeModifier;
import org.geysermc.mcprotocollib.protocol.data.game.entity.attribute.AttributeType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ClientboundUpdateAttributesPacket extends Packet {
    private int entityId;
    private @NonNull List<Attribute> attributes;

    public ClientboundUpdateAttributesPacket(ByteBuf data) {
        super(data);
    }

    public ClientboundUpdateAttributesPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.entityId = MinecraftTypes.readVarInt(in);
        this.attributes = new ArrayList<>();
        int length = MinecraftTypes.readVarInt(in);
        for (int index = 0; index < length; index++) {
            int attributeId = MinecraftTypes.readVarInt(in);
            double value = in.readDouble();
            List<AttributeModifier> modifiers = new ArrayList<>();
            int len = MinecraftTypes.readVarInt(in);
            for (int ind = 0; ind < len; ind++) {
                modifiers.add(new AttributeModifier(MinecraftTypes.readResourceLocation(in), in.readDouble(), MinecraftTypes.readModifierOperation(in)));
            }

            AttributeType type = AttributeType.Builtin.BUILTIN.get(attributeId);
            this.attributes.add(new Attribute(type, value, modifiers));
        }
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writeVarInt(out, this.entityId);
        MinecraftTypes.writeVarInt(out, this.attributes.size());
        for (Attribute attribute : this.attributes) {
            MinecraftTypes.writeVarInt(out, attribute.getType().getId());
            out.writeDouble(attribute.getValue());
            MinecraftTypes.writeVarInt(out, attribute.getModifiers().size());
            for (AttributeModifier modifier : attribute.getModifiers()) {
                MinecraftTypes.writeResourceLocation(out, modifier.getId());
                out.writeDouble(modifier.getAmount());
                MinecraftTypes.writeModifierOperation(out, modifier.getOperation());
            }
        }
    }
}
