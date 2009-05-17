package ua.org.jplayer.javformat.flv.cli;

import org.apache.commons.io.IOUtils;
import ua.org.jplayer.javformat.flv.FLVHelper;
import ua.org.jplayer.javformat.flv.model.Movie;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A command line interface that allows to trim the FLV movie
 *
 * @author Stanislav Vitvitskiy
 */
public class Cli {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Syntax: <in file> <out file> <start sec> <start sec>");
            System.exit(0);
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(args[0]);
            os = new FileOutputStream(args[1]);

            Movie movie = FLVHelper.getCroppedMovie(is,
                    Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            FLVHelper.writeMovie(os, movie);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }
}