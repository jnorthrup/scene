package ua.org.jplayer.javformat.flv.model;

import java.util.Collection;

/**
 * FLV movie
 *
 * @author Stanislav Vitvitskiy
 */
public class Movie {
    private final FLVHeader header;
    private final Collection tags;

    public Movie(FLVHeader header, Collection tags) {
        this.header = header;
        this.tags = tags;
    }

    public FLVHeader getHeader() {
        return header;
    }

    public Collection getTags() {
        return tags;
    }
}
