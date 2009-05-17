package ua.org.jplayer.javformat.iso.model.box;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An MP4 file structure (atom)
 *
 * @author Stanislav Vitvitskiy
 */
@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class Atom {

    private static int ATOM_PREAMBLE_SIZE = 8;

    private AtomType atomType;
    private long size;

    // private byte[] origType;
    public Atom(AtomType atomType) {
        this.atomType = atomType;
        // this.origType = origType;
    }

    public Atom(AtomType atomType, /* byte[] origType, */long size) {
        this.size = size;
        this.atomType = atomType;
        // this.origType = origType;
    }

    public void serialize(OutputStream dos) throws IOException {
        long b1 = size >> 24 & 0xff;
        long b2 = size >> 16 & 0xff;
        long b3 = size >> 8 & 0xff;
        long b4 = size & 0xff;

        dos.write((byte) b1);
        dos.write((byte) b2);
        dos.write((byte) b3);
        dos.write((byte) b4);

        dos.write(atomType.getSign().getBytes());
    }

    public static long readHeaderSize(InputStream di) throws IOException {
        long b1 = di.read();
        long b2 = di.read();
        long b3 = di.read();
        long b4 = di.read();

        if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) {
            return -1;
        }

        return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
    }

    public static Atom read(InputStream di) throws IOException {
        byte[] type = new byte[4];
        long size = readHeaderSize(di);
        if (size == -1) {
            return null;
        }
        di.read(type, 0, 4);

        return new Atom(AtomType.fromSign(type), size);
    }

    public void print() {
        System.out.println((atomType != null ? atomType.getSign() : "unknown") + ","
                + size);
    }

    public void skip(InputStream di) throws IOException {
        di.skip(size - ATOM_PREAMBLE_SIZE);
    }

    public byte[] readContents(InputStream di) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < size - ATOM_PREAMBLE_SIZE; i++) {
            baos.write(di.read());
        }
        return baos.toByteArray();
    }

    public AtomType getType() {
        return atomType;
    }

    public long getBodySize() {
        return size - ATOM_PREAMBLE_SIZE;
    }

    public void setBodySize(int length) {
        size = length + ATOM_PREAMBLE_SIZE;
    }
}
