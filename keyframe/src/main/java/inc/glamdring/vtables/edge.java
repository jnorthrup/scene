package inc.glamdring.vtables;

/**
 * edge class.   midpoint between 2 casts
 * <p/>
 * type,edge, delta, coersion points, etc.
 * <p/>
 * User: jim
 * Date: Sep 18, 2008
 * Time: 6:05:14 AM
 */
public interface edge<referent, pointed> extends _proto<referent> {

    /**
     * left type node with induction
     *
     * @param edge copy ctor/factory proto
     * @return shift left
     */
    referent left(edge<referent, pointed> edge);

    /**
     * right type node with induction
     *
     * @param edge copy ctor/factory proto
     * @return right shift
     */
    pointed right(edge<referent, pointed> edge);

    /**
     * binds two types
     *
     * @param referent
     * @param pointed
     * @return fused arc
     */
    edge<referent, pointed> midpoint(referent referent, pointed pointed);

}
/**
 *
 public interface midpoint<referent, pointed> extends _proto<referent> { referent referent(midpoint<referent, pointed> midpoint); pointed pointed(midpoint<referent, pointed> midpoint); midpoint<referent, pointed> midpoint(referent referent, pointed pointed);}*/