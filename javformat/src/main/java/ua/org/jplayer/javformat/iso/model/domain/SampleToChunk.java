package ua.org.jplayer.javformat.iso.model.domain;

/**
 * A low-level information about binding of samples to chunks
 *
 * @author Stanislav Vitvitskiy
 */
public class SampleToChunk {
    private final long chunkNo;
    private final long count;
    private final int dInd;

    public SampleToChunk(long chunkNo, long count, int ind) {
        this.chunkNo = chunkNo;
        this.count = count;
        dInd = ind;
    }

    public long getChunkNo() {
        return chunkNo;
    }

    public long getCount() {
        return count;
    }

    public int getDInd() {
        return dInd;
    }
}
