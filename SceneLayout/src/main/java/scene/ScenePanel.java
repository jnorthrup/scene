package scene;

import scene.alg.Pair;
import scene.dnd.ImageUrlDropTargetListener;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

/**
 * User: jim
 * Time: 7:14:43 AM
 */
public class ScenePanel extends JPanel {
    public static HashMap<URL, ImageIcon> images = new HashMap<URL, ImageIcon>();
    public static WeakHashMap<JPanel, java.util.List<Pair<Point, ArrayList<URL>>>> panes = new WeakHashMap<JPanel, List<Pair<Point, ArrayList<URL>>>>();
    public final static DataFlavor[] FLAVORS;

    static {


        DataFlavor flavor = new DataFlavor("application/x-java-url;class=java.net.URL", "URL");
        DataFlavor flavor1 = new DataFlavor("text/x-uri-list; class=java.util.List", "URI List");
        DataFlavor flavor2 = new DataFlavor("text/x-java-file-list; class=java.util.List", "Java FileList");
        DataFlavor flavor3 = new DataFlavor("text/uri-list; class=java.util.List", "URI List");
        DataFlavor flavor4 = new DataFlavor("text/x-moz-url; class=java.nio.ByteBuffer", "Mozilla URL");
        FLAVORS = new DataFlavor[]{
                flavor,
                flavor1,
                flavor2,
                flavor3,

                flavor4,

//       flavor4 new DataFlavor("image/x-java-image; class=java.util.List", "URI List"),
                /*      new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                        new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                        new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                */
        };
    }

    {
        panes.put(this, new ArrayList<Pair<Point, ArrayList<URL>>>());

        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new ImageUrlDropTargetListener(), true));
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        final List<Pair<Point, ArrayList<URL>>> pairs = panes.get(this);

        for (final Pair<Point, ArrayList<URL>> pair : pairs) {

            final Point p = pair.$1();


            ImageIcon icon = images.get(pair.$2());
            if (icon == null) {
                URL uri = pair.$2().iterator().next();
                images.put(uri, icon = new ImageIcon(uri));
            }
            final int loadStatus = icon.getImageLoadStatus();
            if (loadStatus == MediaTracker.COMPLETE) {
                g.drawImage(icon.getImage(), p.x, p.y, this);
            } else {
                if (loadStatus == MediaTracker.LOADING) {
                    g.setColor(Color.red);
                    g.drawRoundRect(p.x, p.y, icon.getIconWidth(), icon.getIconHeight(), 3, 3);
                } else {
                    return;
                }
            }
        }
    }

}

