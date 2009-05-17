package ua.org.jplayer.javformat.iso.model.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A leaf box
 * <p/>
 * A box containing data, no children
 *
 * @author Stanislav Vitvitskiy
 */
public class LeafBox extends Box {
    private byte[] data;

    public LeafBox(Atom atom) {
        super(atom);
    }

    public void parse(InputStream di) throws IOException {
        data = new byte[(int) atom.getBodySize()];

        di.read(data);
    }

    public void serialize(OutputStream dos) throws IOException {
        atom.setBodySize(data.length);
        atom.serialize(dos);
        dos.write(data);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
