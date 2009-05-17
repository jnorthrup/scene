package hideftvads;

import static hideftvads.AtomRegisters.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


public class AtomVisitor {

    /**
     * len is going to mark the stack
     *
     * @param src
     * @param register
     * @param stack
     */
    void len(ByteBuffer src, int[] register, IntBuffer stack) {
        stack.put(register, 0, 4);
        register[AtomRegisters.GLOBAL_ATOM_COUNT.ordinal()]++;
        register[CUR_ATOM_STACK.ordinal()] = stack.position();

        int pos = src.position();
        register[CUR_ATOM_SRC.ordinal()] = pos;

        int data = src.getInt();
        register[CUR_ATOM_LEN.ordinal()] = data;
        register[NEXT_SIBLING_SRC.ordinal()] = data + pos;
    }

}