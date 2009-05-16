/**
 * 
 */
package mp4.util.atom;

public abstract class AtomVisitor {
  public abstract void visit(FtypAtom atom) throws AtomException;
  public abstract void visit(FreeAtom atom) throws AtomException;
  public abstract void visit(MoovAtom atom) throws AtomException;
  public abstract void visit(CdscAtom atom) throws AtomException;
  public abstract void visit(ChapAtom atom) throws AtomException;
  public abstract void visit(Co64Atom atom) throws AtomException;
  public abstract void visit(CttsAtom atom) throws AtomException;
  public abstract void visit(EdtsAtom atom) throws AtomException;
  public abstract void visit(ElstAtom atom) throws AtomException;
  public abstract void visit(DinfAtom atom) throws AtomException;
  public abstract void visit(DrefAtom atom) throws AtomException;
  public abstract void visit(GmhdAtom atom) throws AtomException;
  public abstract void visit(HdlrAtom atom) throws AtomException;
  public abstract void visit(HintAtom atom) throws AtomException;
  public abstract void visit(IodsAtom atom) throws AtomException;
  public abstract void visit(MdatAtom atom) throws AtomException;
  public abstract void visit(MdiaAtom atom) throws AtomException;
  public abstract void visit(MdhdAtom atom) throws AtomException;
  public abstract void visit(MinfAtom atom) throws AtomException;
  public abstract void visit(MvhdAtom atom) throws AtomException;
  public abstract void visit(SmhdAtom atom) throws AtomException;
  public abstract void visit(StblAtom atom) throws AtomException;
  public abstract void visit(StcoAtom atom) throws AtomException;
  public abstract void visit(StscAtom atom) throws AtomException;
  public abstract void visit(StsdAtom atom) throws AtomException;
  public abstract void visit(StssAtom atom) throws AtomException;
  public abstract void visit(StszAtom atom) throws AtomException;
  public abstract void visit(SttsAtom atom) throws AtomException;
  public abstract void visit(TrakAtom atom) throws AtomException;
  public abstract void visit(TkhdAtom atom) throws AtomException;
  public abstract void visit(TrefAtom atom) throws AtomException;
  public abstract void visit(UdtaAtom atom) throws AtomException;
  public abstract void visit(VmhdAtom atom) throws AtomException;
}