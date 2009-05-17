package ua.org.jplayer.javformat.flv.model;

/**
 * FLV file header
 *
 * @author Stanislav Vitvitskiy
 */
public class FLVHeader {

    private int version;
    private boolean hasVideo;
    private boolean hasAudio;
    private long dataOffset;

    public FLVHeader(int version, boolean hasVideo, boolean hasAudio,
                     long dataOffset) {
        this.dataOffset = dataOffset;
        this.hasAudio = hasAudio;
        this.hasVideo = hasVideo;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public long getDataOffset() {
        return dataOffset;
    }
}
