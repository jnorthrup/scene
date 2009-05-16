/**
 * 
 */
package mp4.util.atom;

/**
 * The header information for a single track.
 * 
 * In the absence of an edit list, the presentaiton of a track starts at the
 * beginning of the overall presentation.
 */
public class TkhdAtom extends LeafAtom {
  private static final int CREATION_TIME_OFFSET = 4;
  private static final int MODIFICATION_TIME_OFFSET = 8;
  private static final int TRACK_ID_OFFSET = 12;
  private static final int RESERVED_1_OFFSET = 16;
  private static final int DURATION_OFFSET = 20;
  private static final int RESERVED_2_OFFSET = 24;
  private static final int LAYER_OFFSET = 32;
  private static final int ALTERNATE_GROUP_OFFSET = 34;
  private static final int VOLUME_OFFSET = 36;
  private static final int RESERVED_3_OFFSET = 38;
  private static final int MATRIX_OFFSET = 40;
  private static final int WIDTH_OFFSET = 76;
  private static final int HEIGHT_OFFSET = 80;
  
  public TkhdAtom() {
    super(new byte[]{'t','k','h','d'});
  }
  
  /**
   * Copy constructor
   * @param old the version to copy
   */
  public TkhdAtom(TkhdAtom old) {
    super(old);
  }
  
  /**
   * Get the track id.
   * @return the track id
   */
  public long getTrackId() {
    return data.getUnsignedInt(TRACK_ID_OFFSET);
  }
  
  /**
   * Set the track id
   * @param id the new track id
   */
  public void setTrackId(long id) {
    data.addUnsignedInt(TRACK_ID_OFFSET, id);
  }
    
  /**
   * Return the duration of the track.  The track duration is the sum of the 
   * sample durations (in the absence of an edit list).
   * @return the track's duration.
   */
  public long getDuration() {
    return data.getUnsignedInt(DURATION_OFFSET);
  }
  
  /**
   * Set the duration for the track.  The track duration is the sum of the sample
   * durations (in the absence of an edit list).  The timescale is from the movie header atom.
   * @param duration the track's duration in movie's timescale
   */
  public void setDuration(long duration) {
    data.addUnsignedInt(DURATION_OFFSET, duration);
  }
  
  /**
   * Cut the tkhd atom at the specified time.  The duration for the track header is set
   * to zero initially.  It must be filled in later.
   */
  public TkhdAtom cut() {
    TkhdAtom cutTkhd = new TkhdAtom(this);
    cutTkhd.setDuration(0);
    return cutTkhd;
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }

}