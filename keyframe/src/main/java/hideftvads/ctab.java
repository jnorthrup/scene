package hideftvads;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * <p>recordSize: 32
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>Atom</td><td>0x8</td><td>0x0</td><td></td><td>long</td><td>{@link hideftvads.Atom}</td></tr>
 * <tr><td>colortableSeed</td><td>0x4</td><td>0x8</td><td></td><td>int</td><td>{@link ctabVisitor#colortableSeed(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>colorTableFlags</td><td>0x2</td><td>0xc</td><td></td><td>short</td><td>{@link ctabVisitor#colorTableFlags(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>colorTableSize</td><td>0x2</td><td>0xe</td><td></td><td>short</td><td>{@link ctabVisitor#colorTableSize(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>colorArray</td><td>0x10</td><td>0x10</td><td></td><td>byte[]</td><td>{@link hideftvads.colorArray}</td></tr>
 *
 * @see hideftvads.ctab#Atom
 * @see hideftvads.ctab#colortableSeed
 * @see hideftvads.ctab#colorTableFlags
 * @see hideftvads.ctab#colorTableSize
 * @see hideftvads.ctab#colorArray
 *      </table>
 */
public enum ctab {
    Atom(0x8) {{
        ___subrecord___ = hideftvads.Atom.class;
    }}, colortableSeed(0x4), colorTableFlags(0x2), colorTableSize(0x2), colorArray(0x10) {{
        ___subrecord___ = hideftvads.colorArray.class;
    }};
    /**
     * the length of one record
     */
    public static int ___recordlen___;
    /**
     * the size per field, if any
     */
    public final int ___size___;
    /**
     * the offset from record-start of the field
     */
    public final int ___seek___;
    /**
     * a delegate class which will perform sub-indexing on behalf of a field once it has marked its initial starting
     * offset into the stack.
     */
    public Class<? extends Enum> ___subrecord___;
    /**
     * a hint class for bean-wrapper access to data contained.
     */
    public Class ___valueclass___;


    public static final boolean ___isRecord___ = false;
    public static final boolean ___isValue___ = false;
    public static final boolean ___isHeader___ = false;
    public static final boolean ___isRef___ = false;
    public static final boolean ___isInfo___ = false;

    /**
     * ctab templated Byte Struct
     *
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
    ctab(int... dimensions) {
        int[] dim = init(dimensions);
        ___size___ = dim[0];
        ___seek___ = dim[1];

    }

    int[] init(int... dimensions) {
        int size = dimensions.length > 0 ? dimensions[0] : 0,
                seek = dimensions.length > 1 ? dimensions[1] : 0;

        if (___subrecord___ == null) {
            final String[] indexPrefixes = {"", "s", "_", "Index", "Length", "Ref", "Header", "Info", "Table"};
            for (String indexPrefix : indexPrefixes) {
                try {
                    ___subrecord___ = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + indexPrefix);
                    try {
                        size = ___subrecord___.getField("___recordlen___").getInt(null);
                    } catch (Exception e) {
                    }
                    break;
                } catch (ClassNotFoundException e) {
                }
            }
        }

        for (String vPrefixe1 : new String[]{"_", "", "$", "Value",}) {
            if (___valueclass___ != null) break;
            String suffix = vPrefixe1;
            for (String name1 : new String[]{name().toLowerCase(), name(),}) {
                if (___valueclass___ != null) break;
                final String trailName = name1;
                if (trailName.endsWith(suffix)) {
                    for (String aPackage1 : new String[]{"",
                            getClass().getPackage().getName() + ".",
                            "java.lang.",
                            "java.util.",
                    })
                        if (___valueclass___ == null) break;
                        else
                            try {
                                ___valueclass___ = Class.forName(aPackage1 + name().replace(suffix, ""));
                            } catch (ClassNotFoundException e) {
                            }
                }
            }
        }

        seek = ___recordlen___;
        ___recordlen___ += size;

        return new int[]{size, seek};
    }

    /**
     * The struct's top level method for indexing 1 record. Each Enum field will call SubIndex
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    static void index
            (ByteBuffer src, int[] register, IntBuffer stack) {
        for (ctab ctab_ : values()) {
            String hdr = ctab_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ctab_.subIndex(src, register, stack);
        }
    }

    /**
     * Each of the Enums can override thier deault behavior of "___seek___-past"
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        System.err.println(name() + ":subIndex src:stack" + src.position() + ':' + stack.position());
        int begin = src.position();
        int stackPtr = stack.position();
        stack.put(begin);
        if (___isRecord___ && ___subrecord___ != null) {
            /*                 try {
                            final hideftvads.Atom table = hideftvads.Atom.valueOf(___subrecord___.getSimpleName());
                            if (table != null) {
                                //stow the original location
                                int mark = stack.position();
                                stack.position((register[Atom.Atom.ordinal()] + table.___seek___) / 4);
                                ___subrecord___.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);
                                //resume the lower stack activities
                                stack.position(mark);
                            }
                        } catch (Exception e) {
                            throw new Error(e.getMessage());
                        }
            */
        }
    }
}
//@@ #endctab