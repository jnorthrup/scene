package ua.org.jplayer.javformat.iso;

import ua.org.jplayer.javformat.iso.boxes.FileTypeBox;
import ua.org.jplayer.javformat.iso.model.Movie;
import ua.org.jplayer.javformat.iso.model.Sample;
import ua.org.jplayer.javformat.iso.model.SampleInfo;
import ua.org.jplayer.javformat.iso.model.TrackInfo;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.domain.MediaHeader;
import ua.org.jplayer.javformat.iso.model.domain.MovieHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Encodes individual samples into MP4 container
 * <p/>
 * The header (moov) box is written in the end of the file after the data (mdat)
 * box
 * <p/>
 * Requires random access file to correctly write the lenth of mdat box
 *
 * @author Stanislav Vitvitskiy
 */
public class ISOEncoder {
    private final RandomAccessFile file;
    private final RAFOutputStream os;
    public static final int CHUNK_SIZE = 14;
    private Map<Integer, TrackHead> tracks;
    private long mdatPos;

    private class TrackHead {
        int curChunk;
        int posInChunk;
        int sampleNo;
        ByteArrayOutputStream chunkBuffer = new ByteArrayOutputStream();
        Collection<SampleInfo> samples = new ArrayList<SampleInfo>();
    }

    public ISOEncoder(RandomAccessFile file) {
        this.file = file;
        this.os = new RAFOutputStream(file);
    }

    private static class RAFOutputStream extends OutputStream {
        private RandomAccessFile raf;
        private long curOffset;

        public RAFOutputStream(RandomAccessFile raf) {
            this.raf = raf;
        }

        public void write(int b) throws IOException {
            raf.write(b);
            curOffset++;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            raf.write(b, off, len);
            curOffset += len - off;
        }

        public void write(byte[] b) throws IOException {
            raf.write(b);
            curOffset += b.length;
        }

        public long getCurOffset() {
            return curOffset;
        }
    }

    public void start() throws IOException {
        tracks = new HashMap<Integer, TrackHead>();
        file.seek(0);
        FileTypeBox fileTypeBox = new FileTypeBox();
        fileTypeBox.serialize(os);
        this.mdatPos = os.getCurOffset();
        Atom atom = new Atom(AtomType.MDAT, 0);
        atom.serialize(os);
    }

    public void writeSample(Sample s) throws IOException {

        TrackHead trackHead = tracks.get(s.getTrack());
        if (trackHead == null) {
            trackHead = new TrackHead();
            tracks.put(s.getTrack(), trackHead);
        }
        int size = s.getPayload().length;
        SampleInfo sampleInfo = new SampleInfo(trackHead.sampleNo,
                os.getCurOffset(), size);
        sampleInfo.setChunkNo(trackHead.curChunk);
        sampleInfo.setDuration(s.getDuration());
        sampleInfo.setSyncSample(s.isSyncSample());
        sampleInfo.setTimestamp(s.getTimestamp());
        trackHead.samples.add(sampleInfo);

        trackHead.posInChunk++;
        if (trackHead.posInChunk >= CHUNK_SIZE) {
            trackHead.posInChunk = 0;
            trackHead.curChunk++;
            os.write(trackHead.chunkBuffer.toByteArray());
            trackHead.chunkBuffer.reset();
        }
        trackHead.sampleNo++;
        trackHead.chunkBuffer.write(s.getPayload());
    }

    public void stop() throws IOException {

        Collection<TrackInfo> trackInfos = new ArrayList<TrackInfo>();
        Iterator<TrackHead> it = tracks.values().iterator();
        int i = 0;
        while (it.hasNext()) {
            TrackHead track = it.next();
            long duration = 0;
            long width = 0;
            long height = 0;
            TrackInfo trackInfo = new TrackInfo(i + 1, duration, width, height);
            trackInfo.setMediaHeader(new MediaHeader(0, 0));
            trackInfo.setSampleInfos(track.samples);
            trackInfos.add(trackInfo);
            i++;
        }
        MovieHeader movieHeader = new MovieHeader(0, 0, 0);
        ISOMux.writeIndex(os, new Movie(movieHeader, trackInfos));
        file.seek(this.mdatPos + 4);
        Serializer.writeInt32(os, os.getCurOffset() - this.mdatPos - 8);
    }
}
