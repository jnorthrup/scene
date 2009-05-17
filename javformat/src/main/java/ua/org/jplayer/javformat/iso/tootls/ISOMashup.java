package ua.org.jplayer.javformat.iso.tootls;

import ua.org.jplayer.javformat.iso.ISOEncoder;
import ua.org.jplayer.javformat.iso.ISOPlayer;
import ua.org.jplayer.javformat.iso.model.Sample;
import ua.org.jplayer.javformat.iso.model.Track;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;

/**
 * Mashes up fragments from different movies
 *
 * @author Stanislav Vitvitskiy
 */
public class ISOMashup {
    private final ISOEncoder encoder;

    public ISOMashup(RandomAccessFile sink) throws IOException {
        encoder = new ISOEncoder(sink);
        encoder.start();
    }

    public void addFragment(RandomAccessFile source, int startSec, int endSec)
            throws IOException {
        ISOPlayer player = new ISOPlayer(source);
        player.start();
        player.seek(startSec * 1000);

        Collection traks = player.getTraks();
        Iterator iterator = traks.iterator();
        while (iterator.hasNext()) {
            Track track = (Track) iterator.next();
            Sample sample = null;
            while (sample == null
                    || sample.getTimestamp() / track.getTimescale() < endSec) {
                sample = player.getNextSample(track.getNo());
                encoder.writeSample(sample);
            }
        }
    }

    public void writeHeader() throws IOException {
        encoder.stop();
    }
}
