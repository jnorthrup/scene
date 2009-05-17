package inc.glamdring.vtables;

import com.thoughtworks.xstream.XStream;

import java.nio.ByteBuffer;

/**
 * ref class -- approximation of c++ '&'
 * User: jim
 * Date: Sep 20, 2008
 * Time: 12:27:26 AM
 */

public class ref<referent> implements edge<referent, ptr> {
    static final XStream X_STREAM = new XStream();
    referent l;
    ptr r;

    public referent referent() {
        return l;
    }

    /**
     * grab left from the incoming, if any,
     * return left in all cases.
     *
     * @param e
     * @return
     */
    public referent left(edge<referent, ptr> e) {

        if (e != this)
            midpoint(e.left(e), e.right(e));
        return referent();
    }

    public ptr pointed() {
        return r;
    }

    /**
     * grab right from the incoming, if any,
     * and return right in all cases.
     *
     * @param edge
     * @return
     */
    public ptr right(edge<referent, ptr> edge) {
        if (edge != this) {
            midpoint(edge.left(edge), edge.right(edge));
        }
        return pointed();
    }


    /**
     * bind and write pointer
     *
     * @param ?   object
     * @param ref heap waiting for a write, several
     * @return ussualy this
     */
    public edge<referent, ptr> midpoint(referent referent, ptr ref) {
        byte[] bytes = null;
        l = referent;

        if (bytes == null) bytes = X_STREAM.toXML(referent).getBytes();
        Integer integer = ref.$r();
        this.r = (ptr) ref.midpoint(ref.left(ref).putInt(bytes.length).put(bytes), integer);
        return this;
    }

    /**
     * reads object from the first ptr sent in, or returns the most local version
     *
     * @param void$
     * @return
     */
    @SuppressWarnings("unchecked")
    public referent reify(ptr void$) {/*
        for (voidp voidp : voidp) */
        {
            ByteBuffer buffer = void$.l$();
            Integer integer = void$.$r();
            buffer.getInt(integer);
            ByteBuffer buffer1 = (ByteBuffer) buffer.slice().limit(integer);
            String s = buffer1.asCharBuffer().toString();
            referent fromXML = (referent) X_STREAM.fromXML(s);
            return fromXML;
        }
    }

}
