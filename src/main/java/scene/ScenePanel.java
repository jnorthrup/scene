package scene;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * User: jim
 * Time: 7:14:43 AM
 */
public class ScenePanel extends JPanel {
    static HashMap<URL, ImageIcon> images = new HashMap<URL, ImageIcon>();
    static WeakHashMap<JPanel, java.util.List<Pair<Point, URL>>> panes = new WeakHashMap<JPanel, java.util.List<Pair<Point, URL>>>();


    public ScenePanel() {
        panes.put(this, new ArrayList<Pair<Point, URL>>());
        this.setDropTarget(new DropTarget(this, DnDConstants.ACTION_LINK, new ImageUrlDropTargetListener(this), true));
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        final java.util.List<Pair<Point, URL>> pairs = panes.get(this);

        for (final Pair<Point, URL> pair : pairs) {

            final Point p = pair.first;


            ImageIcon icon = images.get(pair.second);
            if (icon == null)
                images.put(pair.second, icon = new ImageIcon(pair.second));

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

    /**
     * User: jim
     * Date: May 13, 2009
     * Time: 7:04:11 PM
     */
    public static class ImageUrlDropTargetListener implements DropTargetListener {
        private final DataFlavor urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL", "URL");
//        private final JPanel component;
        private static SceneLayoutApp instance;

        public ImageUrlDropTargetListener(final Component component) {
//            this.component = (JPanel) component;
        }

        /**
         * The canvas only supports Files and URL's
         *
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        public void dragEnter
                (DropTargetDragEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || event.isDataFlavorSupported(urlFlavor)) {
                return;
            }
            event.rejectDrag();
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
         */
        public void dragExit(DropTargetEvent event) {
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
         */
        public void dragOver
                (DropTargetDragEvent event) {
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
         */
        public void dropActionChanged
                (DropTargetDragEvent event) {
        }

        /**
         * The file or URL has been dropped.
         *
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        public void drop(final DropTargetDropEvent event) {

            final Component component= (Component) event.getSource()       ;
            final Point dragSpot = event.getLocation();

            final int x1 = dragSpot.x;
            final int y1 = dragSpot.y;

            // important to first try urlFlavor

            URL url = null;


            if (event.isDataFlavorSupported(urlFlavor)) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_LINK);
                    Transferable trans = event.getTransferable();
                    url = (URL) (trans.getTransferData(urlFlavor));
                    System.err.println(url.toExternalForm());
                    event.dropComplete(true);
                } catch (Exception e) {
                    event.dropComplete(false);
                }
            } else if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_LINK);
                    Transferable trans = event.getTransferable();
                    java.util.List<File> list = (java.util.List<File>) (trans.getTransferData(DataFlavor.javaFileListFlavor));

                    for (File o1 : list) {
                        url = o1.toURI().toURL();
                        System.err.println(o1.getAbsolutePath());
                    }
                    event.dropComplete(true);


                } catch (Exception e) {
                    event.dropComplete(false);
                }

            }
            if (url != null) {

                final URL url1 = url;
                SceneLayoutApp.TIMER.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        URLConnection urlConnection = null;
                        try {
                            urlConnection = url1.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();  //TODO: Verify for a purpose
                        }
                        assert urlConnection != null;
                        System.err.println(urlConnection.getContentType());

                    }
                }, 250);

                final ImageIcon icon = new ImageIcon(url);
                images.put(url, icon);
                panes.get(component).add(new Pair<Point, URL>(dragSpot, url));

                final java.util.List<Pair<Point, URL>> pairs = panes.get(component);

                final Object[] objects = {component.getMaximumSize(), pairs,};
                final String s = SceneLayoutApp.XSTREAM.toXML(objects);
                SceneLayoutApp.permText.setText(s);

                final TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        
                        component.paint(component.getGraphics());
                    }
                };
                SceneLayoutApp.TIMER.schedule(timerTask, 250);

            }
        }
    }
}
/*
<object-array>
  <java.awt.Dimension>
    <width>1110</width>
    <height>720</height>
  </java.awt.Dimension>
  <list>
    <scene.Pair>
      <first class="java.awt.Point">
        <x>-42</x>
        <y>0</y>
      </first>
      <second class="url">http://6692202750809161650-a-hideftvads-com-s-sites.googlegroups.com/a/hideftvads.com/i1/Home/androidportrait.png</second>
    </scene.Pair>
    <scene.Pair>
      <first class="java.awt.Point">
        <x>378</x>
        <y>-42</y>
      </first>
      <second class="url">http://sites.google.com/a/hideftvads.com/i1/Home/androidlandscape.png?attredirects=0</second>
    </scene.Pair>
  </list>
</object-array>*/