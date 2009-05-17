package ua.org.jplayer.javformat.iso;

import java.io.IOException;
import java.io.InputStream;

/**
 * A parser for MP4 file strunctures
 *
 * @author Stanislav Vitvitskiy
 */
public abstract class Parser {

    public static long readInt32(InputStream di) throws IOException {
        long b1 = di.read();
        long b2 = di.read();
        long b3 = di.read();
        long b4 = di.read();

        if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) {
            return -1;
        }

        return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
    }

    public static long readInt64(InputStream di) throws IOException {
        long b1 = di.read();
        long b2 = di.read();
        long b3 = di.read();
        long b4 = di.read();
        long b5 = di.read();
        long b6 = di.read();
        long b7 = di.read();
        long b8 = di.read();

        if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1 || b5 == -1
                || b6 == -1 || b7 == -1 || b8 == -1) {
            return -1;
        }

        return (b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24)
                + (b6 << 16) + (b7 << 8) + b8;
    }
}