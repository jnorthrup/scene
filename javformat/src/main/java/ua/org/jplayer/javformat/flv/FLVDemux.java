package ua.org.jplayer.javformat.flv;

import ua.org.jplayer.javformat.flv.model.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * FLV parser ( demuxer ).
 * <p/>
 * Extracts and parses FLV tags from FLV file
 *
 * @author Stanislav Vitvitskiy
 */
public class FLVDemux {

    public static FLVHeader readHeader(InputStream is) throws IOException {
        is.skip(3);
        int version = is.read();
        int flags = is.read();
        boolean hasVideo = (flags & 0x1) != 0;
        boolean hasAudio = (flags >> 2 & 0x1) != 0;
        int dataOffset = readUI32(is);
        readUI32(is);

        return new FLVHeader(version, hasVideo, hasAudio, dataOffset);
    }

    public static Tag readTag(InputStream is) throws IOException {
        //int cool = is.read();
        DataInputStream dis = new DataInputStream(is);
        int tagType = dis.readByte();
        if (tagType == -1) {
            return null;
        }

        int dataLen = readUI24(dis);
        int ts = readUI24(dis);
        dis.readByte();
        int streamId = readUI24(dis);

        Tag result;
        if (tagType == TagType.SCRIPT.getValue()) {
            Map events = parseMeta(dis);
            result = new ScriptTag(streamId, ts, events);
        } else if (tagType == TagType.VIDEO.getValue()) {
            result = new VideoTag(streamId, ts);
            parseVideo(dis, (VideoTag) result, dataLen);
        } else if (tagType == TagType.AUDIO.getValue()) {
            result = new AudioTag(streamId, ts);
            parseAudio(dis, (AudioTag) result, dataLen);
        } else {
            result = null;
        }
        int prevTagSize = readUI32(dis);

        return result;
    }

    private static void parseVideo(DataInputStream dis, VideoTag tag,
                                   long dataLen) throws IOException {
        byte descriptor = dis.readByte();

        int frameType = descriptor >> 4 & 0xf;
        int codecId = descriptor & 0xf;

        tag.setFrameType(FrameType.fromValue(frameType));
        tag.setCodec(VideoCodec.fromValue(codecId));

        byte[] b = new byte[(int) dataLen - 1];
        dis.readFully(b);
        tag.setData(b);
    }

    private static void parseAudio(DataInputStream dis, AudioTag tag,
                                   long dataLen) throws IOException {
        byte audioDescriptor = dis.readByte();
        int audioCodec = audioDescriptor >> 4 & 0xf;
        int soundRate = audioDescriptor >> 2 & 0x3;
        int soundSize = audioDescriptor >> 1 & 0x1;
        int soundType = audioDescriptor & 0x1;

        tag.setAudioCodec(AudioCodec.fromValue(audioCodec));
        int sampleRate = 0;
        switch (soundRate) {
            case 0:
                sampleRate = 5500;
                break;
            case 1:
                sampleRate = 11000;
                break;
            case 2:
                sampleRate = 22000;
                break;
            case 3:
                sampleRate = 44000;
                break;
        }
        tag.setSampleRate(sampleRate);
        tag.setSampleSize(soundSize == 0 ? 8 : 16);
        tag.setStereo(soundType != 0);

        byte[] b = new byte[(int) dataLen - 1];
        dis.readFully(b);
        tag.setData(b);
    }

    private static Map parseMeta(InputStream is)
            throws IOException {
        DataInputStream dis = new DataInputStream(is);
        String name = (String) readMetaObject(dis);
        Object val = readMetaObject(dis);
        HashMap result = new HashMap();
        result.put(name, val);
        return result;
    }

    private static Object readMetaObject(DataInputStream dis)
            throws IOException {
        int type = dis.read();
        switch (type) {
            case 0:
                return new Double(readDBL64(dis));
            case 1:
                return new Boolean(dis.read() != 0);
            case 2:
                return readMetaString(dis);
            case 3:
                return new MetaObject(readMixedArray(dis));
            case 4:
                return null;
            case 5:
                return null;
            case 6:
                return null;
            case 7:
                return null;
            case 8:
                int len1 = readUI32(dis);
                return readMixedArray(dis);
            case 0xA:
                ArrayList arrayList = new ArrayList();
                int len2 = readUI32(dis);
                for (int i = 0; i < len2; i++) {
                    arrayList.add(readMetaObject(dis));
                }
                return arrayList;
            default:
                return null;
        }
    }

    private static String readMetaString(DataInputStream dis)
            throws IOException {
        int len = readUI16(dis);
        byte[] b = new byte[len];
        dis.read(b);
        return new String(b, "utf8");
    }

    private static Map readMixedArray(DataInputStream dis)
            throws IOException {
        HashMap result = new HashMap();
        String name = null;
        while (true) {
            name = readMetaString(dis);
            if (name.length() == 0) {
                break;
            }
            Object object = readMetaObject(dis);
            result.put(name, object);
        }
        int nine = dis.read();
        return result;
    }

    private static int readUI16(DataInputStream dis) throws IOException {
        return (dis.read() << 8) + dis.read();
    }

    private static int readUI24(DataInputStream dis) throws IOException {
        return (dis.read() << 16) + (dis.read() << 8) + dis.read();
    }

    private static int readUI32(InputStream is) throws IOException {
        return (is.read() << 24) + (is.read() << 16) + (is.read() << 8)
                + is.read();
    }

    private static double readDBL64(DataInputStream dis) throws IOException {
        return Double.longBitsToDouble(((long) dis.read() << 56)
                + ((long) dis.read() << 48) + ((long) dis.read() << 40)
                + ((long) dis.read() << 32) + ((long) dis.read() << 24)
                + ((long) dis.read() << 16) + ((long) dis.read() << 8)
                + (long) dis.read());
    }
}