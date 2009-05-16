/**
 * 
 */
package mp4.util.atom;

/**
 * The media header atom.
 */
public class MdhdAtom extends LeafAtom {
  private static final int CREATION_TIME_OFFSET = 4;
  private static final int MODIFICATION_TIME_OFFSET = 8;
  private static final int TIMESCALE_OFFSET = 12;
  private static final int DURATION_OFFSET = 16;
  private static final int LANGUAGE_OFFSET = 20;
  private static final int QUALITY_OFFSET = 22;
  
  /**
   * Construct and empty mdhd atom.
   */
  public MdhdAtom() {
    super(new byte[]{'m','d','h','d'});
  }
  
  /**
   * Copy constructor.  Performs a deep copy
   * @param old the version to copy
   */
  public MdhdAtom(MdhdAtom old) {
    super(old);
  }
  
  /**
   * Return the creation time for the atom.  In seconds, from midnight January 1, 1904.
   * @return the creation time for the atom.
   */
  public long getCreationTime() {
    return data.getUnsignedInt(CREATION_TIME_OFFSET);
  }
  
  /**
   * Set the atom's creation time.
   * @param ct the creation time.
   */
  public void setCreationTime(long ct) {
    data.addUnsignedInt(CREATION_TIME_OFFSET, ct);
  }
  
  /**
   * Get the modification time for the atom.  In seconds, from midnight
   * Jan 1, 1904.
   * @return the modification time for the atom.
   */
  public long getModifactionTime() {
    return data.getUnsignedInt(MODIFICATION_TIME_OFFSET);
  }
  
  /**
   * Set the modification time for the atom.  In seconds, from midnight January
   * 1, 1904.
   * @param mt the modification time
   */
  public void setModificationTime(long mt) {
    data.addUnsignedInt(MODIFICATION_TIME_OFFSET, mt);
  }
  
  /**
   * Return the time scale for the mvhd atom
   * @return the time scale
   */
  public long getTimeScale() {
    return data.getUnsignedInt(TIMESCALE_OFFSET);
  }
  
  /**
   * Set the timeScale for the media header atom
   * @param timeScale the media's time scale
   */
  public void setTimeScale(long timeScale) {
    data.addUnsignedInt(TIMESCALE_OFFSET, timeScale);
  }
  
  /**
   * Return the duration for the mvhd atom
   * @return the duration
   */
  public long getDuration() {
    return data.getUnsignedInt(DURATION_OFFSET);
  }
  
  /**
   * Set the duration for the media header atom
   * @param duration the duration value
   */
  public void setDuration(long duration) {
    data.addUnsignedInt(DURATION_OFFSET, duration);
  }
  
  /**
   * Return the duration of the media divided by the time-scale.
   * @return the normalized duration of the media;
   */
  public long getDurationNormalized() {
    return getDuration() / getTimeScale();
  }
    
  /**
   * Cut the mdhd atom at the specified time.  This changes the duration value only, 
   * which is calculated using the new stbl atom.
   * @return a new mdhd atom (duration value must be changed by the caller)
   */
  public MdhdAtom cut() {
    // create a copy of the mdhd data
    MdhdAtom cutMdhd = new MdhdAtom(this);
    // change the duration value based upon the time
    cutMdhd.setDuration(0);
    return cutMdhd;
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}