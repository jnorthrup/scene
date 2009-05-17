package ua.org.jplayer.javformat.flv.model;

/**
 * A model object to store an individual FLV video tag
 *
 * @author Stanislav Vitvitskiy
 */
public class VideoTag extends Tag {
    private FrameType frameType;
    private VideoCodec codec;
    private byte[] data;

    public VideoTag(long streamId, long timestamp) {
        super(TagType.VIDEO, streamId, timestamp);
    }

    public byte[] getData() {
        return data;
    }

    public FrameType getFrameType() {
        return frameType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }

    public VideoCodec getCodec() {
        return codec;
    }

    public void setCodec(VideoCodec codec) {
        this.codec = codec;
    }

}
