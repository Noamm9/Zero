package com.ricedotwho.mcprotocol.protocol.net.pipeline;

import com.ricedotwho.mcprotocol.protocol.net.client.MinecraftClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.geysermc.mcprotocollib.network.NetworkConstants;
import org.geysermc.mcprotocollib.network.crypt.EncryptionConfig;

import java.util.List;

public class TcpPacketEncryptor extends ByteToMessageCodec<ByteBuf> {
    private final MinecraftClient client;
    private byte[] decryptedArray = new byte[0];
    private byte[] encryptedArray = new byte[0];

    public TcpPacketEncryptor(MinecraftClient client) {
        this.client = client;
    }

    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (this.client.getEncryption() != null) {
            int length = in.readableBytes();
            byte[] bytes = this.getBytes(in);
            int outLength = this.client.getEncryption().getEncryptOutputSize(length);
            if (this.encryptedArray.length < outLength) {
                this.encryptedArray = new byte[outLength];
            }

            out.writeBytes(this.encryptedArray, 0, this.client.getEncryption().encrypt(bytes, 0, length, this.encryptedArray, 0));
        } else {
            out.writeBytes(in);
        }

    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (this.client.getEncryption() != null) {
            int length = buf.readableBytes();
            byte[] bytes = this.getBytes(buf);
            ByteBuf result = ctx.alloc().heapBuffer(this.client.getEncryption().getDecryptOutputSize(length));
            result.writerIndex(this.client.getEncryption().decrypt(bytes, 0, length, result.array(), result.arrayOffset()));
            out.add(result);
        } else {
            out.add(buf.readBytes(buf.readableBytes()));
        }
    }

    private byte[] getBytes(ByteBuf buf) {
        int length = buf.readableBytes();
        if (this.decryptedArray.length < length) {
            this.decryptedArray = new byte[length];
        }

        buf.readBytes(this.decryptedArray, 0, length);
        return this.decryptedArray;
    }

//    private final MinecraftClient client;
//
//    public TcpPacketEncryptor(MinecraftClient client) {
//        this.client = client;
//    }
//
//    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
//        PacketEncryption encryption = this.client.getEncryption();
//
//        if (encryption == null) {
//            out.writeBytes(in);
//            return;
//        }
//
//        ByteBuf heapBuf = this.ensureHeapBuffer(ctx.alloc(), in);
//
//        int inBytes = heapBuf.readableBytes();
//        int baseOffset = heapBuf.arrayOffset() + heapBuf.readerIndex();
//
//        try {
//            encryption.encrypt(heapBuf.array(), baseOffset, inBytes, heapBuf.array(), baseOffset);
//            out.writeBytes(heapBuf);
//        } finally {
//            heapBuf.release();
//        }
//    }
//
//    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
//        PacketEncryption encryption = this.client.getEncryption();
//
//        if (encryption == null) {
//            out.add(buf.readBytes(buf.readableBytes()));
//            return;
//        }
//
//        ByteBuf heapBuf = this.ensureHeapBuffer(ctx.alloc(), buf).slice();
//
//        int inBytes = heapBuf.readableBytes();
//        int baseOffset = heapBuf.arrayOffset() + heapBuf.readerIndex();
//
//        try {
//            encryption.decrypt(heapBuf.array(), baseOffset, inBytes, heapBuf.array(), baseOffset);
//            out.add(heapBuf);
//            if (buf.hasArray()) buf.readerIndex(inBytes); // This is required as otherwise the ByteBuf doesn't know it has been read
//        } catch (Exception e) {
//            heapBuf.release();
//            throw e;
//        }
//    }
//
//    private ByteBuf ensureHeapBuffer(ByteBufAllocator alloc, ByteBuf buf) {
//        if (buf.hasArray()) {
//            return buf.retain();
//        } else {
//            ByteBuf heapBuf = alloc.heapBuffer(buf.readableBytes());
//            heapBuf.writeBytes(buf);
//            return heapBuf;
//        }
//    }
}