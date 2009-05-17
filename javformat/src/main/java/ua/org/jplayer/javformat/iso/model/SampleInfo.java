package ua.org.jplayer.javformat.iso.model;

/**
 * A middle-level information about a sample
 * <p/>
 * With binding to container
 *
 * @author Stanislav Vitvitskiy
 */
public class SampleInfo {
    private long no;
    private long offset;
    private final long size;
    private long timestamp;
    private long duration;
    private boolean syncSample;
    private int chunkNo;
    private int sampleDescr;

    public SampleInfo(long no, long offset, long size) {
        this.no = no;
        this.offset = offset;
        this.size = size;
    }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return size;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSyncSample(boolean syncSample) {
        this.syncSample = syncSample;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSyncSample() {
        return syncSample;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getSampleDescr() {
        return sampleDescr;
    }

    public void setSampleDescr(int sampleDescr) {
        this.sampleDescr = sampleDescr;
    }
}