/**
 * 
 */
package mp4.util.atom;

/**
 * The chunk offset table.
 */
public class StcoAtom extends LeafAtom {
  private static final int ENTRIES_OFFSET = 4;
  private static final int TABLE_OFFSET = 8;
  private static final int ENTRY_SIZE = 4;
  
  /**
   * Construct an empty atom
   */
  public StcoAtom() {
    super(new byte[]{'s','t','c','o'});
  }
  
  /**
   * Copy constructor.  Perform a deep copy.
   * @param old the version to copy
   */
  public StcoAtom(StcoAtom old) {
    super(old);
  }

  /**
   * Allocate space for the atom's data.
   */
  @Override
  public void allocateData(long numEntries) {
    long size = TABLE_OFFSET + (numEntries * ENTRY_SIZE);
    super.allocateData(size);
  }

  /**
   * Return the number of entries in the stco table
   * @return the number of entries
   */
  public long getNumEntries() {
    return data.getUnsignedInt(ENTRIES_OFFSET);
  }
  
  /**
   * Set the number of entries in the stco table
   * @param numEntries the number of entries
   */
  public void setNumEntries(long numEntries) {
    data.addUnsignedInt(ENTRIES_OFFSET, numEntries);
  }
  
  /**
   * Get the chunk offset for the specified chunk.  The chunk
   * values are 1 based, while the table is 0 based.
   * @param chunk the chunk number
   * @return the ofset for the chunk
   */
  public long getChunkOffset(long chunk) {
    if (chunk > Integer.MAX_VALUE) {
      return 0;
    }
    return data.getUnsignedInt(TABLE_OFFSET + ((int)(chunk - 1) * ENTRY_SIZE));
  }
  
  /**
   * Set the chunk offset for specified table index
   * @param index the table index number
   * @param chunk the chunk offset
   */
  public void setChunkOffset(int index, long chunk) {
    data.addUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE), chunk);
  }
  
  /**
   * Split the atom at the specified chunk and fill the byte stream with
   * the contents of the new atom.  Any entry prior to the specified 
   * chunk is discarded, and the new table is created with the entries
   * subsequent to the specified chunk.
   * @param bs the byte stream where the new data is added
   * @param chunkNum the chunk where the atom should be split 
   */
  public StcoAtom cut(long chunkNum) {    
    // create the new table
    StcoAtom cutStco = new StcoAtom();
    long numEntries = getNumEntries();
    cutStco.allocateData(numEntries - chunkNum + 1);
    cutStco.setNumEntries(numEntries - chunkNum + 1);
    int entryNumber = 0;
    for (long i = chunkNum; i <= numEntries; i++, entryNumber++) {
      cutStco.setChunkOffset(entryNumber, getChunkOffset(i));
    }
    return cutStco;
  }
  
  /**
   * Perform a fixup of the offsets in the stco atom.  Each existing values
   * changes by the specified amount.  The update must occur if any of the mp4 file
   * changes, e.g., when the mp4 file is cut.
   * @param delta the amount to update each offset
   */
  public void fixupOffsets(long delta) {
    for (int i = 0; i < getNumEntries(); i++) {
      setChunkOffset(i, getChunkOffset(i+1) + delta);
    }
  }

  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}