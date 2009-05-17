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
 * A box to hold chunk offsets
 *
 * @author Stanislav Vitvitskiy
 */

public class ChunkOffsetsBox extends Box {
    private long[] chunkOffsets;

    public ChunkOffsetsBox() {
        super(new Atom(AtomType.CHUNK_OFFSET));
    }

    public ChunkOffsetsBox(long[] chunkOffsets) {
        this();
        this.chunkOffsets = chunkOffsets;
    }

    public void parse(InputStream di) throws IOException {
        di.skip(4);
        int length = (int) Parser.readInt32(di);
        chunkOffsets = new long[length];
        for (int i = 0; i < length; i++) {
            chunkOffsets[i] = Parser.readInt32(di);
        }
    }

    public void serialize(OutputStream dos) throws IOException {
        atom.setBodySize(8 + chunkOffsets.length * 4);
        atom.serialize(dos);

        Serializer.writeInt32(dos, 0);
        Serializer.writeInt32(dos, chunkOffsets.length);
        for (int i = 0; i < chunkOffsets.length; i++) {
            Serializer.writeInt32(dos, chunkOffsets[i]);
        }
    }

    public long[] getChunkOffsets() {
        return chunkOffsets;
    }

    public void setChunkOffsets(long[] chunkOffsets) {
        this.chunkOffsets = chunkOffsets;
    }
}
