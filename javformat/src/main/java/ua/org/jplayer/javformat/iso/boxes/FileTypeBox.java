package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * File type box
 *
 * @author Stanislav Vitvitskiy
 */
public class FileTypeBox extends Box {
    private long majorBrand;
    private long minorBrand;
    private Collection compBrands = new ArrayList();

    public FileTypeBox() {
        super(new Atom(AtomType.FILE_Atom_TYPE));
    }

    @Override
    public void parse(InputStream is) throws IOException {
        this.majorBrand = Parser.readInt32(is);
        this.minorBrand = Parser.readInt32(is);
        long compBrand;
        do {
            compBrand = Parser.readInt32(is);
            compBrands.add(new Long(compBrand));
        } while (compBrand != -1);
    }

    @Override
    public void serialize(OutputStream os) throws IOException {
        atom.setBodySize(8 + compBrands.size() * 4);
        atom.serialize(os);

        Serializer.writeInt32(os, majorBrand);
        Serializer.writeInt32(os, minorBrand);
        Iterator it = compBrands.iterator();
        while (it.hasNext()) {
            Long compBrand = (Long) it.next();
            Serializer.writeInt32(os, compBrand.longValue());
        }
    }
}