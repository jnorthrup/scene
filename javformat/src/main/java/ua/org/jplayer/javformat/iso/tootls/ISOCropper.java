package ua.org.jplayer.javformat.iso.tootls;

import org.apache.commons.io.output.CountingOutputStream;
import ua.org.jplayer.javformat.iso.ISODemux;
import ua.org.jplayer.javformat.iso.ISOMux;
import ua.org.jplayer.javformat.iso.TrackInterleaver;
import ua.org.jplayer.javformat.iso.boxes.ChunkOffsetsBox;
import ua.org.jplayer.javformat.iso.boxes.MovieHeaderBox;
import ua.org.jplayer.javformat.iso.boxes.TrackHeaderBox;
import ua.org.jplayer.javformat.iso.model.Movie;
import ua.org.jplayer.javformat.iso.model.SampleInfo;
import ua.org.jplayer.javformat.iso.model.TrackInfo;
import ua.org.jplayer.javformat.iso.model.box.*;
import ua.org.jplayer.javformat.iso.model.domain.MediaHeader;
import ua.org.jplayer.javformat.iso.model.domain.MovieHeader;
import ua.org.jplayer.javformat.toolkit.CountingInputStream;

import java.io.*;
import java.util.*;

/**
 * Efficient cropper for MP4 file
 * <p/>
 * Uses low lever container hacks to crop the video without remuxing and much
 * memory consumption
 * <p/>
 * Works only when the header box (moov) comes before the data box (mdat)
 * <p/>
 * Doesn't work on fragmented movies
 *
 * @author Stanislav Vitvitskiy
 */
public class ISOCropper {

    private Movie movie;
    private NodeBox originalBox;
    private LeafBox ftypBox;
    private FileInputStream is;

    public interface Dumper {
        public void dump(OutputStream os, boolean throttle) throws IOException;

        public long getDataSize();
    }

    public ISOCropper(FileInputStream is) throws IOException {
        CountingInputStream cis = new CountingInputStream(
                new BufferedInputStream(is));
        ISODemux demux = new ISODemux(cis);
        demux.parse();
        this.originalBox = demux.getOriginalBox();
        this.movie = demux.getMovie();
        this.ftypBox = demux.getFtypBox();
        this.is = is;
    }

    public Dumper getDumper(int startSec, int endSec) throws IOException {
        if (endSec == 0) {
            MovieHeaderBox mvhd = (MovieHeaderBox) Box.navigate(originalBox,
                    new AtomType[]{AtomType.MOVIE_HEADER});
            endSec = (int) (mvhd.getMovieHeader().getDuration() / mvhd.getMovieHeader().getTimescale());
        }
        Movie croppedMovie = cropMeta(startSec, endSec, movie);
        final TrackInterleaver trackInterleaver = new TrackInterleaver(
                croppedMovie);
        final Movie interleavedMovie = trackInterleaver.getInerleavedMovie();

        NodeBox tmpBox = ISOMux.composeMovieBox(interleavedMovie);
        final NodeBox newBox = copyUnchangedBoxes(tmpBox, originalBox);
        final long headerSize = getHeaderSize(newBox)
                + ftypBox.getData().length + 8;

        fixOffsets(newBox, headerSize + 8);

        return new Dumper() {
            public void dump(OutputStream os, boolean throttle)
                    throws IOException {
                int dataSize = calcDataSize(interleavedMovie);
                ftypBox.serialize(os);
                newBox.serialize(os);
                Atom atom = new Atom(AtomType.MDAT, dataSize + 8);
                atom.serialize(os);
                trackInterleaver.writeSamples(is, os, throttle);
                os.flush();
            }

            public long getDataSize() {
                return calcDataSize(interleavedMovie) + 8 + headerSize;
            }
        };
    }

