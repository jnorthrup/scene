package ua.org.jplayer.javformat.iso.model.domain;

/**
 * A low-level information about sample sizes
 *
 * @author Stanislav Vitvitskiy
 */
public class SampleSizes {
    private final long defaultSize;
    private final long[] sizes;

    public SampleSizes(long fixedSample, long[] sizes) {
        this.defaultSize = fixedSample;
        this.sizes = sizes;
    }

    public long getDefaultSize() {
        return defaultSize;
    }

    public long[] getSizes() {
        return sizes;
    }
}