package hideftvads;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 12
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>Atom</td><td>0x8</td><td>0x0</td><td></td><td>long</td><td>{@link hideftvads.Atom}</td></tr>
 * <tr><td>version</td><td>0x1</td><td>0x8</td><td></td><td>byte</td><td>{@link elstVisitor#version(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>flags</td><td>0x3</td><td>0x9</td><td></td><td>byte[]</td><td>{@link elstVisitor#flags(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>elstEntris</td><td>0x0</td><td>0xc</td><td></td><td>byte[]</td><td>{@link elstVisitor#elstEntris(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>editListVariable</td><td>0x0</td><td>0xc</td><td></td><td>byte[]</td><td>{@link elstVisitor#editListVariable(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see hideftvads.elst#Atom
 * @see hideftvads.elst#version
 * @see hideftvads.elst#flags
 * @see hideftvads.elst#elstEntris
 * @see hideftvads.elst#editListVariable
 * </table>
 */
public enum elst { 
Atom(0x8)	{{
		___subrecord___=hideftvads.Atom.class;
	}}
,version(0x1),flags(0x3),elstEntris,editListVariable;
	public java.lang.Class ___valueclass___;

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
    /** elst templated Byte Struct 
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
	elst (int... dimensions) {
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
        for (elst elst_ : values()) {
            String hdr = elst_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            elst_.subIndex(src, register, stack);
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
//@@ #endelst