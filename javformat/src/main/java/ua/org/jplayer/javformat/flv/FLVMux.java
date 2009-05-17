package ua.org.jplayer.javformat.flv;

import ua.org.jplayer.javformat.flv.model.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * FLV serializer ( mux )
 * <p/>
 * Serializes tags into FLV file
 *
 * @author Stanislav Vitvitskiy
 */
public class FLVMux {

    public static final int TAG_OWN_LENGTH = 15;
    private static final byte[] _flvheader = hexToByte("464C56"); // "FLV"

    private final OutputStream os;
    private boolean tagsStarted;

    public FLVMux(OutputStream os) {
        this.os = os;
    }

    public void writeHeader(FLVHeader flvHeader) throws IOException {
        os.write(_flvheader);
        int flags = (flvHeader.isHasVideo() ? 1 : 0)
                | (flvHeader.isHasAudio() ? 1 : 0) << 2;
        os.write(flvHeader.getVersion());
        os.write(flags);
        writeUI32(os, 9);
        writeUI32(os, 0);
    }

    public void writeTag(Tag tag) throws IOException {
        if (tag instanceof ScriptTag && tagsStarted) {
            throw new IOException("Metadata should be the first tag");
        }
        tagsStarted = true;

        os.write(tag.getType().getValue());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (tag instanceof ScriptTag) {
            writeMeta(baos, ((ScriptTag) tag).getEvents());
        } else {
            if (tag instanceof VideoTag) {
                VideoTag videoTag = (VideoTag) tag;
                baos.write(videoTag.getFrameType().getValue() << 4
                        | videoTag.getCodec().getValue());
                baos.write(videoTag.getData());
            } else {
                if (tag instanceof AudioTag) {
                    AudioTag audioTag = (AudioTag) tag;
                    int soundRate = 0;
                    switch (audioTag.getSampleRate()) {
                        case 5500:
                            soundRate = 0;
                            break;
                        case 11000:
                            soundRate = 1;
                            break;
                        case 22000:
                            soundRate = 2;
                            break;
                        case 44000:
                            soundRate = 3;
                            break;
                    }

                    final int b = prepare(audioTag, soundRate);
                    baos.write(b);
                    baos.write(audioTag.getData());
                }
            }
        }
        byte[] tagData = baos.toByteArray();

        writeUI24(os, tagData.length);
        writeUI24(os, tag.getTimestamp());
        os.write(0);
        writeUI24(os, tag.getStreamId());
        os.write(tagData);
        writeUI32(os, tagData.length + TAG_OWN_LENGTH);
    }

    private int prepare(AudioTag audioTag, int soundRate) {
        return audioTag.getAudioCodec().getValue() << 4
                | soundRate << 2
                | (audioTag.getSampleSize() == 16 ? 1 : 0) << 1
                | (audioTag.isStereo() ? 1 : 0);
    }

    private static void writeMeta(OutputStream os, Map data) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        for (Object o : data.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            writeMetaObject(dos, entry.getKey());
            writeMetaObject(dos, entry.getValue());
            // writeUI24(dos, 9);
        }
    }

    private static void writeMetaObject(DataOutputStream dos, Object obj)
            throws IOException {
        if (obj != null) {
            if (obj instanceof Double) {
                dos.write(0);
                FLVMux.writeDBL64(dos, (Double) obj);
            } else {
                if (obj instanceof Boolean) {
                    dos.write(1);
                    dos.write(((Boolean) obj).booleanValue() ? 1 : 0);
                } else {
                    if (obj instanceof String) {
                        dos.write(2);
                        writeMetaString(dos, obj);
                    } else {
                        if (obj instanceof Map) {
                            dos.write(8);
                            Map map = (Map) obj;
                            writeUI32(dos, map.entrySet().size());
                            writeMixed(dos, map);
                        } else {
                            if (obj instanceof Collection) {
                                dos.write(0xA);
                                Collection col = (Collection) obj;
                                writeUI32(dos, col.size());
                                for (Object aCol : col) {
                                    writeMetaObject(dos, aCol);
                                }
                            } else {
                                if (obj instanceof MetaObject) {
                                    dos.write(3);
                                    MetaObject mobj = (MetaObject) obj;
                                    writeMixed(dos, mobj.getContents());
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            dos.write(6);
        }
    }

    private static void writeMixed(DataOutputStream dos, Map map)
            throws IOException {

        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            writeMetaString(dos, entry.getKey());
            writeMetaObject(dos, entry.getValue());
        }
        writeUI24(dos, 9);
    }

    private static void writeMetaString(DataOutputStream dos, Object obj)
            throws IOException {
        String val = (String) obj;
        writeUI16(dos, val.length());
        dos.write(val.getBytes());
    }

    private static void writeUI16(OutputStream os, int n) throws IOException {
        os.write(n >> 8 & 0xff);
        os.write(n & 0xff);
    }

    private static void writeDBL64(OutputStream os, double dbl)
            throws IOException {
        final long n = Double.doubleToLongBits(dbl);
        os.write((int) (n >> 56 & 0xff));
        os.write((int) (n >> 48 & 0xff));
        os.write((int) (n >> 40 & 0xff));
        os.write((int) (n >> 32 & 0xff));
        os.write((int) (n >> 24 & 0xff));
        os.write((int) (n >> 16 & 0xff));
        os.write((int) (n >> 8 & 0xff));
        os.write((int) (n & 0xff));
    }

    private static void writeUI24(OutputStream dis, long n) throws IOException {
        dis.write((int) (n >> 16 & 0xff));
        dis.write((int) (n >> 8 & 0xff));
        dis.write((int) (n & 0xff));
    }

    private static void writeUI32(OutputStream dis, int n) throws IOException {
        dis.write(n >> 24 & 0xff);
        dis.write(n >> 16 & 0xff);
        dis.write(n >> 8 & 0xff);
        dis.write(n & 0xff);
    }

    private static byte[] hexToByte(String hexString) {
        byte[] returnBytes = new byte[hexString.length() / 2];
        for (int i = 0; i < returnBytes.length; i++) {
            returnBytes[i] = Byte.parseByte(hexString.substring(i * 2,
                    i * 2 + 2), 16);
        }
        return returnBytes;
    }
}
