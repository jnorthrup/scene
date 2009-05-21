package hideftvads;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 0
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>PREV_ATOM_STACK</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#PREV_ATOM_STACK(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>PARENT_ATOM_STACK</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#PARENT_ATOM_STACK(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>CUR_ATOM_ID</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#CUR_ATOM_ID(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>CUR_ATOM_STACK</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#CUR_ATOM_STACK(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>CUR_ATOM_SRC</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#CUR_ATOM_SRC(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>CUR_ATOM_LEN</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#CUR_ATOM_LEN(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>NEXT_ATOM_SRC</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#NEXT_ATOM_SRC(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>NEXT_SIBLING_SRC</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#NEXT_SIBLING_SRC(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>GLOBAL_ATOM_COUNT</td><td>0x4</td><td>0x0</td><td></td><td>int</td><td>{@link AtomRegistersVisitor#GLOBAL_ATOM_COUNT(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see hideftvads.AtomRegisters#PREV_ATOM_STACK
 * @see hideftvads.AtomRegisters#PARENT_ATOM_STACK
 * @see hideftvads.AtomRegisters#CUR_ATOM_ID
 * @see hideftvads.AtomRegisters#CUR_ATOM_STACK
 * @see hideftvads.AtomRegisters#CUR_ATOM_SRC
 * @see hideftvads.AtomRegisters#CUR_ATOM_LEN
 * @see hideftvads.AtomRegisters#NEXT_ATOM_SRC
 * @see hideftvads.AtomRegisters#NEXT_SIBLING_SRC
 * @see hideftvads.AtomRegisters#GLOBAL_ATOM_COUNT
 * </table>
 */
public enum AtomRegisters { 
PREV_ATOM_STACK,PARENT_ATOM_STACK,CUR_ATOM_ID,CUR_ATOM_STACK,CUR_ATOM_SRC,CUR_ATOM_LEN,NEXT_ATOM_SRC,NEXT_SIBLING_SRC,GLOBAL_ATOM_COUNT;
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
     * if we find this, we use it for sub-index.
     */
	final public Method ___visitorMethod___;
	/**
     * a hint class for bean-wrapper access to data contained.
     */
	public Class ___valueClass___;
	
	
	
	
	
	public static final boolean ___isRecord___=false;
	public static final boolean ___isValue___=false;
	public static final boolean ___isHeader___=false;
	public static final boolean ___isRef___=false;
	public static final boolean ___isInfo___=false;
    /** AtomRegisters templated Byte Struct 
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
	AtomRegisters (int... dimensions) {
        Method method = null;try {method = Class.forName(getClass().getName() + "Visitor").getMethod(name(), ByteBuffer.class, int[].class, IntBuffer.class);}catch (Exception e) {}
        ___visitorMethod___ = method;

        int[] dim = init(dimensions);
        ___size___ = dim[0];
        ___seek___ = dim[1];

    }

    int[] init(int... dimensions) {
        int size = dimensions.length > 0 ? dimensions[0] : 0,
                seek= dimensions.length > 1 ? dimensions[1] : 0;
        if (___visitorMethod___==null&&___subrecord___ == null) {            final String[] indexPrefixes = {"", "s", "_", "Index", "Length", "Ref", "Header", "Info", "Table"};
            for (String indexPrefix : indexPrefixes) {
                try {___subrecord___ = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + indexPrefix);
                    try {size = ___subrecord___.getField("___recordlen___").getInt(null);
                    } catch (Exception ignored) {}
                    break;} catch (Exception ignored) {}}
        }

        for (String vPrefixe1 : new String[]{"_", "", "$", "Value",}) {
            if (___valueClass___ != null) break;
            String suffix = vPrefixe1;
            for (String name1 : new String[]{name().toLowerCase(), name(),}) {
                if (___valueClass___ != null) break;
                final String trailName = name1;
                if (trailName.endsWith(suffix)) {
                    for (String aPackage1 : new String[]{"",
                            getClass().getPackage().getName() + ".",
                            "java.lang.",
                            "java.util.",
                    })
                        if (___valueClass___ == null) break;
                        else
                            try {
                                ___valueClass___ = Class.forName(aPackage1 + name().replace(suffix, ""));
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
        for (AtomRegisters AtomRegisters_ : values()) {
            String hdr = AtomRegisters_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            AtomRegisters_.subIndex(src, register, stack);
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
        if (___visitorMethod___ != null) try {
            ___visitorMethod___.invoke(null, src, register, stack);
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.err.println(name() + ":subIndex src:stack" + src.position() + ':' + stack.position());
        int begin = src.position();
        int stackPtr = stack.position();
        stack.put(begin);
        if (___isRecord___ && ___subrecord___ != null) {        }
    }}
//@@ #endAtomRegisters