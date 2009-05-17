package hideftvads;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 104
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>description</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>Atom</td><td>0x8</td><td>0x0</td><td></td><td>long</td><td>{@link hideftvads.Atom}</td></tr>
 * <tr><td>version</td><td>0x1</td><td>0x8</td><td></td><td>byte</td><td>{@link mvhdVisitor#version(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>flags</td><td>0x3</td><td>0x9</td><td></td><td>byte[]</td><td>{@link mvhdVisitor#flags(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>creationTime</td><td>0x4</td><td>0xc</td><td></td><td>int</td><td>{@link mvhdVisitor#creationTime(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>modificationTime</td><td>0x4</td><td>0x10</td><td></td><td>int</td><td>{@link mvhdVisitor#modificationTime(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>timeScale</td><td>0x4</td><td>0x14</td><td></td><td>int</td><td>{@link mvhdVisitor#timeScale(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>duration</td><td>0x4</td><td>0x18</td><td></td><td>int</td><td>{@link mvhdVisitor#duration(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>preferredRate</td><td>0x4</td><td>0x1c</td><td></td><td>int</td><td>{@link mvhdVisitor#preferredRate(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>preferredVolume</td><td>0x2</td><td>0x20</td><td></td><td>short</td><td>{@link mvhdVisitor#preferredVolume(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>reserved</td><td>0xa</td><td>0x22</td><td></td><td>byte[]</td><td>{@link mvhdVisitor#reserved(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>matrixStructure</td><td>0x24</td><td>0x2c</td><td></td><td>byte[]</td><td>{@link mvhdVisitor#matrixStructure(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>preview_time</td><td>0x4</td><td>0x50</td><td></td><td>int</td><td>{@link mvhdVisitor#preview_time(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>previewDuration</td><td>0x4</td><td>0x54</td><td></td><td>int</td><td>{@link mvhdVisitor#previewDuration(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>selectionTime</td><td>0x4</td><td>0x58</td><td></td><td>int</td><td>{@link mvhdVisitor#selectionTime(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>selectionDuration</td><td>0x4</td><td>0x5c</td><td></td><td>int</td><td>{@link mvhdVisitor#selectionDuration(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>currentTime</td><td>0x4</td><td>0x60</td><td></td><td>int</td><td>{@link mvhdVisitor#currentTime(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>nextTrackId</td><td>0x4</td><td>0x64</td><td></td><td>int</td><td>{@link mvhdVisitor#nextTrackId(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see hideftvads.mvhd#Atom
 * @see hideftvads.mvhd#version
 * @see hideftvads.mvhd#flags
 * @see hideftvads.mvhd#creationTime
 * @see hideftvads.mvhd#modificationTime
 * @see hideftvads.mvhd#timeScale
 * @see hideftvads.mvhd#duration
 * @see hideftvads.mvhd#preferredRate
 * @see hideftvads.mvhd#preferredVolume
 * @see hideftvads.mvhd#reserved
 * @see hideftvads.mvhd#matrixStructure
 * @see hideftvads.mvhd#preview_time
 * @see hideftvads.mvhd#previewDuration
 * @see hideftvads.mvhd#selectionTime
 * @see hideftvads.mvhd#selectionDuration
 * @see hideftvads.mvhd#currentTime
 * @see hideftvads.mvhd#nextTrackId
 * </table>
 */
public enum mvhd { 
Atom(0x8)	{{
		___subrecord___=hideftvads.Atom.class;
	}}
,version(0x1),flags(0x3),creationTime(0x4),modificationTime(0x4),timeScale(0x4),duration(0x4),preferredRate(0x4),preferredVolume(0x2),reserved(0xa),matrixStructure(0x24),preview_time(0x4),previewDuration(0x4),selectionTime(0x4),selectionDuration(0x4),currentTime(0x4),nextTrackId(0x4);
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
    /** mvhd templated Byte Struct 
     * @param dimensions [0]=___size___,[1]= forced ___seek___
     */
	mvhd (int... dimensions) {
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
        for (mvhd mvhd_ : values()) {
            String hdr = mvhd_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            mvhd_.subIndex(src, register, stack);
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
//@@ #endmvhd