package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;
import ua.org.jplayer.javformat.iso.model.domain.TimeToSampleEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A box containing sample presentation time information
 *
 * @author Stanislav Vitvitskiy
 */
public class TimeToSampleBox extends Box {

    private TimeToSampleEntry[] timeToSamples;

    public TimeToSampleBox() {
        super(new Atom(AtomType.TIME_TO_SAMPLE));
    }

    public TimeToSampleBox(TimeToSampleEntry[] timeToSamples) {
        this();
        this.timeToSamples = timeToSamples;
    }

    public void serialize(OutputStream dos) throws IOException {
        atom.setBodySize(8 + timeToSamples.length * 8);
        atom.serialize(dos);
        Serializer.writeInt32(dos, 0);
        Serializer.writeInt32(dos, timeToSamples.length);
        for (int i = 0; i < timeToSamples.length; i++) {
            TimeToSampleEntry stss = timeToSamples[i];
            Serializer.writeInt32(dos, stss.getSampleCount());
            Serializer.writeInt32(dos, stss.getSampleDuration());
        }
    }

    public void parse(InputStream di) throws IOException {
        di.skip(4);
        int foo = (int) Parser.readInt32(di);
        timeToSamples = new TimeToSampleEntry[foo];
        for (int i = 0; i < foo; i++) {
            long sampleCount = Parser.readInt32(di);
            long sampleDuration = Parser.readInt32(di);
            timeToSamples[i] = new TimeToSampleEntry(sampleCount,
                    sampleDuration);
        }
    }

    public TimeToSampleEntry[] getTimeToSamples() {
        return timeToSamples;
    }

    public void setTimeToSamples(TimeToSampleEntry[] timeToSamples) {
        this.timeToSamples = timeToSamples;
    }
}