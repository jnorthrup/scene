package ua.org.jplayer.javformat.iso.model;

import ua.org.jplayer.javformat.iso.model.domain.MediaHeader;

import java.util.Collection;

/**
 * A middle-level inforamtion about track in movie file
 *
 * @author Stanislav Vitvitskiy
 */
public class TrackInfo {
    private Collection sampleInfos;
    private MediaHeader mediaHeader;
    private final int no;
    private final long duration;
    private final long width;
    private final long height;
    private int type;

    public TrackInfo(int no, long duration, long width, long height) {
        this.no = no;
        this.duration = duration;
        this.width = width;
        this.height = height;
    }

    public long getDuration() {
        return duration;
    }

    public long getWidth() {
        return width;
    }

    public void setSampleInfos(Collection sampleInfos) {
        this.sampleInfos = sampleInfos;
    }

    public void setMediaHeader(MediaHeader mediaHeader) {
        this.mediaHeader = mediaHeader;
    }

    public long getHeight() {
        return height;
    }

    public MediaHeader getMediaHeader() {
        return mediaHeader;
    }

    public Collection getSampleInfos() {
        return sampleInfos;
    }

    public int getNo() {
        return no;
    }

    public int getType() {
        return type;
    }

    public void setType(int codecType) {
        this.type = codecType;
    }
}