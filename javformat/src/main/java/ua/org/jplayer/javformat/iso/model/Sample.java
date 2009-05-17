package ua.org.jplayer.javformat.iso.model;

/**
 * High-level information about a semple
 * <p/>
 * No binding to container information
 *
 * @author Stanislav Vitvitskiy
 */
public class Sample {
    private final int track;
    private final boolean syncSample;
    private final long timestamp;
    private final long duration;
    private final byte[] payload;

    public Sample(int track, long duration, boolean syncSample, long timestamp,
                  byte[] payload) {
        this.track = track;
        this.duration = duration;
        this.syncSample = syncSample;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public int getTrack() {
        return track;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSyncSample() {
        return syncSample;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getPayload() {
        return payload;
    }

}