    private static int calcDataSize(Movie movie) {
        int dataSize = 0;
        Iterator it = movie.getTracks().iterator();
        while (it.hasNext()) {
            TrackInfo ti = (TrackInfo) it.next();
            Iterator it1 = ti.getSampleInfos().iterator();
            while (it1.hasNext()) {
                SampleInfo si = (SampleInfo) it1.next();
                dataSize += si.getSize();
            }
        }

        return dataSize;
    }

    private static void fixOffsets(NodeBox newBox, long headerSize) {
        final Collection<? extends Box>
                tracks = NodeBox.navigateAll(newBox,
                AtomType.TRACK);
        for (Box track : tracks) {
            Box box = (Box) track;
            NodeBox nb = (NodeBox) box;
            ChunkOffsetsBox co = (ChunkOffsetsBox) nb.navigate(nb, new AtomType[]{
                    AtomType.MEDIA, AtomType.MEDIA_INFO, AtomType.SAMPLE_TABLE,
                    AtomType.CHUNK_OFFSET});

            try {
                long[] offsets = co.getChunkOffsets();
                for (int i = 0; i < offsets.length; i++) {
                    offsets[i] += headerSize;
                }
            } catch (Exception e) {
            }
        }
    }

    private static NodeBox copyUnchangedBoxes(NodeBox newBox,
                                              NodeBox originalBox) {
        NodeBox result = new NodeBox(newBox.getAtom());
        result.addBox(Box.navigate(originalBox,
                new AtomType[]{AtomType.MOVIE_HEADER}));

        final Collection<? extends Box> newTracks = Box.navigateAll(newBox,
                AtomType.TRACK);
        final Collection<? extends Box> oldTracks = Box.navigateAll(originalBox,
                AtomType.TRACK);

        Iterator<? extends Box> it = newTracks.iterator();
        while (it.hasNext()) {
            final NodeBox newTrack = (NodeBox) it.next();
            TrackInfo trackInfo = parseTrackHeader(newTrack);
            Iterator<? extends Box> it1 = oldTracks.iterator();
            while (it1.hasNext()) {
                Box oldTrack = it1.next();
                TrackInfo trackInfo1 = parseTrackHeader(oldTrack);
                if (trackInfo1.getNo() == trackInfo.getNo()) {
                    result.addBox(copyUnchangedBoxes1(newTrack, oldTrack));
                }
            }
        }
        return result;
    }

    private static TrackInfo parseTrackHeader(Box track) {
        return ((TrackHeaderBox) Box.navigate(track,
                new AtomType[]{AtomType.TRACK_HEADER})).getTrackInfo();
    }

    private static NodeBox copyUnchangedBoxes1(NodeBox newBox, Box originalBox) {
        final NodeBox resultBox = new NodeBox(newBox.getAtom());
        final Collection boxes = new ArrayList();
        boxes.addAll(newBox.getBoxes());
        final Iterator it1 = ((NodeBox) originalBox).getBoxes().iterator();

        while (it1.hasNext()) {
            Box box = (Box) it1.next();
            Box found = null;
            Iterator it = boxes.iterator();
            while (it.hasNext()) {
                Box box2 = (Box) it.next();
                if (box2.getAtom().getType() == box.getAtom().getType()) {
                    found = box2;
                    it.remove();
                    break;
                }
            }
            if (found == null) {
                resultBox.addBox(box);
            } else {
                if (found instanceof NodeBox) {
                    resultBox.addBox(copyUnchangedBoxes1((NodeBox) found,
                            (NodeBox) box));
                } else {
                    resultBox.addBox(found);
                }
            }
        }
        return resultBox;
    }

