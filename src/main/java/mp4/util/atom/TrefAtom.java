package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;

/**
 * A track reference atom.
 */
public class TrefAtom extends ContainerAtom {
  // the track reference type atom
  private ITrefTypeAtom trefType;
  
  /**
   * Construct an empty tref atom
   */
  public TrefAtom() {
    super(new byte[]{'t','r','e','f'});
  }
  
  /**
   * Copy constructor for tref atom. Performs a deep copy.
   * @param old the version to copy
   */
  public TrefAtom(TrefAtom old) {
    super(old);
    trefType = old.getTrefType().copy();
  }
  
  /**
   * Return the tref type
   * @return the tref type
   */
  public ITrefTypeAtom getTrefType() {
    return trefType;
  }
  
  @Override
  public void addChild(Atom child) {
    if (child instanceof ITrefTypeAtom) {
      trefType = (ITrefTypeAtom) child;
    }
    else {
      throw new AtomError("Can't add " + child + " to tref");
    }
  }

  @Override
  protected void recomputeSize() {
    setSize(ATOM_HEADER_SIZE + trefType.size());
  }

  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }

  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    trefType.writeData(out);
  }

  /**
   * Cut the tref atom.  This just creates a new copy.
   * @return a new tref atom
   */
  public TrefAtom cut() {
    return new TrefAtom(this);
  }
}
