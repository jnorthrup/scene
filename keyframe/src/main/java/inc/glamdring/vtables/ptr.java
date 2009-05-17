package inc.glamdring.vtables;

import java.nio.ByteBuffer;

/**
 * pointer class -- approximation of c++ '*'
 *
 * @author jim
 */
public class ptr implements edge<ByteBuffer, Integer> {
    private ByteBuffer l;


    public ByteBuffer left(edge<ByteBuffer, Integer> e) {
        if (this != e) midpoint(e.left(e), e.right(e));
        return l$();
    }

    public Integer right(edge<ByteBuffer, Integer> e) {
        if (this != e)
            midpoint(e.left(e), e.right(e));
        return $r();
    }

    public edge<ByteBuffer, Integer> midpoint(ByteBuffer byteBuffer, Integer r) {

        l = (ByteBuffer) byteBuffer.duplicate().position(r);
        return this;
    }

    public ByteBuffer reify(ptr ptr1) {
        return ptr1.l$();

    }

    public ByteBuffer l$() {
        return l;
    }

    public Integer $r() {
        return left(this).position();
    }
}