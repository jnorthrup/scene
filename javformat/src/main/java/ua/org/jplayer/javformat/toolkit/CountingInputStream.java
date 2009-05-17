package ua.org.jplayer.javformat.toolkit;

import java.io.IOException;
import java.io.InputStream;

/**
 * An alternative implementation of CountingInputStream that supports mark/reset
 *
 * @author Stanislav Vitvitskiy
 */
public class CountingInputStream extends InputStream {
    private int count;
    private int mCount;
    private final InputStream in;

    public CountingInputStream(InputStream in) {
        this.in = in;
    }

    public int read(byte[] b) throws IOException {
        int found = in.read(b);
        this.count += found >= 0 ? found : 0;
        return found;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int found = in.read(b, off, len);
        this.count += found >= 0 ? found : 0;
        return found;
    }

    public int read() throws IOException {
        int found = in.read();
        this.count += found >= 0 ? 1 : 0;
        return found;
    }

    public long skip(final long length) throws IOException {
        final long skip = in.skip(length);
        this.count += skip;
        return skip;
    }

    public int getCount() {
        return this.count;
    }

    public synchronized int resetCount() {
        int tmp = this.count;
        this.count = 0;
        return tmp;
    }

    public synchronized void mark(int readlimit) {
        this.mCount = count;
        in.mark(readlimit);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public synchronized void reset() throws IOException {
        count = mCount;
        in.reset();
    }
}