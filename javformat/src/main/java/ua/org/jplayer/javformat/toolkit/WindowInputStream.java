package ua.org.jplayer.javformat.toolkit;

import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream that ensures all reads within a given window
 *
 * @author Stanislav Vitvitskiy
 */
public class WindowInputStream extends InputStream {
    private long remaining = 0;
    private long savedRemaining = 0;
    private InputStream proxied;

    public WindowInputStream(InputStream in, long remaining) {
        this.remaining = remaining;
        this.proxied = in;
    }

    public void openWindow(int size) {
        remaining = size;
    }

    public int read() throws IOException {
        if (remaining > 0) {
            --remaining;
            return proxied.read();
        } else {
            return -1;
        }
    }

    public synchronized void mark(int readlimit) {
        proxied.mark(readlimit);
        savedRemaining = remaining;
    }

    public boolean markSupported() {
        return proxied.markSupported();
    }

    public synchronized void reset() throws IOException {
        proxied.reset();
        remaining = savedRemaining;
    }

    public void skipRemaining() throws IOException {
        skip(remaining);
    }
}