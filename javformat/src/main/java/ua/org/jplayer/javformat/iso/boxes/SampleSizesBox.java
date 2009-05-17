package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;
import ua.org.jplayer.javformat.iso.model.domain.SampleSizes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SampleSizesBox extends Box {
    private SampleSizes sampleSizes;

    public SampleSizesBox() {
        super(new Atom(AtomType.SAMPLE_SIZE));
    }

    public SampleSizesBox(SampleSizes sampleSizes) {
        this();
        this.sampleSizes = sampleSizes;
    }

    public void parse(InputStream is) throws IOException {
        is.skip(4);
        long fixedSampleSize = Parser.readInt32(is);

        long[] sizes = null;
        if (fixedSampleSize == 0) {
            int nSampleSizes = (int) Parser.readInt32(is);
            sizes = new long[nSampleSizes];
            for (int i = 0; i < nSampleSizes; i++) {
                sizes[i] = Parser.readInt32(is);
            }
        }

        this.sampleSizes = new SampleSizes(fixedSampleSize, sizes);
    }

    public void serialize(OutputStream os) throws IOException {
        atom.setBodySize(4 + (sampleSizes.getDefaultSize() != 0 ? 4
                : 8 + sampleSizes.getSizes().length * 4));
        atom.serialize(os);

        Serializer.writeInt32(os, 0);

        if (sampleSizes.getDefaultSize() != 0) {
            Serializer.writeInt32(os, sampleSizes.getDefaultSize());
        } else {
            Serializer.writeInt32(os, 0);
            Serializer.writeInt32(os, sampleSizes.getSizes().length);

            long[] sizes = sampleSizes.getSizes();
            for (int i = 0; i < sizes.length; i++) {
                Serializer.writeInt32(os, sizes[i]);
            }
        }
    }

    public SampleSizes getSampleSizes() {
        return sampleSizes;
    }

    public void setSampleSizes(SampleSizes sampleSizes) {
        this.sampleSizes = sampleSizes;
    }
}