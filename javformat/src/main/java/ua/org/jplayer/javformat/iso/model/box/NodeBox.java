package ua.org.jplayer.javformat.iso.model.box;

import ua.org.jplayer.javformat.toolkit.WindowInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A node box
 * <p/>
 * A box containing children, no data
 *
 * @author Stanislav Vitvitskiy
 */
public class NodeBox extends Box {
    protected Collection boxes;

    public NodeBox(Atom atom) {
        super(atom);
        this.boxes = new LinkedList();
    }

    public void parse(InputStream dis) throws IOException {
        while (true) {
            dis.mark(8);
            Atom childAtom = Atom.read(dis);
            if (childAtom == null) {
                break;
            }

            if (!childAtom.getType().getParents().contains(atom.getType())) {
                dis.reset();
                break;
            }

            AtomType atomType = childAtom.getType();

            Class claz = atomType.getClaz();
            Box box;
            try {
                if (claz == LeafBox.class) {
                    box = new LeafBox(childAtom);
                } else if (claz == NodeBox.class) {
                    box = new NodeBox(childAtom);
                } else {
                    box = (Box) claz.newInstance();
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Could not instanitiate box claz", e);
            }
            WindowInputStream window = new WindowInputStream(dis,
                    childAtom.getBodySize());
            box.parse(window);
            window.skipRemaining();
            addBox(box);
        }
    }

    public void serialize(OutputStream os) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator it = boxes.iterator();
        while (it.hasNext()) {
            Box box = (Box) it.next();
            box.serialize(baos);
        }
        byte[] bytes = baos.toByteArray();
        atom.setBodySize(bytes.length);
        atom.serialize(os);
        os.write(bytes);
    }

    public Collection getBoxes() {
        return boxes;
    }

    public void addBox(Box box) {
        boxes.add(box);
    }
}