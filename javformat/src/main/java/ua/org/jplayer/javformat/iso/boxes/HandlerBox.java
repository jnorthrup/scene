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
 * A handler description box
 *
 * @author Stanislav Vitvitskiy
 */
public class HandlerBox extends Box {
    private int trackType;

    public HandlerBox() {
        super(new Atom(AtomType.HANDLER));
    }

    public HandlerBox(int trackType) {
        this();
        this.trackType = trackType;
    }

    public void parse(InputStream is) throws IOException {
        int version = is.read();
        is.read(); // flags
        is.read();
        is.read();

        Parser.readInt32(is);
        byte[] type = new byte[4];
        is.read(type);
        Parser.readInt32(is);
        Parser.readInt32(is);
        Parser.readInt32(is);
        StringBuilder sb = new StringBuilder();

        int size = is.read();
        for (int i = 0; i < size; i++) {
            sb.append((char) is.read());
        }
        // int b = -1;
        // while ((b = is.read()) != 0)
        // sb.append((char) b);

        String tStr = new String(type);
        if ("soun".equals(tStr)) {
            trackType = 1;
        } else if ("vide".equals(tStr)) {
            trackType = 0;
        } else if ("hint".equals(tStr)) {
            trackType = 2;
        }
    }

    public void serialize(OutputStream os) throws IOException {
        atom.setBodySize(25);
        atom.serialize(os);

        Serializer.writeInt32(os, 0);

        Serializer.writeInt32(os, 0);
        String code;
        switch (trackType) {
            case 0:
                code = "vide";
                break;
            case 1:
                code = "soun";
                break;
            default:
                code = "hint";
                break;
        }
        os.write(code.getBytes());

        Serializer.writeInt32(os, 0);
        Serializer.writeInt32(os, 0);
        Serializer.writeInt32(os, 0);

        os.write(0);
    }

    public int getTrackType() {
        return trackType;
    }
}