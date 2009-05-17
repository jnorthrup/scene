package ua.org.jplayer.javformat.iso;

import org.apache.commons.io.IOUtils;
import ua.org.jplayer.javformat.iso.model.Sample;
import ua.org.jplayer.javformat.iso.model.SampleInfo;
import ua.org.jplayer.javformat.iso.model.Track;
import ua.org.jplayer.javformat.iso.model.TrackInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * An MP4 file player
 * <p/>
 * Reads MP4 file and returns payload samples for specified streams.
 * <p/>
 * Allows for random seek inside of the stream.
 *
 * @author Stanislav Vitvitskiy
 */
public class ISOPlayer {
    private final RandomAccessFile file;
    private Map head;
    private Collection tracks;

    public ISOPlayer(RandomAccessFile file) {
        this.file = file;
    }

    private static class RAFInputStream extends InputStream {
        private final RandomAccessFile raf;

        public RAFInputStream(RandomAccessFile raf) {
            this.raf = raf;
        }

        public int read() throws IOException {
            return raf.read();
        }
    }

    public void start() throws IOException {
        InputStream is = null;
        try {
            file.seek(0L);
            is = new BufferedInputStream(new RAFInputStream(file));
            ISODemux demux = new ISODemux(is);
            demux.parse();
            tracks = demux.getMovie().getTracks();
            head = new HashMap();
            Iterator it = tracks.iterator();
            while (it.hasNext()) {
                TrackInfo t = (TrackInfo) it.next();
                head.put(new Integer(t.getNo()), t.getSampleInfos().iterator());
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public void seek(int millisecond) {
        double masterSecond = -1;
        head = new HashMap();
        Iterator it1 = tracks.iterator();
        while (it1.hasNext()) {

            TrackInfo t = (TrackInfo) it1.next();
            double denom = 1.0 / t.getMediaHeader().getTimescale();
            Iterator it = t.getSampleInfos().iterator();
            while (it.hasNext()) {
                SampleInfo si = (SampleInfo) it.next();
                if (denom * si.getTimestamp() * 1000 > millisecond
                        && si.isSyncSample()) {
                    masterSecond = denom * si.getTimestamp() * 1000;
                    break;
                }
                if (masterSecond != -1
                        && denom * si.getTimestamp() * 1000 > masterSecond) {
                    break;
                }
            }
            head.put(new Integer(t.getNo()), it);
        }
    }

    public Sample getNextSample(int streamId) throws IOException {
        Iterator it = (Iterator) head.get(new Integer(streamId));
        SampleInfo si = (SampleInfo) it.next();
        file.seek(si.getOffset());

        byte[] buf = new byte[(int) si.getSize()];
        file.read(buf);
        return new Sample(streamId, si.getDuration(), si.isSyncSample(),
                si.getTimestamp(), buf);
    }

    public Collection getTraks() {
        ArrayList result = new ArrayList();
        Iterator it = tracks.iterator();
        while (it.hasNext()) {
            TrackInfo track = (TrackInfo) it.next();
            result.add(new Track(track.getNo(),
                    track.getMediaHeader().getTimescale()));
        }
        return result;
    }
}