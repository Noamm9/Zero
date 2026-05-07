package com.ricedotwho.mcprotocol.protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
public class Packet {
    private final ByteBuf rawData;
    protected boolean decoded = false;
    @Setter
    private boolean modified = false;

    public Packet(ByteBuf raw) {
        this.rawData = raw;
    }

    public Packet() {
        rawData = null;
    }

    public ByteBuf getRawData() {
        if (rawData == null) return null;
        return rawData.slice();
    }

    public void lazyDecode() {
        if (decoded) return;
        try {
            decode(getRawData());
            decoded = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decode the packet
     */
    public void decode(ByteBuf in) throws IOException {
        throw new IllegalStateException("Cannot decode raw unimplemented packet");
    }

    /**
     * Encode the packet
     */
    public void encode(ByteBuf out) {
        throw new IllegalStateException("Cannot encode raw unimplemented packet");
    }

    public void release() {
        ReferenceCountUtil.release(rawData);
    }
}
