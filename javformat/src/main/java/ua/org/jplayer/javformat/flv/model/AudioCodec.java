package ua.org.jplayer.javformat.flv.model;

/**
 * An enumeration of audio codecs available for FLV files
 *
 * @author Stanislav Vitvitskiy
 */
public class AudioCodec {
    public static final AudioCodec PCM = new AudioCodec(0);
    public static final AudioCodec ADPCM = new AudioCodec(1);
    public static final AudioCodec MP3 = new AudioCodec(2);
    public static final AudioCodec LPCM = new AudioCodec(3);
    public static final AudioCodec NELLYMOSER_16 = new AudioCodec(4);
    public static final AudioCodec NELLYMOZER_8 = new AudioCodec(5);
    public static final AudioCodec NELLYMOSER = new AudioCodec(6);
    public static final AudioCodec G711_A = new AudioCodec(7);
    public static final AudioCodec G711_M = new AudioCodec(8);
    public static final AudioCodec AAC = new AudioCodec(10);
    public static final AudioCodec SPEEX = new AudioCodec(11);
    public static final AudioCodec MP3_8 = new AudioCodec(14);
    public static final AudioCodec DEV_SPEC = new AudioCodec(15);

    private int value;

    private AudioCodec(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AudioCodec fromValue(int value) {
        switch (value) {
            case 0:
                return PCM;
            case 1:
                return ADPCM;
            case 2:
                return MP3;
            case 3:
                return LPCM;
            case 4:
                return NELLYMOSER_16;
            case 5:
                return NELLYMOZER_8;
            case 6:
                return NELLYMOSER;
            case 7:
                return G711_A;
            case 8:
                return G711_M;
            case 10:
                return AAC;
            case 11:
                return SPEEX;
            case 14:
                return MP3_8;
            case 15:
                return DEV_SPEC;
        }
        return null;
    }
}