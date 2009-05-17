package ua.org.jplayer.javformat.iso.boxes;

import ua.org.jplayer.javformat.iso.Parser;
import ua.org.jplayer.javformat.iso.Serializer;
import ua.org.jplayer.javformat.iso.model.box.Atom;
import ua.org.jplayer.javformat.iso.model.box.AtomType;
import ua.org.jplayer.javformat.iso.model.box.Box;
import ua.org.jplayer.javformat.iso.model.domain.MovieHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A movie header box
 *
 * @author Stanislav Vitvitskiy
 */
public class MovieHeaderBox extends Box {
    private MovieHeader movieHeader;

    public MovieHeaderBox() {
        super(new Atom(AtomType.MOVIE_HEADER));
    }

    public MovieHeaderBox(MovieHeader movieHeader) {
        this();
        this.movieHeader = movieHeader;
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
        movieHeader = new MovieHeader(timeScale, duration, 0);
    }

    public void serialize(OutputStream os) throws IOException {
        int version = movieHeader.getDuration() > Integer.MAX_VALUE ? 1 : 0;

        atom.setBodySize(84 + (version == 0 ? 16 : 28));
        atom.serialize(os);

        os.write(version);
        os.write(0);
        os.write(0);
        os.write(0);

        if (version == 0) {
            Serializer.writeInt32(os, 0);
            Serializer.writeInt32(os, 0);
            Serializer.writeInt32(os, movieHeader.getTimescale());
            Serializer.writeInt32(os, movieHeader.getDuration());
        } else {
            Serializer.writeInt64(os, 0);
            Serializer.writeInt32(os, 0);
            Serializer.writeInt64(os, movieHeader.getTimescale());
            Serializer.writeInt64(os, movieHeader.getDuration());
        }

        Serializer.writeInt32(os, 0x00010000); /*
                                                * reserved (preferred rate) 1.0
                                                * = normal
                                                */
        Serializer.writeInt16(os, 0x0100); /*
                                            * reserved (preferred volume) 1.0 =
                                            * normal
                                            */
        Serializer.writeInt16(os, 0); /* reserved */
        Serializer.writeInt32(os, 0); /* reserved */
        Serializer.writeInt32(os, 0); /* reserved */

        /* Matrix structure */
        Serializer.writeInt32(os, 0x00010000); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x00010000); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x0); /* reserved */
        Serializer.writeInt32(os, 0x40000000); /* reserved */

        Serializer.writeInt32(os, 0); /* reserved (preview time) */
        Serializer.writeInt32(os, 0); /* reserved (preview duration) */
        Serializer.writeInt32(os, 0); /* reserved (poster time) */
        Serializer.writeInt32(os, 0); /* reserved (selection time) */
        Serializer.writeInt32(os, 0); /* reserved (selection duration) */
        Serializer.writeInt32(os, 0); /* reserved (current time) */
        Serializer.writeInt32(os, movieHeader.getNextTrackId()); /* Next track id */
    }

    public MovieHeader getMovieHeader() {
        return movieHeader;
    }
}