package ua.org.jplayer.javformat.flv.model;

/**
 * Base class for FLV tags
 *
 * @author Stanislav Vitvitskiy
 */
public abstract class Tag {
    private final TagType type;
    private final long streamId;
    private long timestamp;

    public Tag(TagType type, long streamId, long timestamp) {
        this.type = type;
        this.streamId = streamId;
        this.timestamp = timestamp;
    }

    public TagType getType() {
        return type;
    }

    public long getStreamId() {
        return streamId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
