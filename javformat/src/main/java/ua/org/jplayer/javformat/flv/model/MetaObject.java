package ua.org.jplayer.javformat.flv.model;

import java.util.Map;

/**
 * A holder for action script object
 *
 * @author Stanislav Vitvitskiy
 */
public class MetaObject {
    private final Map contents;

    public MetaObject(Map contents) {
        this.contents = contents;
    }

    public Map getContents() {
        return contents;
    }
}
