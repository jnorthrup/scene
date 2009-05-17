package ua.org.jplayer.javformat.iso.model.domain;

/**
 * A low-level information about movie
 *
 * @author Stanislav Vitvitskiy
 */
public class MovieHeader {
    private final long timescale;
    private final long duration;
    private final long nextTrackId;

    public MovieHeader(long timescale, long duration, long nextTrackId) {
        this.duration = duration;
        this.timescale = timescale;
        this.nextTrackId = nextTrackId;
    }

    public long getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return duration;
    }

    public long getNextTrackId() {
        return nextTrackId;
    }
}