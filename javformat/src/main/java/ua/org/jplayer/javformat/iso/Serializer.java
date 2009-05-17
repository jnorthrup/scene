package ua.org.jplayer.javformat.iso;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A serializer for MP4 file structures
 *
 * @author Stanislav Vitvitskiy
 */
public class Serializer {
    public static void writeInt32(OutputStream dos, long value)
            throws IOException {
        long b1 = value >> 24 & 0xff;
        long b2 = value >> 16 & 0xff;
        long b3 = value >> 8 & 0xff;
        long b4 = value & 0xff;

        dos.write((byte) b1);
        dos.write((byte) b2);
        dos.write((byte) b3);
        dos.write((byte) b4);
    }

    public static void writeInt16(OutputStream dos, long value)
            throws IOException {
        long b1 = value >> 8 & 0xff;
        long b2 = value & 0xff;

        dos.write((byte) b1);
        dos.write((byte) b2);
    }

    public static void writeInt24(OutputStream dos, long value)
            throws IOException {
        long b1 = value >> 16 & 0xff;
        long b2 = value >> 8 & 0xff;
        long b3 = value & 0xff;

        dos.write((byte) b1);
        dos.write((byte) b2);
        dos.write((byte) b3);
    }

    public static void writeInt64(OutputStream dos, long value)
            throws IOException {
        long b1 = value >> 56 & 0xff;
        long b2 = value >> 48 & 0xff;
        long b3 = value >> 40 & 0xff;
        long b4 = value >> 32 & 0xff;
        long b5 = value >> 24 & 0xff;
        long b6 = value >> 16 & 0xff;
        long b7 = value >> 8 & 0xff;
        long b8 = value & 0xff;

        dos.write((byte) b1);
        dos.write((byte) b2);
        dos.write((byte) b3);
        dos.write((byte) b4);
        dos.write((byte) b5);
        dos.write((byte) b6);
        dos.write((byte) b7);
        dos.write((byte) b8);
    }
}