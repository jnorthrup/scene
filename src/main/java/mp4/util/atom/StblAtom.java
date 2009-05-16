/**
 * 
 */
package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;

/**
 * The sample table container atom contains the atoms with the information
 * for converting from media time to sample number to sample location.
 * The atoms within the sample table tell the handler how to parse and process
 * the samples in media.
 */
public class StblAtom extends ContainerAtom {
  // sample description atom
  private StsdAtom stsd;
  // time-to-sample atom
  private SttsAtom stts;
  // sample size atom
  private StszAtom stsz;
  // sample-to-chunk atom
  private StscAtom stsc;
  // chunk offset atom
  private StcoAtom stco;
  // (composition) time-to-sample atom 
  private CttsAtom ctts;
  // sync sample atom
  private StssAtom stss;
  // TODO: shadow sync atom
  
  /**
   * Constructor for the sample table atom
   */
  public StblAtom() {
    super(new byte[]{'s','t','b','l'});
  }
  
  /**
   * A copy constructor for the stbl atom
   * @param old the stbl atom to copy
   */
  public StblAtom(StblAtom old) {
    super(old);
    stsd = new StsdAtom(old.stsd);
    stts = new SttsAtom(old.stts);
    stsz = new StszAtom(old.stsz);
    stsc = new StscAtom(old.stsc);
    stco = new StcoAtom(old.stco);
    if (old.ctts != null) {
      ctts = new CttsAtom(old.ctts);
    }
    stss = new StssAtom(old.stss);
  }
  
  public StsdAtom getStsd() {
    return stsd;
  }
  public SttsAtom getStts() {
    return stts;
  }
  public StszAtom getStsz() {
    return stsz;
  }
  public StscAtom getStsc() {
    return stsc;
  }
  public StcoAtom getStco() {
    return stco;
  }
  public CttsAtom getCtts() {
    return ctts;
  }
  public StssAtom getStss() {
    return stss;
  }
  
  @Override
  public void addChild(Atom child) {
    if (child instanceof StsdAtom) {
      stsd = (StsdAtom) child;
    }
    else if (child instanceof SttsAtom) {
      stts = (SttsAtom) child;
    }
    else if (child instanceof StszAtom) {
      stsz = (StszAtom) child;
    }
    else if (child instanceof StscAtom) {
      stsc = (StscAtom) child;
    }
    else if (child instanceof StcoAtom) {
      stco = (StcoAtom) child;
    }
    else if (child instanceof CttsAtom) {
      ctts = (CttsAtom) child;
    }
    else if (child instanceof StssAtom) {
      stss = (StssAtom) child;
    }
    else {
      throw new AtomError("Can't add " + child + " to stbl");
    }
  }
  
  /**
   * Compute the size for the stbl container atom.
   */
  protected void recomputeSize() {
    long newSize = stsd.size() + stts.size() + stsz.size() + stsc.size() + stco.size();
    if (ctts != null) {
      newSize += ctts.size();
    }
    if (stss != null) {
      newSize += stss.size();
    }
    setSize(ATOM_HEADER_SIZE + newSize);
  }
  
  /**
   * Cut the sample table atom at the specified point.
   * @param time the time normalized to the track time
   * @return the new stbl atom that has been cut
   */
  public StblAtom cut(long time) {
    long sampleNum = getStts().timeToSample(time);
    long keyFrame = sampleNum;
    if (getStss() != null) {
      keyFrame = getStss().getKeyFrame(sampleNum);
    }
    System.out.println("\tDBG: sampleNum " + sampleNum + " sync frame " + keyFrame);
    
    long chunk = getStsc().sampleToChunk(keyFrame);
    System.out.println("\tDBG: chunk " + chunk);
    
    long offset = getStco().getChunkOffset(chunk);
    System.out.println("\tDBG: offset " + offset);
    
    StblAtom cutStbl = new StblAtom();
    cutStbl.stsd = stsd.cut();
    cutStbl.stts = stts.cut(keyFrame);
    cutStbl.stsz = stsz.cut(keyFrame);
    cutStbl.stsc = stsc.cut(keyFrame);
    cutStbl.stco = stco.cut(chunk);
    if (ctts != null) {
      cutStbl.ctts = ctts.cut(keyFrame);
    }
    if (stss != null) {
      cutStbl.stss = stss.cut(keyFrame);
    }
    // fix the size of the container atom
    cutStbl.recomputeSize();
    return cutStbl;
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }

  /**
   * Write the stbl atom data to the specified output
   * @param out where the data goes
   * @throws IOException if there is an error writing the data
   */
  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    stsd.writeData(out);
    stts.writeData(out);
    stsz.writeData(out);
    stsc.writeData(out);
    stco.writeData(out);
    if (ctts != null) {
      ctts.writeData(out);
    }
    if (stss != null) {
      stss.writeData(out);
    }
  }
}