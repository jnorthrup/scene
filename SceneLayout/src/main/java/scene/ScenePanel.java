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
    public   HashMap<URL, ImageIcon> images = new HashMap<URL, ImageIcon>();
    public static WeakHashMap<JPanel, java.util.List<Pair<Point, ArrayList<URL>>>> panes = new WeakHashMap<JPanel, List<Pair<Point, ArrayList<URL>>>>();
    public final static DataFlavor[] FLAVORS = new DataFlavor[]{
            DataFlavor.javaFileListFlavor, 
            new DataFlavor("application/x-java-url;class=java.net.URL", "URL"),
            new DataFlavor("text/x-uri-list; class=java.util.List", "URI List"),
//            new DataFlavor("text/x-java-file-list; class=java.util.List", "Java FileList"),
            new DataFlavor("text/uri-list; class=java.util.List", "URI List"),

            new DataFlavor("text/x-moz-url; class=java.nio.ByteBuffer", "Mozilla URL"),
            DataFlavor.imageFlavor,
//       flavor4 new DataFlavor("image/x-java-image; class=java.util.List", "URI List"),
            /*      new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                    new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                    new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
            */
    };

    static {


    }

    {
        panes.put(this, new ArrayList<Pair<Point, ArrayList<URL>>>());

        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new ImageUrlDropTargetListener(), true));
  this.setToolTipText("<html><h1>Scene Layout</h1><p>Drop image files and browser images onto this canvas.");  }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        final List<Pair<Point, ArrayList<URL>>> pairs = panes.get(this);

        for (final Pair<Point, ArrayList<URL>> pair : pairs) {

            final Point p = pair.$1();


            ImageIcon icon = images.get(pair.$2());
            if (icon == null) {
                final ArrayList<URL> urlArrayList = pair.$2();
                URL uri = urlArrayList.iterator().next();
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

