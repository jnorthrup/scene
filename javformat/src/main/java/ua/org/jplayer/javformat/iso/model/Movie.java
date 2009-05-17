package ua.org.jplayer.javformat.iso.model;

import ua.org.jplayer.javformat.iso.model.domain.MovieHeader;

import java.util.Collection;

public class Movie {
    private MovieHeader movieHeader;
    private Collection tracks;

    public Movie(MovieHeader movieHeader, Collection tracks) {
        this.movieHeader = movieHeader;
        this.tracks = tracks;
    }

    public MovieHeader getMovieHeader() {
        return movieHeader;
    }

    public Collection getTracks() {
        return tracks;
    }
}
