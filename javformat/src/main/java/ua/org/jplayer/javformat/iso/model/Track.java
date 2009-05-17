package ua.org.jplayer.javformat.iso.model;

/**
 * A high-level information about a track
 * <p/>
 * Without binding to container
 *
 * @author Stanislav Vitvitskiy
 */
public class Track {
    private int no;
    private long timescale;

    public Track(int no, long timescale) {
        this.no = no;
        this.timescale = timescale;
    }

    public int getNo() {
        return no;
    }

    public long getTimescale() {
        return timescale;
    }
}