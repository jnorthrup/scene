/**
 * 
 */
package mp4.util.atom;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;


/**
 * The media data atom.  This atom holds the video frames and sound tracks.
 */
public class MdatAtom extends LeafAtom {
  // the input stream contains the data.  we use the stream instead of a data file
  // because the amount of data is so large.
  private DataInputStream in;
   
  /**
   * Construct an empty mdat atom
   */
  public MdatAtom() {
    super(new byte[]{'m','d','a','t'});
  }
  
  /**
   * Copy constructor for the mdat atom.  Performs a deep copy
   * @param old the version to copy
   */
  public MdatAtom(MdatAtom old) {
    super(old);
  }
  
  /**
   * Set the data input stream for the mdata atom.  The input stream points
   * at the start of the video and sound data.
   * @param in the input stream.
   */
  public void setInputStream(DataInputStream in) {
    this.in = in;
  }
  /**
   * Cut the mdat atom by skipping the specified number of bytes.  This 
   * action does not create a new mdat atom.  Instead, it alters the existing
   * atom to skip over the appropriate data.
   * @param skip the number of bytes to skip
   * @return an altered mdat atom that has skipped over the approriate data.
   */
  public MdatAtom cut(long skip) {
    try {
      in.skipBytes((int)skip);
      long newSize = dataSize() - skip;
      setSize(ATOM_HEADER_SIZE + newSize);
    } catch (IOException e) {
      throw new AtomError("Unable to cut the mdat atom");
    }
    return this;
  }
  
  /**
   * Write the video and sound data to the specified output
   * @param out the specified output
   * @throws IOException if there is a problem writing the data
   */
  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    long numBytesToRead = dataSize();
    // read data in using 100 MB chunks
    byte[] input = new byte[100*1024*1024];
    while (numBytesToRead > 0) {
      int read = in.read(input);
      out.write(input, 0, read);
      numBytesToRead -= read;
    }
  }

  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
 }