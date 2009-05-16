/**
 * 
 */
package mp4.util.atom;

/**
 * The sample description atom gives detailed information about the coding
 * type, including information specific to the type of track.  The format
 * depends on where the media is sound, video, or hint
 */
public class StsdAtom extends LeafAtom {
  private static final int ENTRY_OFFSET = 4;
  // common sample entry offsets
  private static final int ENTRY_SIZE_OFFSET = 8;
  @SuppressWarnings("unused") // This value is 0
  private static final int RESERVED_OFFSET = 16;
  private static final int DATA_REFERENCE_OFFSET = 22;
  // sound offsets
  
  // video offsets
  private static final int PREDEFINED_OFFSET = 24;
  private static final int RESERVED1_OFFSET = 26;
  private static final int PREDEFINED1_OFFSET = 28;
  private static final int WIDTH_OFFSET = 40;
  private static final int HEIGHT_OFFSET = 42;
  @SuppressWarnings("unused") // This value is 0x00480000
  private static final int HORIZONTAL_RESOLUTION = 44;
  @SuppressWarnings("unused") // This value is 0x00480000
  private static final int VERTICAL_RESOLUTION = 48;
  private static final int RESERVED2_OFFSET = 52;
  @SuppressWarnings("unused") // This value is always 1
  private static final int FRAME_COUNT_OFFSET = 56;
  private static final int COMPRESSOR_OFFSET = 58;
  @SuppressWarnings("unused") // This value is 0x0018
  private static final int DEPTH_OFFSET = 62;
  @SuppressWarnings("unused") // This value is -1 (0xFFFF)
  private static final int PREDEFINED2_OFFSET = 64;
  
  /**
   * Constructor that creates an empty stsd atom.
   */
  public StsdAtom() {
    super(new byte[]{'s','t','s','d'});
  }
  
  /**
   * Copy constructor.  Performs a deep copy
   * @param old the version to copy
   */
  public StsdAtom(StsdAtom old) {
    super(old);
  }
  
  /**
   * Cut the stsd atom at the specified time.  Nothing changes for the sample
   * description atom
   * @return a new stsd atom 
   */
  public StsdAtom cut() {
    return new StsdAtom(this);
  }
  
  /**
   * Return the number of entries in the stsd atom.
   * @return the number of entries in the stsd atom
   */
  public long getNumEntries() {
    return data.getUnsignedInt(ENTRY_OFFSET);
  }
  
  /**
   * Return the video width in pixels
   * @return the video width in pixels
   */
  public int getWidth() {
    return data.getUnsignedShort(WIDTH_OFFSET);
  }
  
  /**
   * Return the video height in pixels
   * @return the video height in pixels
   */
  public int getHeight() {
    return data.getUnsignedShort(HEIGHT_OFFSET);
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}