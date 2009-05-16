/**
 * 
 */
package mp4.util.atom;


public abstract class ContainerAtom extends Atom {
  
  /**
   * Create a container atom
   * @param type the atom's type information
   */
  protected ContainerAtom(byte[] type) {
    super(type);
  }
  
  /**
   * Copy constructor
   * @param old the version to copy
   */
  protected ContainerAtom(ContainerAtom old) {
    super(old);
  }
  
  public boolean isContainer() {
    return true;
  }
  
  public abstract void addChild(Atom child);
  
  /**
   * Recompute the size of the container by summing the size of each
   * contained atom
   */
  protected abstract void recomputeSize();
}