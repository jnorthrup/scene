package ua.org.jplayer.javformat.iso;

import ua.org.jplayer.javformat.iso.boxes.*;
import ua.org.jplayer.javformat.iso.model.Movie;
import ua.org.jplayer.javformat.iso.model.SampleInfo;
import ua.org.jplayer.javformat.iso.model.TrackInfo;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.NodeBox;
import ua.org.jplayer.javformat.iso.model.domain.SampleSizes;
import ua.org.jplayer.javformat.iso.model.domain.SampleToChunk;
import ua.org.jplayer.javformat.iso.model.domain.TimeToSampleEntry;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * MP4 muxer
 * <p/>
 * Transforms a middle-level information about tracks and samples into a valid
 * movie header box (moov).
 *
 * @author Stanislav Vitvitskiy
 */
public class ISOMux {

    public static NodeBox composeMovieBox(Movie movie) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(buffer);
        NodeBox moovBox = new NodeBox(new Atom(AtomType.MOVIE));

        moovBox.addBox(new MovieHeaderBox(movie.getMovieHeader()));

        Iterator tit = movie.getTracks().iterator();
        while (tit.hasNext()) {
            TrackInfo track = (TrackInfo) tit.next();

            NodeBox stblBox = new NodeBox(new Atom(AtomType.SAMPLE_TABLE));

            stblBox.addBox(new TimeToSampleBox(
                    assembleTimeToSample(track.getSampleInfos())));

            stblBox.addBox(new SampleSizesBox(
                    assembleSampleSizes(track.getSampleInfos())));

            stblBox.addBox(new SampleToChunkBox(
                    assempleSampleToChunk(track.getSampleInfos())));

            stblBox.addBox(new ChunkOffsetsBox(
                    assembleChunkOffsets(track.getSampleInfos())));

            stblBox.addBox(new SyncSamplesBox(
                    assembleSyncSamples(track.getSampleInfos())));

            NodeBox minfBox = new NodeBox(new Atom(AtomType.MEDIA_INFO));
            minfBox.addBox(stblBox);

            NodeBox mdiaBox = new NodeBox(new Atom(AtomType.MEDIA));
            mdiaBox.addBox(new MediaHeaderBox(track.getMediaHeader()));
            mdiaBox.addBox(new HandlerBox(track.getType()));
            mdiaBox.addBox(minfBox);
            NodeBox trackBox = new NodeBox(new Atom(AtomType.TRACK));
            trackBox.addBox(new TrackHeaderBox(track));
            trackBox.addBox(mdiaBox);

            moovBox.addBox(trackBox);

        }
        return moovBox;
    }

    public static void writeIndex(OutputStream out, Movie movie)
            throws IOException {
        composeMovieBox(movie).serialize(out);
    }

    private static TimeToSampleEntry[] assembleTimeToSample(
            Collection sampleInfos) {
        Collection timeToSampleEntries = new ArrayList();
        long lastDuration = -1;
        int count = 0;
        Iterator it = sampleInfos.iterator();
        while (it.hasNext()) {
            SampleInfo sampleInfo = (SampleInfo) it.next();
            if (lastDuration == -1) {
                lastDuration = sampleInfo.getDuration();
            }
            if (lastDuration != sampleInfo.getDuration()) {
                timeToSampleEntries.add(new TimeToSampleEntry(count,
                        lastDuration));
                count = 0;
                lastDuration = sampleInfo.getDuration();
            }
            count++;
        }
        timeToSampleEntries.add(new TimeToSampleEntry(count, lastDuration));
        return (TimeToSampleEntry[]) timeToSampleEntries.toArray(new TimeToSampleEntry[]{});
    }

    private static long[] assembleChunkOffsets(Collection sampleInfos) {
        ArrayList chunkOffsets = new ArrayList();
        int prefChunkNo = -1;
        Iterator it = sampleInfos.iterator();
        while (it.hasNext()) {
            SampleInfo sampleInfo = (SampleInfo) it.next();

            if (prefChunkNo == -1 || prefChunkNo != sampleInfo.getChunkNo()) {
                prefChunkNo = sampleInfo.getChunkNo();
                chunkOffsets.add(new Long(sampleInfo.getOffset()));
            }
        }
        long[] result = new long[chunkOffsets.size()];
        Iterator iterator = chunkOffsets.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            result[i] = ((Long) iterator.next()).longValue();
            i++;
        }

        return result;
    }

    private static SampleToChunk[] assempleSampleToChunk(Collection sampleInfos) {
        ArrayList uncompressed = new ArrayList();
        int prefChunkNo = -1;
        int prefInd = -1;
        int count = 0;
        Iterator it1 = sampleInfos.iterator();
        while (it1.hasNext()) {
            SampleInfo sampleInfo = (SampleInfo) it1.next();
            if (prefChunkNo == -1) {
                prefChunkNo = sampleInfo.getChunkNo();
                prefInd = sampleInfo.getSampleDescr();
            }
            if (prefChunkNo != sampleInfo.getChunkNo()) {
                uncompressed.add(new SampleToChunk(prefChunkNo, count, prefInd));
                count = 0;
                prefChunkNo = sampleInfo.getChunkNo();
            }
            count++;
        }
        uncompressed.add(new SampleToChunk(prefChunkNo, count, prefInd));

        return (SampleToChunk[]) uncompressed.toArray(new SampleToChunk[]{});
    }

    private static SampleSizes assembleSampleSizes(Collection samples) {
        boolean fixedSize = true;
        long prevSampleSize = -1;
        Iterator it1 = samples.iterator();
        while (it1.hasNext()) {
            SampleInfo sampleInfo = (SampleInfo) it1.next();
            if (prevSampleSize == -1) {
                prevSampleSize = sampleInfo.getSize();
            }
            if (sampleInfo.getDuration() != prevSampleSize) {
                fixedSize = false;
                break;
            }
        }
        SampleSizes ss;
        if (fixedSize) {
            ss = new SampleSizes(prevSampleSize, null);
        } else {
            long[] sizes = new long[samples.size()];
            int i = 0;
            Iterator it2 = samples.iterator();
            while (it2.hasNext()) {
                SampleInfo sampleInfo = (SampleInfo) it2.next();
                sizes[i] = sampleInfo.getSize();
                i++;
            }
            ss = new SampleSizes(0, sizes);
        }
        return ss;
    }

    private static long[] assembleSyncSamples(Collection samples) {
        ArrayList syncSamples = new ArrayList();
        Iterator it = samples.iterator();
        while (it.hasNext()) {
            SampleInfo sampleInfo = (SampleInfo) it.next();
            if (sampleInfo.isSyncSample()) {
                syncSamples.add(new Long(sampleInfo.getNo() + 1));
            }
        }
        long[] result = new long[syncSamples.size()];
        Iterator iterator = syncSamples.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            result[i] = ((Long) iterator.next()).longValue();
            i++;
        }

        return result;
    }
}