    private static long getHeaderSize(NodeBox box) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CountingOutputStream cos = new CountingOutputStream(baos);
        box.serialize(cos);
        return cos.getCount();
    }

    private static Movie cropMeta(int startSec, int endSec, Movie movie) {

        ArrayList<? extends Object> sortedTracks = new ArrayList<Object>();
        sortedTracks.addAll(movie.getTracks());
        Collections.sort(sortedTracks, new Comparator() {
            public int compare(Object o1, Object o2) {
                TrackInfo ti1 = (TrackInfo) o1;
                TrackInfo ti2 = (TrackInfo) o2;
                return ti1.getType() > ti1.getType() ? 1
                        : ti1.getType() == ti1.getType() ? 0 : -1;
            }
        });

        double masterStart = -1;
        double masterEnd = -1;
        long maxDuration = 0;
        final Collection<TrackInfo> newTracks = new ArrayList<TrackInfo>();
        final Iterator<? extends TrackInfo> it1 = (Iterator<? extends TrackInfo>) sortedTracks.iterator();
        long maxTrackId = 0;
        while (it1.hasNext()) {
            TrackInfo trackInfo = it1.next();
            // Throw away hint tracks, we'll recreate them
            if (trackInfo.getType() == 2) {
                continue;
            }
            Collection<SampleInfo> newSampleInfos = new ArrayList<SampleInfo>();
            double denom = 1.0 / trackInfo.getMediaHeader().getTimescale();
            Collection sampleInfos = trackInfo.getSampleInfos();
            if (masterStart == -1 && masterEnd == -1) {
                Iterator it = sampleInfos.iterator();
                SampleInfo si = null;
                while (it.hasNext()) {
                    si = (SampleInfo) it.next();
                    if (denom * si.getTimestamp() >= startSec
                            && si.isSyncSample()) {
                        masterStart = denom * si.getTimestamp();
                        break;
                    }
                }
                int sampleNo = 0;
                if (si != null) {
                    si.setNo(sampleNo);
                    sampleNo++;
                    newSampleInfos.add(si);
                }
                long ts = 0;
                while (it.hasNext()) {
                    si = (SampleInfo) it.next();
                    ts = si.getTimestamp();
                    si.setNo(sampleNo);
                    sampleNo++;
                    newSampleInfos.add(si);
                    if (denom * ts >= endSec && si.isSyncSample()) {
                        masterEnd = denom * ts;
                        break;
                    }
                }
                if (masterEnd == -1) {
                    masterEnd = denom * ts;
                }
            } else {
                int sampleNo = 0;
                Iterator it = sampleInfos.iterator();
                SampleInfo si = null;
                while (it.hasNext()) {
                    si = (SampleInfo) it.next();
                    if (denom * si.getTimestamp() >= masterStart) {
                        break;
                    }
                }
                if (si != null) {
                    si.setNo(sampleNo);
                    sampleNo++;
                    newSampleInfos.add(si);
                }
                while (it.hasNext()) {
                    si = (SampleInfo) it.next();
                    long ts = si.getTimestamp();
                    si.setNo(sampleNo);
                    sampleNo++;
                    newSampleInfos.add(si);
                    if (denom * ts >= masterEnd) {
                        break;
                    }
                }
            }

            long trackDuration = 0;
            Iterator<SampleInfo> it = newSampleInfos.iterator();
            while (it.hasNext()) {
                SampleInfo si = it.next();
                trackDuration += si.getDuration();
            }

            long curDuration = (long) ((double) trackDuration / trackInfo.getMediaHeader().getTimescale() * movie.getMovieHeader().getTimescale());
            if (curDuration > maxDuration) {
                maxDuration = curDuration;
            }

            TrackInfo newTrack = new TrackInfo(trackInfo.getNo(), curDuration,
                    trackInfo.getWidth(), trackInfo.getHeight());
            MediaHeader newMediaHeader = new MediaHeader(
                    trackInfo.getMediaHeader().getTimescale(), trackDuration);

            newTrack.setMediaHeader(newMediaHeader);
            newTrack.setSampleInfos(newSampleInfos);
            newTracks.add(newTrack);
            if (trackInfo.getNo() > maxTrackId) {
                maxTrackId = trackInfo.getNo();
            }
        }

        MovieHeader newMovieHeader = new MovieHeader(
                movie.getMovieHeader().getTimescale(), maxDuration,
                maxTrackId + 1);

        return new Movie(newMovieHeader, newTracks);
    }
}