package ua.org.jplayer.javformat.flv.model;

/**
 * An enumeration of video codecs available in FLV
 *
 * @author Stanislav Vitvitskiy
 */
public class VideoCodec {
    public static final VideoCodec JPEG = new VideoCodec(1);
    public static final VideoCodec H263 = new VideoCodec(2);
    public static final VideoCodec SCREEN = new VideoCodec(3);
    public static final VideoCodec VP6 = new VideoCodec(4);
    public static final VideoCodec VP6_ALPHA = new VideoCodec(5);
    public static final VideoCodec SCREEN_2 = new VideoCodec(6);
    public static final VideoCodec AVC = new VideoCodec(7);

    private int value;

    private VideoCodec(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static VideoCodec fromValue(int value) {
        switch (value) {
            case 1:
                return JPEG;
            case 2:
                return H263;
            case 3:
                return SCREEN;
            case 4:
                return VP6;
            case 5:
                return VP6_ALPHA;
            case 6:
                return SCREEN_2;
            case 7:
                return AVC;
        }
        return null;
    }

}
