package ua.org.jplayer.javformat.iso.model.domain;

/**
 * A low-level information about media
 *
 * @author Stainislav Vitvitskiy
 */
public class MediaHeader {
    private final long timescale;
    private final long duration;

    public MediaHeader(long timescale, long duration) {
        this.duration = duration;
        this.timescale = timescale;
    }

    public long getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return duration;
    }

}
