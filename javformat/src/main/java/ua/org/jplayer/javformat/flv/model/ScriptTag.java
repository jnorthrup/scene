package ua.org.jplayer.javformat.flv.model;

import java.util.Map;

/**
 * A model object for holding FLV script tag
 *
 * @author Stanislav Vitvitskiy
 */
public class ScriptTag extends Tag {
    Map events;

    public ScriptTag(long streamId, long timestamp, Map events) {
        super(TagType.SCRIPT, streamId, timestamp);
        this.events = events;
    }

    public Map getEvents() {
        return events;
    }
}
