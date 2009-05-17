package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;
import ua.org.jplayer.javformat.iso.model.domain.SampleToChunk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Sample to chunk mapping box
 *
 * @author Stanislav Vitvitskiy
 */
public class SampleToChunkBox extends Box {
    SampleToChunk[] sampleToChunk;

    public SampleToChunkBox() {
        super(new Atom(AtomType.SAMPLE_TO_CHUNK));
    }

    public SampleToChunkBox(SampleToChunk[] sampleToChunk) {
        this();
        this.sampleToChunk = sampleToChunk;
    }

    public void parse(InputStream di) throws IOException {
        // di.skip(4);
        // int size = (int) Parser.readInt32(di);
        // int[] ids = new int[size];
        // long[] counts = new long[size];
        // int[] dInd = new int[size];
        //
        // for (int i = 0; i < size; i++) {
        // ids[i] = ;
        // counts[i] = ;
        // dInd[i] = ;
        // }
        //
        // int nChunks = ids[size - 1];
        //
        // sampleToChunk = new SampleToChunk[nChunks];
        // int j = nChunks - 1;
        // for (int i = size - 1; i >= 0; i--) {
        // for (; j >= ids[i] - 1; j--) {
        // sampleToChunk[j] = new SampleToChunk(j, counts[i], dInd[i]);
        // }
        // }

        di.skip(4);
        int size = (int) Parser.readInt32(di);

        sampleToChunk = new SampleToChunk[size];
        for (int i = 0; i < size; i++) {
            sampleToChunk[i] = new SampleToChunk((int) Parser.readInt32(di),
                    Parser.readInt32(di), (int) Parser.readInt32(di));
        }
    }

    public void serialize(OutputStream dos) throws IOException {
        Collection compressed = compress();

        atom.setBodySize(8 + compressed.size() * 12);
        atom.serialize(dos);

        Serializer.writeInt32(dos, 0);
        Serializer.writeInt32(dos, compressed.size());
        Iterator it = compressed.iterator();
        while (it.hasNext()) {
            SampleToChunk s2c = (SampleToChunk) it.next();
            Serializer.writeInt32(dos, s2c.getChunkNo() + 1);
            Serializer.writeInt32(dos, s2c.getCount());
            Serializer.writeInt32(dos, s2c.getDInd());
        }
    }

    private Collection compress() {
        ArrayList compressed = new ArrayList();
        long prevCount = -1;
        int prevInd = -1;
        int prevChunk = -1;
        int i = 0;
        for (; i < sampleToChunk.length; i++) {
            SampleToChunk s2c = sampleToChunk[i];
            if (prevCount == -1 || prevCount != s2c.getCount()) {
                prevCount = s2c.getCount();
                prevChunk = i;
                prevInd = s2c.getDInd();
                compressed.add(new SampleToChunk(prevChunk, prevCount, prevInd));
            }
        }
        if (i - 1 != prevChunk) {
            compressed.add(new SampleToChunk(i - 1, prevCount, prevInd));
        }

        return compressed;
    }

    public SampleToChunk[] getSampleToChunk() {
        return sampleToChunk;
    }
}