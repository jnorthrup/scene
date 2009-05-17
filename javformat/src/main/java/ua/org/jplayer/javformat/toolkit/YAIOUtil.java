package ua.org.jplayer.javformat.toolkit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Contains varios input output helper methods
 *
 * @author Stanislav Vitvitskiy
 */
public class YAIOUtil {

    /**
     * Copies a given amount of bytes from input stream to output stream
     *
     * @param is
     * @param os
     * @param len
     * @throws IOException
     */
    public static long copy(InputStream is, OutputStream os, long len)
            throws IOException {
        byte[] buf = new byte[1024];
        long copied = 0;
        int read;
        while ((read = is.read(buf)) != 0 && copied < len) {
            long leftToCopy = len - copied;
            int toWrite = read < leftToCopy ? read : (int) leftToCopy;
            os.write(buf, 0, toWrite);
            copied += toWrite;
        }
        return copied;
    }

    /**
     * Skips a given amount of bytes from input stream. Ensures the exact amount
     * is skipped.
     *
     * @param is
     * @param len
     * @throws IOException
     */
    public static void skipForSure(InputStream is, long len) throws IOException {
        long leftToSkip = len;
        while (leftToSkip > 0) {
            long skiped = is.skip(leftToSkip);
            leftToSkip -= skiped;
        }
    }

    public static byte[] read(InputStream is, long len) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(is, out, len);
        return out.toByteArray();
    }
}
