package com.ricedotwho.mcprotocol.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ByteBufUtils {

    public static void writeString(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readString(ByteBuf buf) {
        int length = readVarInt(buf);
        if (length < 0) return null;
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        do {
            byte temp = (byte)(value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= (byte) 0b10000000;
            }
            buf.writeByte(temp);
        } while (value != 0);
    }

    public static int readVarInt(ByteBuf buf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            if (!buf.isReadable()) return -1;

            read = buf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }

    public static int readVarInt(byte[] data) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        int numRead = 0;
        int result = 0;
        byte read;

        do {
            read = in.readByte();
            int value = read & 0b0111_1111;
            result |= value << (7 * numRead);

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((read & 0b1000_0000) != 0);

        return result;
    }

    public static void writeLong(ByteBuf buf, long value) {
        buf.writeLong(value);
    }

    public static long readLong(ByteBuf buf) {
        return buf.readLong();
    }

    public static void writeByte(ByteBuf buf, int value) {
        buf.writeByte(value);
    }

    public static byte readByte(ByteBuf buf) {
        return buf.readByte();
    }

    public static UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static String readString(byte[] data) {
        int index = 0;

        int length = 0;
        int numRead = 0;
        byte read;
        do {
            read = data[index++];
            int value = read & 0b01111111;
            length |= value << (7 * numRead);

            numRead++;
            if (numRead > 5) throw new RuntimeException("VarInt too big");
        } while ((read & 0b10000000) != 0);

        byte[] strBytes = new byte[length];
        System.arraycopy(data, index, strBytes, 0, length);

        return new String(strBytes, StandardCharsets.UTF_8);
    }

    public static byte[] writeString(String str) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        int length = strBytes.length;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeVarInt(out, length);

        out.writeBytes(strBytes);

        return out.toByteArray();
    }

    private static void writeVarInt(ByteArrayOutputStream out, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.write(value);
                return;
            }
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }
}
