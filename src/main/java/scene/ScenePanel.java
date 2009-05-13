package scene;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.*;

/**
 * User: jim
 * Date: May 13, 2009
 * Time: 7:14:43 AM
 */
class ScenePanel extends JPanel {
    private App app;
    static HashMap<URL, ImageIcon> images = new HashMap<URL, ImageIcon>();
    static WeakHashMap<JPanel, java.util.List<Pair<Point, URL>>> panes = new WeakHashMap<JPanel, java.util.List<Pair<Point, URL>>>();


    public ScenePanel(App app) {
        this.app = app;
    }

    {
        panes.put(this, new ArrayList<Pair<Point, URL>>());

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        final java.util.List<Pair<Point, URL>> pairs = panes.get(this);

        for (Pair<Point, URL> pair : pairs) {

            final Point p = pair.$1;


            final ImageIcon icon = images.get(pair.$2);
            final int loadStatus = icon.getImageLoadStatus();
            if (loadStatus == MediaTracker.COMPLETE) {
                g.drawImage(icon.getImage(), p.x, p.y, this);

            } else {
                if
                        (loadStatus == MediaTracker.LOADING) {
                    final Graphics2D g2 = (Graphics2D) (g);
                    g2.setPaint(Color.red);
                    g2.drawRoundRect(p.x, p.y, icon.getIconWidth(), icon.getIconHeight(), 3, 3);

                } else return;

            }
        }
    }
}
