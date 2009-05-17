package ua.org.jplayer.javformat.flv;

import org.apache.commons.io.input.CountingInputStream;
import ua.org.jplayer.javformat.flv.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * A bunch of methods to perform typical operations on FLV file
 *
 * @author Stanislav Vitvitskiy
 */
public class FLVHelper {

    public static Movie getCroppedMovie(InputStream is, int startSec, int endSec)
            throws IOException {
        CountingInputStream cis = new CountingInputStream(is);
        FLVHeader header = FLVDemux.readHeader(cis);
        ScriptTag metadata = findScriptTag(cis);

        double startOffset = getKeyframeOffset(metadata, startSec);
        double endOffset = getKeyframeOffset(metadata, endSec);

        cis.skip((long) startOffset - cis.getCount());

        MetadataGen metadataGen = new MetadataGen(metadata);
        long firstTs = -1;
        LinkedList buffer = new LinkedList();
        while (cis.getCount() < endOffset) {
            Tag tag = FLVDemux.readTag(cis);
            if (firstTs == -1) {
                firstTs = tag.getTimestamp();
            }
            tag.setTimeStamp(tag.getTimestamp() - firstTs);
            buffer.add(tag);
            metadataGen.tag(tag);
        }
        ScriptTag newMetadata = metadataGen.generateMetadata();
        buffer.addFirst(newMetadata);

        return new Movie(header, buffer);
    }

    public static void writeMovie(OutputStream os, Movie movie)
            throws IOException {
        FLVMux mux = new FLVMux(os);
        mux.writeHeader(movie.getHeader());
        Iterator it = movie.getTags().iterator();
        while (it.hasNext()) {
            Tag tag = (Tag) it.next();
            mux.writeTag(tag);
        }
    }

    public static boolean seekToSecond(InputStream is, ScriptTag scriptTag,
                                       long second) throws IOException {
        double offset = getKeyframeOffset(scriptTag, second);

        if (offset != -1) {
            is.skip((long) offset);
            return true;
        }
        return false;
    }

    public static double getKeyframeOffset(ScriptTag scriptTag, long second)
            throws IOException {
        Map event = getMetadata(scriptTag);
        if (event == null) {
            return -1;
        }

        MetaObject mobj = (MetaObject) event.get("keyframes");
        if (mobj == null) {
            return -1;
        }

        Collection times = (Collection) mobj.getContents().get("times");
        Collection filepositions = (Collection) mobj.getContents().get(
                "filepositions");

        if (times == null || filepositions == null) {
            return -1;
        }

        double filePosition = -1;
        Iterator it = filepositions.iterator();
        Iterator tit = times.iterator();
        while (tit.hasNext()) {
            Double time = (Double) tit.next();
            if (time.longValue() > second) {
                break;
            }
            filePosition = ((Double) it.next()).doubleValue();
        }

        return filePosition;
    }

    private static Map getMetadata(ScriptTag scriptTag) {
        return (Map) scriptTag.getEvents().get("onMetaData");
    }

    public static ScriptTag findScriptTag(InputStream is) throws IOException {
        Tag readTag;
        do {
            readTag = FLVDemux.readTag(is);
            if (readTag.getType() == TagType.SCRIPT) {
                return (ScriptTag) readTag;
            }
        } while (readTag != null);

        return null;
    }

}
