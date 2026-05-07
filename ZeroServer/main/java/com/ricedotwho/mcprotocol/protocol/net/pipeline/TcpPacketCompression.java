package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import com.ricedotwho.mcprotocol.protocol.net.io.ByteBufNetInput;
import com.ricedotwho.mcprotocol.protocol.net.io.ByteBufNetOutput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class TcpPacketCompression extends ByteToMessageCodec<ByteBuf> {
    private static final int MAX_COMPRESSED_SIZE = 2097152;
    private final int threshold;
    private final Deflater deflater = new Deflater();
    private final Inflater inflater = new Inflater();
    private final byte[] buf = new byte[8192];

    public TcpPacketCompression(int threshold, boolean _a) {
        this.threshold = threshold;
    }

    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        int readable = in.readableBytes();
        ByteBufNetOutput output = new ByteBufNetOutput(out);
        if (readable < this.threshold) {
            output.writeVarInt(0);
            out.writeBytes(in);
        } else {
            byte[] bytes = new byte[readable];
            in.readBytes(bytes);
            output.writeVarInt(bytes.length);
            this.deflater.setInput(bytes, 0, readable);
            this.deflater.finish();

            while(!this.deflater.finished()) {
                int length = this.deflater.deflate(this.buf);
                output.writeBytes(this.buf, length);
            }

            this.deflater.reset();
        }

    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (buf.readableBytes() != 0) {
            ByteBufNetInput in = new ByteBufNetInput(buf);
            int size = in.readVarInt();
            if (size == 0) {
                ByteBuf slice = buf.readSlice(buf.readableBytes()).retain();
                out.add(slice);
            } else {
                if (size < this.threshold) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is below threshold of " + this.threshold + ".");
                }

                if (size > 2097152) {
                    throw new DecoderException("Badly compressed packet: size of " + size + " is larger than protocol maximum of " + 2097152 + ".");
                }

                byte[] bytes = new byte[buf.readableBytes()];
                in.readBytes(bytes);
                this.inflater.setInput(bytes);
                byte[] inflated = new byte[size];
                this.inflater.inflate(inflated);
                out.add(Unpooled.wrappedBuffer(inflated));
                this.inflater.reset();
            }
        }

    }

//    private static final int MAX_UNCOMPRESSED_SIZE = 8 * 1024 * 1024; // 8MiB
//    private final int threshold;
//    private final boolean validateDecompression;
//
//    private final ZlibCompression compression = new ZlibCompression();
//
//    public TcpPacketCompression(int threshold, boolean validateDecompression) {
//        this.threshold = threshold;
//        this.validateDecompression = validateDecompression;
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) {
//        compression.close();
//    }
//
//    @Override
//    public void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
//        int uncompressed = msg.readableBytes();
//        if (uncompressed > MAX_UNCOMPRESSED_SIZE) {
//            throw new IllegalArgumentException("Packet too big (is " + uncompressed + ", should be less than " + MAX_UNCOMPRESSED_SIZE + ")");
//        }
//
//        ByteBuf outBuf = ctx.alloc().directBuffer(uncompressed);
//        try {
//            if (uncompressed < threshold) {
//                // Under the threshold, there is nothing to do.
//                MinecraftTypes.writeVarInt(outBuf, 0);
//                outBuf.writeBytes(msg);
//            } else {
//                MinecraftTypes.writeVarInt(outBuf, uncompressed);
//                compression.deflate(msg, outBuf);
//            }
//
//            out.writeBytes(outBuf);
//        } finally {
//            ReferenceCountUtil.release(outBuf);
//        }
//    }
//
//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//
//        int claimedUncompressedSize = MinecraftTypes.readVarInt(in);
//        if (claimedUncompressedSize == 0) {
//            out.add(in.readRetainedSlice(in.readableBytes()));
//            return;
//        }
//
//        if (validateDecompression) {
//            if (claimedUncompressedSize < threshold) {
//                throw new DecoderException("Badly compressed packet - size of " + claimedUncompressedSize + " is below server threshold of " + threshold);
//            }
//
//            if (claimedUncompressedSize > MAX_UNCOMPRESSED_SIZE) {
//                throw new DecoderException("Badly compressed packet - size of " + claimedUncompressedSize + " is larger than protocol maximum of " + MAX_UNCOMPRESSED_SIZE);
//            }
//        }
//
//        ByteBuf uncompressed = ctx.alloc().directBuffer(claimedUncompressedSize);
//        try {
//            compression.inflate(in, uncompressed, claimedUncompressedSize);
//            out.add(uncompressed);
//        } catch (Exception e) {
//            ReferenceCountUtil.release(uncompressed);
//            throw new DecoderException("Failed to decompress packet", e);
//        }
//    }
}
