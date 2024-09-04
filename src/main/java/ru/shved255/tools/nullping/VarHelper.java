package ru.shved255.tools.nullping;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class VarHelper {
    public static byte[] createHandshakeMessage(String host, int port) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(buffer);
        handshake.writeByte(0);
        handshake.write(1000);
        VarHelper.writeVarInt(handshake, 47);
        VarHelper.writeString(handshake, host, StandardCharsets.UTF_8);
        handshake.writeShort(port);
        VarHelper.writeVarInt(handshake, 1);
        return buffer.toByteArray();
    }

    public static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte[] bytes = string.getBytes(charset);
        VarHelper.writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static void writeVarInt2(DataOutputStream out, int value) throws IOException {
        do {
            byte temp = (byte)(value & 0x7F);
            if ((value >>>= 7) != 0) {
                temp = (byte)(temp | 0x80);
            }
            out.writeByte(temp);
        } while (value != 0);
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        byte k;
        int i = 0;
        int j = 0;
        do {
            k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((k & 0x80) == 128);
        return i;
    }
}

