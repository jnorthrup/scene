package ua.org.jplayer.javformat.iso.model;

/**
 * A generic pair of values
 *
 * @author Stanislav Vitvitskiy
 */
public class Pair {
    public Pair(Object one, Object two) {
        this.one = one;
        this.two = two;
    }

    private Object one;
    private Object two;

    public Object getOne() {
        return one;
    }

    public Object getTwo() {
        return two;
    }
}