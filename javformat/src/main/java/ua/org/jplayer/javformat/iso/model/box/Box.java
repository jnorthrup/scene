package ua.org.jplayer.javformat.iso.model.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * An MP4 file struncture (box).
 *
 * @author Stanislav Vitvitskiy
 */
public abstract class Box {
    protected Atom atom;

    public Box(Atom atom) {
        this.atom = atom;
    }

    public Atom getAtom() {
        return atom;
    }

    public abstract void serialize(OutputStream dos) throws IOException;

    public abstract void parse(InputStream is) throws IOException;

    public static Box navigate(Box box, AtomType[] atomTypes) {
        ArrayList<Box> result = new ArrayList<Box>();
        ArrayList<AtomType> tlist = new ArrayList<AtomType>();
        tlist.addAll(Arrays.asList(atomTypes));
        navigate(box, tlist, result);
        Iterator<Box> it = result.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public static Collection<? extends Box> navigateAll(Box box, AtomType... atomTypes) {
        final Collection<? extends Box> result = new ArrayList<Box>();
        final Collection<AtomType> tlist = new ArrayList<AtomType>();
        tlist.addAll(Arrays.asList(atomTypes));
        navigate(box, tlist, (Collection<Box>) result);
        return result;
    }

    private static void navigate(Box box, Collection<AtomType> types, Collection<Box> result) {
        final Iterator<AtomType> it = types.iterator();
        if (it.hasNext()) {
            AtomType atomType = it.next();
            ArrayList<AtomType> tail = new ArrayList<AtomType>();
            while (it.hasNext()) {
                tail.add(it.next());
            }

            if (box instanceof NodeBox) {
                NodeBox nb = (NodeBox) box;
                Iterator it1 = nb.getBoxes().iterator();
                while (it1.hasNext()) {
                    Box box2 = (Box) it1.next();
                    if (box2.atom.getType() == atomType) {
                        navigate(box2, tail, result);
                    }
                }
            }
        } else {
            result.add(box);
        }
    }
}
