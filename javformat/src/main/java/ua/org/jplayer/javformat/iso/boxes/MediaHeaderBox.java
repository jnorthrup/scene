package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;
import ua.org.jplayer.javformat.iso.model.domain.MediaHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A media header atom
 *
 * @author Stanislav Vitvitskiy
 */
public class MediaHeaderBox extends Box {
    private MediaHeader mediaHeader;

    public MediaHeaderBox() {
        super(new Atom(AtomType.MEDIA_HEADER));
    }

    public MediaHeaderBox(MediaHeader mediaHeader) {
        this();
        this.mediaHeader = mediaHeader;
    }

    public void parse(InputStream is) throws IOException {
        int version = is.read();
        is.read();
        is.read();
        is.read();
        long timeScale;
        long duration;
        if (version == 0) {
            Parser.readInt32(is);
            Parser.readInt32(is);
            timeScale = Parser.readInt32(is);
            duration = Parser.readInt32(is);
        } else if (version == 1) {
            Parser.readInt64(is);
            Parser.readInt64(is);
            timeScale = Parser.readInt32(is);
            duration = Parser.readInt64(is);
        } else {
            throw new RuntimeException("Unsupported version");
        }
        mediaHeader = new MediaHeader(timeScale, duration);
    }

    public void serialize(OutputStream os) throws IOException {
        int version = mediaHeader.getDuration() > Integer.MAX_VALUE ? 1 : 0;

        atom.setBodySize(8 + (version == 0 ? 16 : 28));
        atom.serialize(os);

        os.write(version);
        os.write(0);
        os.write(0);
        os.write(0);

        if (version == 0) {
            Serializer.writeInt32(os, 0);
            Serializer.writeInt32(os, 0);
            Serializer.writeInt32(os, mediaHeader.getTimescale());
            Serializer.writeInt32(os, mediaHeader.getDuration());
        } else {
            Serializer.writeInt64(os, 0);
            Serializer.writeInt32(os, 0);
            Serializer.writeInt64(os, mediaHeader.getTimescale());
            Serializer.writeInt64(os, mediaHeader.getDuration());
        }
        Serializer.writeInt16(os, 0); /* language */
        Serializer.writeInt16(os, 0); /* reserved (quality) */
    }

    public MediaHeader getMediaHeader() {
        return mediaHeader;
    }
}