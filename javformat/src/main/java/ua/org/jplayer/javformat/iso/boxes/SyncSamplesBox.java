package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A box storing a list of synch samples
 *
 * @author Stanislav Vitvitskiy
 */
public class SyncSamplesBox extends Box {
    private long[] syncSamples;

    public SyncSamplesBox() {
        super(new Atom(AtomType.SYNCH_SAMPLE));
    }

    public SyncSamplesBox(long[] synchSample) {
        this();
        this.syncSamples = synchSample;
    }

    public void parse(InputStream is) throws IOException {
        is.skip(4);
        int foo = (int) Parser.readInt32(is);
        syncSamples = new long[foo];
        for (int i = 0; i < foo; i++) {
            syncSamples[i] = Parser.readInt32(is);
        }
    }

    public void serialize(OutputStream dos) throws IOException {
        atom.setBodySize(8 + syncSamples.length * 4);
        atom.serialize(dos);

        Serializer.writeInt32(dos, 0);

        Serializer.writeInt32(dos, syncSamples.length);
        for (int i = 0; i < syncSamples.length; i++) {
            Serializer.writeInt32(dos, syncSamples[i]);
        }
    }

    public long[] getSyncSamples() {
        return syncSamples;
    }
}
