/**
 * 
 */
package mp4.util.atom;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

public abstract class LeafAtom extends Atom {
  // the raw mpeg4 data for the atom
  protected ByteStream data;
  
  protected static final int VERSION_OFFSET = 0;
  protected static final int FLAG_OFFSET = 1;
  protected static final int FLAG_SIZE = 3;
  
  /**
   * Constructor for a leaf Atom
   * @param type the atom's type
   */
  protected LeafAtom(byte[] type) {
    super(type);
  }
  
  /**
   * Copy constructor for the leaf atom.  Performs a deep copy
   * @param old the version to copy
   */
  protected LeafAtom(LeafAtom old) {
    super(old);
    data = new ByteStream(old.data);
  }
  
  /**
   * Return the version value for the atom. Currently, we support only 0, which
   * means 
   * @return
   */
  public byte getVersion() {
    return data.getData(VERSION_OFFSET);
  }
  
  /**
   * Set the version value for the atom.
   * @param version the atom's version
   */
  public void setVersion(byte version) {
    data.addData(VERSION_OFFSET, version);
  }
  
  /**
   * Return the flag data from the atom as a byte array
   * @return the flag data from the atom as a byte array
   */
  public byte[] getFlag() {
    return data.getData(FLAG_OFFSET, FLAG_SIZE);
  }
  
  /**
   * Add the flag data to the byte stream
   * @param flag the flag info
   */
  public void setFlag(byte[] flag) {
    data.addData(FLAG_OFFSET, flag);
  }
  
  /**
   * Return false since a leaf is not a container of other aotoms
   * @return false
   */
  public boolean isContainer() {
    return false;
  }
  
  /**
   * Write the byte stream to the specified output.
   * @param out where the output goes
   * @throws IOException if there is a problem writing the data
   */
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    data.writeData(out);
  }
  
  /**
   * Read the data from the input stream in to the atom.
   * @param in the input stream
   * @throws AtomException
   */
  public void readData(DataInputStream in) throws AtomException {
    data = new ByteStream(dataSize());
    try {
      data.read(in);
    } catch (IOException e) {
      throw new AtomException("IOException while reading mp4 file");
    }
  }
  
  /**
   * Allocate space for the data needed by the atom.
   * @param size the size of data in bytes
   */
  public void allocateData(long size) {
    assert data == null;
    data = new ByteStream(size);
    data.reserveSpace(size);
    setSize(size + ATOM_HEADER_SIZE);
  }
  
}