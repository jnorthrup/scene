package ua.org.jplayer.javformat.iso;

import ua.org.jplayer.javformat.iso.boxes.*;
import ua.org.jplayer.javformat.iso.model.Movie;
import ua.org.jplayer.javformat.iso.model.SampleInfo;
import ua.org.jplayer.javformat.iso.model.TrackInfo;
import ua.org.jplayer.javformat.iso.model.box.*;
import ua.org.jplayer.javformat.iso.model.domain.*;
import ua.org.jplayer.javformat.toolkit.YAIOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Demuxer for MP4 file
 * <p/>
 * Handles sample tables and extracts a middle level information about tracks
 * and samples within a file
 *
 * @author Stanislav Vitvitskiy
 */
public class ISODemux {
    private final InputStream is;
    protected Movie movie;
    protected NodeBox originalBox;
    protected LeafBox ftypBox;

    public ISODemux(InputStream is) {
        this.is = is;
    }

    public NodeBox getOriginalBox() {
        return originalBox;
    }

    public LeafBox getFtypBox() {
        return ftypBox;
    }

    public void parse() throws IOException {
        Atom read;
        do {
            read = Atom.read(is);
            if (read.getType() == AtomType.FILE_Atom_TYPE) {
                ftypBox = new LeafBox(read);
                ftypBox.parse(is);
            } else if (read.getType() != AtomType.MOVIE) {
                YAIOUtil.skipForSure(is, read.getBodySize());
            }
        } while (read.getType() != AtomType.MOVIE);

        originalBox = new NodeBox(read);
        originalBox.parse(is);

        readHeader(originalBox);
    }

    private void readHeader(NodeBox moov) throws IOException {
        MovieHeader movieHeader = ((MovieHeaderBox) Box.navigate(moov,
                new AtomType[]{AtomType.MOVIE_HEADER})).getMovieHeader();

        Collection traks = Box.navigateAll(moov, AtomType.TRACK);
        Collection<TrackInfo> tracks = new ArrayList<TrackInfo>();
        for (Object trak1 : traks) {
            Box trak = (Box) trak1;
            tracks.add(readTrack(trak));
        }

        this.movie = new Movie(movieHeader, tracks);
    }

    private TrackInfo readTrack(Box trak) throws IOException {
        Box stbl = Box.navigate(trak, new AtomType[]{AtomType.MEDIA, AtomType.MEDIA_INFO,
                AtomType.SAMPLE_TABLE});

        MediaHeader mediaHeader = ((MediaHeaderBox) Box.navigate(trak,
                new AtomType[]{AtomType.MEDIA, AtomType.MEDIA_HEADER})).getMediaHeader();

        TrackInfo trackInfo = ((TrackHeaderBox) Box.navigate(trak,
                new AtomType[]{AtomType.TRACK_HEADER})).getTrackInfo();

        trackInfo.setType(((HandlerBox) Box.navigate(trak, new AtomType[]{
                AtomType.MEDIA, AtomType.HANDLER})).getTrackType());

        TimeToSampleEntry[] timeToSample = ((TimeToSampleBox) Box.navigate(
                stbl, new AtomType[]{AtomType.TIME_TO_SAMPLE})).getTimeToSamples();

        SampleSizes sampleSizes = ((SampleSizesBox) Box.navigate(stbl,
                new AtomType[]{AtomType.SAMPLE_SIZE})).getSampleSizes();

        SampleToChunk[] sampleToChunk = ((SampleToChunkBox) Box.navigate(stbl,
                new AtomType[]{AtomType.SAMPLE_TO_CHUNK})).getSampleToChunk();

        long[] chunkOffsets = ((ChunkOffsetsBox) Box.navigate(stbl,
                new AtomType[]{AtomType.CHUNK_OFFSET})).getChunkOffsets();

        Box syncSampleBox = Box.navigate(stbl, new AtomType[]{AtomType.SYNCH_SAMPLE});
        long[] synchSample = null;
        if (syncSampleBox != null) {
            synchSample = ((SyncSamplesBox) syncSampleBox).getSyncSamples();
        }

        Collection<SampleInfo> flatSamples = new ArrayList<SampleInfo>();
        int curChunkSize = 0;
        int curSample = 0;
        int curDind = 0;
        for (int i = 0; i < chunkOffsets.length; i++) {
            for (SampleToChunk aSampleToChunk : sampleToChunk) {
                if (aSampleToChunk.getChunkNo() == i + 1) {
                    curChunkSize = (int) aSampleToChunk.getCount();
                    curDind = aSampleToChunk.getDInd();
                    break;
                }
            }
            int curOffset = 0;
            for (int j = 0; j < curChunkSize; j++) {
                long size;
                if (sampleSizes.getDefaultSize() > 0) {
                    size = sampleSizes.getDefaultSize();
                } else {
                    size = sampleSizes.getSizes()[curSample];
                }

                SampleInfo sampleInfo = new SampleInfo(curSample, curOffset
                        + chunkOffsets[i], size);
                sampleInfo.setSampleDescr(curDind);

                sampleInfo.setChunkNo(i);

                curOffset += size;
                curSample++;

                flatSamples.add(sampleInfo);
            }
        }

        SampleInfo[] flatSamplesArray = flatSamples.toArray(new SampleInfo[]{});

        int curSample1 = 0;
        int curTimestamp = 0;
        for (TimeToSampleEntry ttsEntry : timeToSample) {
            for (int i = 0; i < ttsEntry.getSampleCount(); i++) {
                flatSamplesArray[curSample1].setTimestamp(curTimestamp);
                flatSamplesArray[curSample1].setDuration(ttsEntry.getSampleDuration());
                curSample1++;
                curTimestamp += ttsEntry.getSampleDuration();
            }
        }

        if (synchSample != null) {
            for (long sampleNo : synchSample) {
                flatSamplesArray[(int) sampleNo - 1].setSyncSample(true);
            }
        } else {
            for (SampleInfo aFlatSamplesArray : flatSamplesArray) {
                aFlatSamplesArray.setSyncSample(true);
            }
        }

        trackInfo.setMediaHeader(mediaHeader);
        trackInfo.setSampleInfos(flatSamples);

        return trackInfo;
    }

    public Movie getMovie() {
        return movie;
    }

    public void printDebugInfo() {
        System.out.println(movie.getTracks().size() + " tracks");
        for (Object o1 : movie.getTracks()) {
            TrackInfo trackInfo = (TrackInfo) o1;
            System.out.println("Track " + trackInfo.getNo());

            System.out.println("Smp no.\tOffset\tSize\tTS\tDur\tSync\tChunk");

            for (Object o : trackInfo.getSampleInfos()) {
                SampleInfo sampleInfo = (SampleInfo) o;
                System.out.println(sampleInfo.getNo() + "\t"
                        + sampleInfo.getOffset() + "\t" + sampleInfo.getSize()
                        + "\t" + sampleInfo.getTimestamp() + "\t"
                        + sampleInfo.getDuration() + "\t"
                        + (sampleInfo.isSyncSample() ? "yes" : "") + "\t"
                        + sampleInfo.getChunkNo());
            }
        }

    }
}