package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BinaryWriter {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public void writeUint8(int value) {
        baos.write(value & 0xFF);
    }

    public void writeUint16(int value) {
        baos.write((value >>> 8) & 0xFF);
        baos.write(value & 0xFF);
    }

    public void writeUint32(long value) {
        baos.write((int) ((value >>> 24) & 0xFF));
        baos.write((int) ((value >>> 16) & 0xFF));
        baos.write((int) ((value >>> 8) & 0xFF));
        baos.write((int) (value & 0xFF));
    }

    public void writeInt64(long value) {
        baos.write((int) ((value >>> 56) & 0xFF));
        baos.write((int) ((value >>> 48) & 0xFF));
        baos.write((int) ((value >>> 40) & 0xFF));
        baos.write((int) ((value >>> 32) & 0xFF));
        baos.write((int) ((value >>> 24) & 0xFF));
        baos.write((int) ((value >>> 16) & 0xFF));
        baos.write((int) ((value >>> 8) & 0xFF));
        baos.write((int) (value & 0xFF));
    }

    public void writeUUID(UUID uuid) {
        writeInt64(uuid.getMostSignificantBits());
        writeInt64(uuid.getLeastSignificantBits());
    }

    public void writeBytes(byte[] bytes) {
        try {
            baos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeUint16(bytes.length);
        writeBytes(bytes);
    }

    public byte[] toByteArray() {
        return baos.toByteArray();
    }
}