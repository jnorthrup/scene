package ua.org.jplayer.javformat.flv.model;

/**
 * An enumeration of frame types in FLV file
 *
 * @author Stanislav Vitvitskiy
 */
public class FrameType {
    public static final FrameType KEYFRAME = new FrameType(1);
    public static final FrameType INTERFRAME = new FrameType(2);
    public static final FrameType DISP_INTERFRAME = new FrameType(3);
    public static final FrameType GEN_KEYFRAME = new FrameType(4);
    public static final FrameType VIDEOINFO = new FrameType(5);

    private int value;

    private FrameType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FrameType fromValue(int frameType) {
        switch (frameType) {
            case 1:
                return KEYFRAME;
            case 2:
                return INTERFRAME;
            case 3:
                return DISP_INTERFRAME;
            case 4:
                return GEN_KEYFRAME;
            case 5:
                return VIDEOINFO;

        }
        return null;
    }
}
