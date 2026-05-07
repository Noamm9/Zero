package com.ricedotwho.mcprotocol.protocol.packet.ingame.severbound.inventory;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudburstmc.math.vector.Vector3i;
import org.geysermc.mcprotocollib.protocol.codec.MinecraftTypes;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.UpdateStructureBlockAction;
import org.geysermc.mcprotocollib.protocol.data.game.inventory.UpdateStructureBlockMode;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.StructureMirror;
import org.geysermc.mcprotocollib.protocol.data.game.level.block.StructureRotation;

@Getter
@Setter
@AllArgsConstructor
public class ServerboundSetStructureBlockPacket extends Packet {
    private static final int FLAG_IGNORE_ENTITIES = 0x01;
    private static final int FLAG_SHOW_AIR = 0x02;
    private static final int FLAG_SHOW_BOUNDING_BOX = 0x04;
    private static final int FLAG_STRICT = 0x08;

    private @NonNull Vector3i position;
    private @NonNull UpdateStructureBlockAction action;
    private @NonNull UpdateStructureBlockMode mode;
    private @NonNull String name;
    private @NonNull Vector3i offset;
    private @NonNull Vector3i size;
    private @NonNull StructureMirror mirror;
    private @NonNull StructureRotation rotation;
    private @NonNull String metadata;
    private float integrity;
    private long seed;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private boolean strict;

    public ServerboundSetStructureBlockPacket(ByteBuf data) {
        super(data);
    }

    public ServerboundSetStructureBlockPacket(Packet packet) {
        super(packet.getRawData());
    }

    @Override
    public void decode(ByteBuf in) {
        this.position = MinecraftTypes.readPosition(in);
        this.action = UpdateStructureBlockAction.from(MinecraftTypes.readVarInt(in));
        this.mode = UpdateStructureBlockMode.from(MinecraftTypes.readVarInt(in));
        this.name = MinecraftTypes.readString(in);
        this.offset = Vector3i.from(in.readByte(), in.readByte(), in.readByte());
        this.size = Vector3i.from(in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte());
        this.mirror = StructureMirror.from(MinecraftTypes.readVarInt(in));
        this.rotation = StructureRotation.from(MinecraftTypes.readVarInt(in));
        this.metadata = MinecraftTypes.readString(in);
        this.integrity = in.readFloat();
        this.seed = MinecraftTypes.readVarLong(in);

        int flags = in.readUnsignedByte();
        this.ignoreEntities = (flags & FLAG_IGNORE_ENTITIES) != 0;
        this.showAir = (flags & FLAG_SHOW_AIR) != 0;
        this.showBoundingBox = (flags & FLAG_SHOW_BOUNDING_BOX) != 0;
        this.strict = (flags & FLAG_STRICT) != 0;
    }

    @Override
    public void encode(ByteBuf out) {
        MinecraftTypes.writePosition(out, this.position);
        MinecraftTypes.writeVarInt(out, this.action.ordinal());
        MinecraftTypes.writeVarInt(out, this.mode.ordinal());
        MinecraftTypes.writeString(out, this.name);
        out.writeByte(this.offset.getX());
        out.writeByte(this.offset.getY());
        out.writeByte(this.offset.getZ());
        out.writeByte(this.size.getX());
        out.writeByte(this.size.getY());
        out.writeByte(this.size.getZ());
        MinecraftTypes.writeVarInt(out, this.mirror.ordinal());
        MinecraftTypes.writeVarInt(out, this.rotation.ordinal());
        MinecraftTypes.writeString(out, this.metadata);
        out.writeFloat(this.integrity);
        MinecraftTypes.writeVarLong(out, this.seed);

        int flags = 0;
        if (this.ignoreEntities) {
            flags |= FLAG_IGNORE_ENTITIES;
        }

        if (this.showAir) {
            flags |= FLAG_SHOW_AIR;
        }

        if (this.showBoundingBox) {
            flags |= FLAG_SHOW_BOUNDING_BOX;
        }

        if (this.strict) {
            flags |= FLAG_STRICT;
        }

        out.writeByte(flags);
    }
}
