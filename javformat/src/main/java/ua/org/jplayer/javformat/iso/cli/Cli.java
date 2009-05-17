package ua.org.jplayer.javformat.iso.cli;

import org.apache.commons.io.IOUtils;
import ua.org.jplayer.javformat.iso.tootls.ISOCropper;
import ua.org.jplayer.javformat.iso.tootls.ISOCropper.Dumper;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A primitive command line interface to access mp4 cropping functionality
 *
 * @author Stanislav Vitvitskiy
 */
public class Cli {
    public static void main(String[] args) throws FileNotFoundException {

        if (args.length < 3) {
            System.out.println("Syntax: <command> <in file> <out file> <command opts>");
            return;
        }

        String command = args[0];

        if (command.startsWith("crop")) {
            Pattern pattern = Pattern.compile("crop=([0-9]+),([0-9]+)");
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) {
                String ss = matcher.group(1);
                String es = matcher.group(2);

                FileInputStream is = null;
                OutputStream os = null;
                try {
                    is = new FileInputStream(args[1]);
                    os = new BufferedOutputStream(new FileOutputStream(args[2]));

                    ISOCropper cropper = new ISOCropper(is);
                    Dumper dumper = cropper.getDumper(Integer.parseInt(ss),
                            Integer.parseInt(es));
                    dumper.dump(os, false);
                } catch (IOException e) {
                    IOUtils.closeQuietly(is);
                    IOUtils.closeQuietly(os);
                }
            }
        } else {
            System.out.println("Unknown command");
        }
    }
}
