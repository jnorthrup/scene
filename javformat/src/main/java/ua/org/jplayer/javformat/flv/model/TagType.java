package ua.org.jplayer.javformat.flv.model;

/**
 * An enumeration of FLV tag types
 *
 * @author Stanislav Vitvitskiy
 */

public class TagType {
    public static final TagType VIDEO = new TagType(9);
    public static final TagType AUDIO = new TagType(8);
    public static final TagType SCRIPT = new TagType(18);

    private final int value;

    private TagType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
