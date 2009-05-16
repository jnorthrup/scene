package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;

/**
 * This class represents an atom in the mpeg-4 file.  See the mpeg-4 file
 * documentation:
 * <ul>
 *   <li><a href="http://standards.iso.org/ittf/PubliclyAvailableStandards/index.html">ISO Standard 14496-12:2005</a>
 *   <li><a href="developer.apple.com/DOCUMENTATION/QuickTime/QTFF/qtff.pdf ">QuickTime File Format Specification</a>
 * </ul>
 * 
 * In the ISO spec, an Atom is a Box.  We use the QuickTime name, Atom.
 */
public abstract class Atom {
  // the size of the atom, an unsigned int so we use a long
  protected long size;
  // the type, represented using the characters in the byte stream
  protected byte[] type;
  
  // The basic unit size of an atom, in bytes
  public static final int ATOM_WORD = 4;
  // The canonical atom size, which includes the type and the size
  public static final int ATOM_HEADER_SIZE = 8;
  
  /**
   * Create an atom with the specified size and type
   * @param size the size inclusive of the size and type 
   * @param type the atom's type
   */
  protected Atom(long size, byte[] type) {
    this.size = size;
    this.type = type;
  }
  
  /**
   * Copy constructor.
   * @param old the atom to copy
   */
  protected Atom(Atom old) {
    this.size = old.size;
    this.type = old.type;
  }
  
  /**
   * Create an atom but the size isn't specified, and needs
   * to be filled in later.
   * @param type the atom's type
   */
  protected Atom(byte[] type) {
    this.type = type;
  }
  
  /**
   * Set the size of the atom
   * @param size the atom's size
   */
  public void setSize(long size) {
    this.size = size;
  }
  
  /**
   * Return the size of the atom
   * @return
   */
  public long size() {
    return size;
  }  
  /**
   * Return the size of the data part of the atom
   * @return the size of the atom's data part
   */
  public long dataSize() {
    return size - Atom.ATOM_HEADER_SIZE;
  }

 /**
   * Return the atom's type as an integer
   * @return the atom's type as an integer
   */
  public int getType() {
    return getAtomValue(type);
  }
  
  /**
   * @return true if this atom is a container atom
   */
  public abstract boolean isContainer();
  
  /**
   * The visitor pattern accept method
   * @param v an atom visitor
   */
  public abstract void accept(AtomVisitor v) throws AtomException;
  
  /**
   * Write the atom to the specified output
   * @param out where the output goes
   * @throws IOException if there is an error when writing the data
   */
  public abstract void writeData(DataOutput out) throws IOException;
  
  /**
   * Write the atom header data to the output stream.  The header data
   * includes the size and type information.
   * @param out where the output goes
   * @throws IOException if there is an error writing the data
   */
  public void writeHeader(DataOutput out) throws IOException {
    byte[] sizeData = new byte[ATOM_WORD];
    unsignedIntToByteArray(sizeData, 0, size);
    out.write(sizeData);
    out.write(type);
  }
  
  /**
   * Return the atom as a string
   * @return the string for the atom
   */
  public String toString() {
    return "Atom " + new String(type) + " size " + size;
  }
  
  /**
   * Utility function that converts a byte array to an integer starting
   * at the specified array index.
   * The byte array must be at least 4 bytes in length.
   * @param b the byte array
   * @param off offset to start the conversion
   * @return the integer value of the byte array
   */
  public static final int byteArrayToInt(byte[] b, int off) {
    return (int) byteArrayToUnsignedInt(b, off);
  }
  
  /**
   * Java doesn't have unsigned types, so we need to use the next
   * larger signed type.
   * @param b the byte array
   * @param off offset to start the conversion
   * @return the unsigned integer value of the byte array
   */
  public static final long byteArrayToUnsignedInt(byte[] b, int off) {
    return ((long)(b[off] & 0xff) << 24) |
    ((long)(b[off+1] & 0xff) << 16) |
    ((long)(b[off+2] & 0xff) << 8) |
    (long)(b[off+3] & 0xff);  
  }
  
  /**
   * Write the unsigned int to the byte array
   * @param b the byte array
   * @param off the offset into the byte array
   * @param data the data
   */
  public static final void unsignedIntToByteArray(byte b[], int off, long data) {
    b[off] = (byte) ((data >> 24) & 0xff);
    b[off+1] = (byte) ((data >> 16) & 0xff);
    b[off+2] = (byte) ((data >> 8) & 0xff);
    b[off+3] = (byte) (data & 0xff);
  }
    
  /**
   * Convert the atom type characters to an integer value
   * @param val the characters
   * @return the integer
   */
  public static final int getAtomValue(byte[] val) {
    return Atom.byteArrayToInt(val, 0);
  }
  
  /**
   * Convert the type name to a class name.  The class name converts
   * the first character of the type to uppercase, then prepends
   * the package name, and appends 'Atom' string.
   * 
   * @param typ the type represented as a byte array
   * @return the class name for the type
   */
  public static String typeToClassName(byte[] typ) {
    String str = new String(typ);
    String clsName = str.substring(0, 1).toUpperCase() + str.substring(1);
    return "mp4.util.atom." + new String(clsName) + "Atom";
  }
  
}