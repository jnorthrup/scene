package ua.org.jplayer.javformat.flv;

import ua.org.jplayer.javformat.flv.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * A utility to generate metadata for a clip.
 * <p/>
 * Usage:
 * <p/>
 * <pre>
 * MetadataGen mdg = new MetadataGen(&quot;Your name&quot;, &quot;Your company&quot;, 320, 240);
 * for (Tag t : tags) {
 *     mdg.tag(t);
 * }
 * ScriptTag st = mdg.generateMetaData();
 * </pre>
 *
 * @author Stanislav Vitvitskiy
 */
public class MetadataGen {

    private final String creator;
    private final String metadataCreator;
    private final int width;
    private final int height;
    private boolean hasVideo;
    private boolean hasAudio;
    private long duration;
    private long videoSize;
    private long audioSize;
    private long videoCodecId;
    private long audioCodecId;
    private long lastkeyframetimestamp;
    private long lastkeyframelocation;
    private long lasttimestamp = -1;
    private Collection times = new ArrayList();
    private Collection positions = new ArrayList();
    private long curPos;
    private long nFrames;
    private long sampleRate;
    private long sampleSize;
    private boolean stereo;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private Double videoDataRate;
    private Double audioDataRate;

    public MetadataGen(String creator, String metadataCreator, int width,
                       int height) {
        this.creator = creator;
        this.metadataCreator = metadataCreator;
        this.width = width;
        this.height = height;
    }

    public MetadataGen(ScriptTag metadata) {
        Map event = (Map) metadata.getEvents().get("onMetaData");

        this.creator = (String) event.get("creator");
        this.metadataCreator = (String) event.get("metadatacreator");
        this.width = ((Double) event.get("width")).intValue();
        this.height = ((Double) event.get("height")).intValue();
        this.videoDataRate = (Double) event.get("videodatarate");
        this.audioDataRate = (Double) event.get("audiodatarate");
    }

    public void tag(Tag tag) {
        if (lasttimestamp == -1) {
            lasttimestamp = tag.getTimestamp();
        }
        if (tag.getType() == TagType.VIDEO) {
            VideoTag vTag = (VideoTag) tag;
            hasVideo = true;
            if (vTag.getFrameType() == FrameType.KEYFRAME) {
                times.add(new Double(vTag.getTimestamp() / 1000));
                positions.add(new Double(curPos));
                lastkeyframetimestamp = tag.getTimestamp();
                lastkeyframelocation = curPos;
            }
            duration += tag.getTimestamp() - lasttimestamp;
            if (tag.getTimestamp() > lasttimestamp) {
                lasttimestamp = tag.getTimestamp();
            }

            videoCodecId = vTag.getCodec().getValue();
            nFrames++;

        } else if (tag.getType() == TagType.AUDIO) {
            AudioTag aTag = (AudioTag) tag;
            hasAudio = true;
            audioCodecId = aTag.getAudioCodec().getValue();
            stereo = aTag.isStereo();
            sampleRate = aTag.getSampleRate();
            sampleSize = aTag.getSampleSize();
        }

        baos.reset();

        try {
            new FLVMux(baos).writeTag(tag);
        } catch (IOException e) {
        }

        curPos += baos.size();

        if (tag.getType() == TagType.VIDEO) {
            videoSize += baos.size() - 15;
        } else if (tag.getType() == TagType.AUDIO) {
            audioSize += baos.size() - 15;
        }
    }

    public ScriptTag generateMetadata() {

        double durationSec = (double) duration / 1000;

        Map events = new HashMap();
        Map properties = new HashMap();
        events.put("onMetaData", properties);
        properties.put("creator", creator);
        properties.put("metadatacreator", metadataCreator);
        properties.put("hasKeyframes", new Boolean(true));
        properties.put("hasVideo", new Boolean(hasVideo));
        properties.put("hasAudio", new Boolean(hasAudio));
        properties.put("hasMetadata", new Boolean(true));
        properties.put("canSeekToEnd", new Boolean(false));
        properties.put("duration", new Double(durationSec));
        properties.put("datasize", new Double(curPos));
        properties.put("filesize", new Double(0));
        properties.put("videosize", new Double(videoSize));
        properties.put("audiosize", new Double(audioSize));
        properties.put("videocodecid", new Double(videoCodecId));
        properties.put("width", new Double(width));
        properties.put("height", new Double(height));
        properties.put("framerate", new Double(nFrames / durationSec));
        properties.put("audiocodecid", new Double(audioCodecId));

        if (videoDataRate != null) {
            properties.put("videodatarate", videoDataRate);
        }
        if (audioDataRate != null) {
            properties.put("audiodatarate", audioDataRate);
        }

        properties.put("audiosamplerate", new Double(sampleRate));
        properties.put("audiosamplesize", new Double(sampleSize));
        properties.put("stereo", new Boolean(stereo));
        properties.put("lasttimestamp", new Double(lasttimestamp / 1000));
        properties.put("lastkeyframetimestamp", new Double(
                lastkeyframetimestamp / 1000));
        properties.put("lastkeyframelocation", new Double(lastkeyframelocation));

        HashMap keyframes = new HashMap();
        keyframes.put("times", times);
        keyframes.put("filepositions", positions);
        MetaObject metaObject = new MetaObject(keyframes);
        properties.put("keyframes", metaObject);

        ScriptTag scriptTag = new ScriptTag(0, 0, events);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            new FLVMux(baos).writeTag(scriptTag);
        } catch (IOException e) {
        }
        int metaSize = baos.size();

        ArrayList npositions = new ArrayList();
        Iterator it = positions.iterator();
        while (it.hasNext()) {
            Double pos = (Double) it.next();
            npositions.add(new Double(pos.doubleValue() + metaSize + 13));
        }
        keyframes.put("filepositions", npositions);

        properties.put("filesize", new Double(curPos + metaSize + 13));

        return scriptTag;
    }
}
