package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.TrackInfo;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TrackHeaderBox extends Box {
    private TrackInfo trackInfo;

    public TrackHeaderBox() {
        super(new Atom(AtomType.TRACK_HEADER));
    }

    public TrackHeaderBox(TrackInfo trackInfo) {
        this();
        this.trackInfo = trackInfo;
    }

    public void parse(InputStream is) throws IOException {
        int version = is.read();
        is.read(); // flags
        is.read();
        is.read();

        if (version == 0) {
            Parser.readInt32(is); // Creation time
            Parser.readInt32(is); // Modification time
        } else {
            Parser.readInt64(is);
            Parser.readInt64(is);
        }
        long trackId = Parser.readInt32(is);
        Parser.readInt32(is);

        long duration;
        if (version == 0) {
            duration = Parser.readInt32(is);
        } else {
            duration = Parser.readInt64(is);
        }

        Parser.readInt32(is); // Reserved
        Parser.readInt32(is);
        Parser.readInt32(is);

        Parser.readInt32(is); // Volume

        // Matrix structure
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);

        long width = Parser.readInt32(is) >> 16;
        long height = Parser.readInt32(is) >> 16;

        trackInfo = new TrackInfo((int) trackId, duration, width, height);

    }

    public void serialize(OutputStream os) throws IOException {
        int version = trackInfo.getDuration() > Integer.MAX_VALUE ? 1 : 0;

        atom.setBodySize(18 * 4 + (version == 0 ? 12 : 24));
        atom.serialize(os);

        os.write(version);
        Serializer.writeInt24(os, 0xf); // Track enabled

        if (version == 0) {
            Serializer.writeInt32(os, 0); // Creation time
            Serializer.writeInt32(os, 0); // Modification time
        } else {
            Serializer.writeInt64(os, 0);
            Serializer.writeInt64(os, 0);
        }
        Serializer.writeInt32(os, trackInfo.getNo());
        Serializer.writeInt32(os, 0); // reserved

        if (version == 0) {
            Serializer.writeInt32(os, trackInfo.getDuration());
        } else {
            Serializer.writeInt64(os, trackInfo.getDuration());
        }

        Serializer.writeInt32(os, 0);
        Serializer.writeInt32(os, 0);
        Serializer.writeInt32(os, 0);

        // Volume, only for audio tracks
        if (trackInfo.getType() == 1) {
            Serializer.writeInt16(os, 0x0100);
        } else {
            Serializer.writeInt16(os, 0x0);
        }
        Serializer.writeInt16(os, 0);

        Serializer.writeInt32(os, 0x00010000); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x00010000); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x40000000); /* reserved */

        if (trackInfo.getType() == 0) {
            Serializer.writeInt32(os, trackInfo.getWidth() << 16);
            Serializer.writeInt32(os, trackInfo.getHeight() << 16);
        } else {
            Serializer.writeInt32(os, 0);
            Serializer.writeInt32(os, 0);
        }
    }

    public TrackInfo getTrackInfo() {
        return trackInfo;
    }
}