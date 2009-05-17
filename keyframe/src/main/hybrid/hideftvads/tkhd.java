package hideftvads;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 64
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>Atom</td><td>0x8</td><td>0x0</td><td></td><td>long</td><td>{@link hideftvads.Atom}</td></tr>
 * <tr><td>version</td><td>0x1</td><td>0x8</td><td></td><td>byte</td><td>{@link tkhdVisitor#version(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>flags</td><td>0x3</td><td>0x9</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#flags(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>creationTime</td><td>0x0</td><td>0xc</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#creationTime(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>modificationTime</td><td>0x0</td><td>0xc</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#modificationTime(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>trackId</td><td>0x0</td><td>0xc</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#trackId(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>reserved</td><td>0x8</td><td>0xc</td><td></td><td>long</td><td>{@link tkhdVisitor#reserved(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>layer</td><td>0x2</td><td>0x14</td><td></td><td>short</td><td>{@link tkhdVisitor#layer(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>alternateGroup</td><td>0x2</td><td>0x16</td><td></td><td>short</td><td>{@link tkhdVisitor#alternateGroup(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>volume</td><td>0x2</td><td>0x18</td><td></td><td>short</td><td>{@link tkhdVisitor#volume(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>reserved2</td><td>0x2</td><td>0x1a</td><td></td><td>short</td><td>{@link tkhdVisitor#reserved2(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>matrixStructure</td><td>0x24</td><td>0x1c</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#matrixStructure(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>trackWidth</td><td>0x0</td><td>0x40</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#trackWidth(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>trackHeight</td><td>0x0</td><td>0x40</td><td></td><td>byte[]</td><td>{@link tkhdVisitor#trackHeight(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see hideftvads.tkhd#Atom
 * @see hideftvads.tkhd#version
 * @see hideftvads.tkhd#flags
 * @see hideftvads.tkhd#creationTime
 * @see hideftvads.tkhd#modificationTime
 * @see hideftvads.tkhd#trackId
 * @see hideftvads.tkhd#reserved
 * @see hideftvads.tkhd#layer
 * @see hideftvads.tkhd#alternateGroup
 * @see hideftvads.tkhd#volume
 * @see hideftvads.tkhd#reserved2
 * @see hideftvads.tkhd#matrixStructure
 * @see hideftvads.tkhd#trackWidth
 * @see hideftvads.tkhd#trackHeight
 * </table>
 */
public enum tkhd { 
Atom(0x8)	{{
		___subrecord___=hideftvads.Atom.class;
	}}
,version(0x1),flags(0x3),creationTime,modificationTime,trackId,reserved(0x8),layer(0x2),alternateGroup(0x2),volume(0x2),reserved2(0x2),matrixStructure(0x24),trackWidth,trackHeight;
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
	
	
	
	
	
	public static final boolean ___isRecord___=false;
	public static final boolean ___isValue___=false;
	public static final boolean ___isHeader___=false;
	public static final boolean ___isRef___=false;
	public static final boolean ___isInfo___=false;
    /** tkhd templated Byte Struct 
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
	tkhd (int... dimensions) {
        int[] dim = init(dimensions);
        ___size___ = dim[0];
        ___seek___ = dim[1];

    }

    int[] init(int... dimensions) {
        int size = dimensions.length > 0 ? dimensions[0] : 0,
                seek= dimensions.length > 1 ? dimensions[1] : 0;

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
        for (tkhd tkhd_ : values()) {
            String hdr = tkhd_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            tkhd_.subIndex(src, register, stack);
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
*/        }
    }}
//@@ #endtkhd