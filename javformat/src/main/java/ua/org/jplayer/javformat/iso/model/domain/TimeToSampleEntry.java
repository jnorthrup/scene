package ua.org.jplayer.javformat.iso.model.domain;

/**
 * A low-level information about sample durations
 *
 * @author Stanislav Vitvitskiy
 */
public class TimeToSampleEntry {
    long sampleCount;
    long sampleDuration;

    public TimeToSampleEntry(long sampleCount, long sampleDuration) {
        this.sampleCount = sampleCount;
        this.sampleDuration = sampleDuration;
    }

    public long getSampleCount() {
        return sampleCount;
    }

    public long getSampleDuration() {
        return sampleDuration;
    }
}
