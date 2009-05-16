package mp4.util.atom;

/**
 * The movie header atom.
 */
public class MvhdAtom extends LeafAtom {
  private static final int CREATION_TIME_OFFSET = 4;
  private static final int MODIFICATION_TIME_OFFSET = 8;
  private static final int TIMESCALE_OFFSET = 12;
  private static final int DURATION_OFFSET = 16;
  private static final int RATE_OFFSET = 20;
  private static final int VOLUME_OFFSET = 24;
  private static final int RESERVED_OFFSET = 26;
  private static final int MATRIX_STRUCTURE_OFFSET = 36;
  // quicktime has other data in the pre_defined space
  private static final int PRE_DEFINED_OFFSET = 72;
  private static final int NEXT_TRACK_ID_OFFSET = 76;
  
  /**
   * Construct an empty mvhd atom.
   */
  public MvhdAtom() {
    super(new byte[]{'m','v','h','d'});
  }
  
  /**
   * Copy constructor.  Perform a deep copy.
   * @param old the old version to copy
   */
  public MvhdAtom(MvhdAtom old) {
    super(old);
  }
  
  /**
   * Return the creation time of the presentation.  In seconds, since
   * midnight January 1, 1904.
   * @return the creation time of the presentation.
   */
  public long getCreationTime() {
    return data.getUnsignedInt(CREATION_TIME_OFFSET);
  }
  
  /**
   * Set the creation time of the presentation.  In seconds, since
   * midnight January 1, 1904.
   * @param ct the creation time
   */
  public void setCreationTime(long ct) {
    data.addUnsignedInt(CREATION_TIME_OFFSET, ct);
  }
  
  /**
   * Return the modification time of the presentation.  In seconds,
   * since midnight January 1, 1904.
   * @return the modification time
   */
  public long getModificationTime() {
    return data.getUnsignedInt(MODIFICATION_TIME_OFFSET);
  }
  
  /**
   * Set the modification time of the presentation.  In seconds,
   * since midnight January 1, 1904.
   * @param mt the modification time
   */
  public void setModificationTime(long mt) {
    data.addUnsignedInt(MODIFICATION_TIME_OFFSET, mt);
  }
  
  /**
   * Return the time-scale for the mvhd atom
   * @return the time-scale
   */
  public long getTimeScale() {
    return data.getUnsignedInt(TIMESCALE_OFFSET);
  }
  
  /**
   * Set the time-scale for the mvhd atom
   * @param ts the new time-scale
   */
  public void setTimeScale(long ts) {
    data.addUnsignedInt(TIMESCALE_OFFSET, ts);
  }
  
  /**
   * Return the duration for the mvhd atom.  The duration is the maximum of the 
   * duration of the tracks.
   * @return the duration
   */
  public long getDuration() {
    return data.getUnsignedInt(DURATION_OFFSET);
  }
  
  /**
   * Set the duration for the movie header atom.  The duration is the length of the duration of its
   * longest track.
   * @param duration the duration for the movie header
   */
  public void setDuration(long duration) {
    data.addUnsignedInt(DURATION_OFFSET, duration);
  }
  
  /**
   * Return the normalized time-scale for the movie.
   * @return the movie's normalized time-scale.
   */
  public long getDurationNormalized() {
    return getDuration() / getTimeScale();
  }
  
  /**
   * Cut the movie header.  The only change needed is the duration, which is easiest done
   * after the entire moov atom has been updated.
   * @return a copy of the movie header
   */
  public MvhdAtom cut() {
    MvhdAtom copy = new MvhdAtom(this);
    // set to zero, and remember to update it by the caller.
    copy.setDuration(0);
    return copy;
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}