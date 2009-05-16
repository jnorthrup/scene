package scene;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import static java.lang.Character.isWhitespace;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

/**
 * User: jim
 * Time: 7:14:43 AM
 */
public class ScenePanel extends JPanel {
    static HashMap<URL, ImageIcon> images = new HashMap<URL, ImageIcon>();
    static WeakHashMap<JPanel, java.util.List<Pair<Point, ArrayList<URL>>>> panes = new WeakHashMap<JPanel, List<Pair<Point, ArrayList<URL>>>>();
    static DataFlavor[] FLAVORS;


    public ScenePanel() {


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

    /**
     * User: jim
     * Date: May 13, 2009
     * Time: 7:04:11 PM
     */
    public static class ImageUrlDropTargetListener implements DropTargetListener {
        private static final Charset UTF16 = Charset.forName("UTF16");

        /**
         * The canvas only supports Files and URL's
         *
         * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
         */
        public void dragEnter(DropTargetDragEvent event) {
            for (DataFlavor flavor : FLAVORS) {
                if (event.isDataFlavorSupported(flavor)) {
                    System.err.println("supported: " + flavor.toString());
                    return;
                }
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
        public void dragOver(DropTargetDragEvent event) {
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
         */
        public void dropActionChanged(DropTargetDragEvent event) {
        }

        /**
         * The file or URL has been dropped.
         *
         * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
         */
        public void drop(final DropTargetDropEvent event) {
            DropTarget dt = (DropTarget) event.getSource();

            final ScenePanel component = (ScenePanel) dt.getComponent();
            final Point dragSpot = event.getLocation();

            final int x1 = dragSpot.x;
            final int y1 = dragSpot.y;

            // important to first try uriListFlavor


            ArrayList<URL> res = null;
            Transferable transferable = event.getTransferable();

            for (DataFlavor flavor : FLAVORS) {
                if (transferable.isDataFlavorSupported(flavor)) {
                    String str;
                    if (!flavor.getRepresentationClass().isAssignableFrom(Iterable.class)) {
                        try {
                            event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                            Object data = transferable.getTransferData(flavor);

                            if (data instanceof ByteBuffer) {
                                ByteBuffer buffer = (ByteBuffer) data;
                                /* if (buffer.get(0) == 0 || buffer.get(1) == 0) {
                                   str = buffer.asCharBuffer().toString();
                               } else*/
                                CharBuffer charBuffer = buffer.order(ByteOrder.nativeOrder()).asCharBuffer();

                                char c = charBuffer.get();
                                while (charBuffer.hasRemaining() && !isWhitespace(c = charBuffer.get()) && c > 1) ;
                                str = charBuffer.limit(charBuffer.position() - 1).position(0).toString();

                            } else

                                str = String.valueOf(data);
                            (res = new ArrayList<URL>(1)).add(new URL(str));
                        } catch (UnsupportedFlavorException e) {
                            e.printStackTrace();  //TODO: verify for a purpose
                        } catch (IOException e) {
                            e.printStackTrace();  //TODO: verify for a purpose
                        }
                    } else {
                        try {
                            ArrayList<URL> ar = new ArrayList<URL>();
                            for (Object o : (Iterable<?>) transferable.getTransferData(flavor)) {
                                str = String.valueOf(o);
                                URL u = new URL(str);
                                ar.add(u);
                                res = ar;
                            }
                        } catch (UnsupportedFlavorException e) {
                            e.printStackTrace();  //TODO: verify for a purpose
                        } catch (IOException e) {
                            e.printStackTrace();  //TODO: verify for a purpose
                        }
                    }
                    System.err.println("dump: " + String.valueOf(res));

                    if (res != null) {
                        final URL url;
                        url = res.iterator().next();

                        panes.get(component).add(new scene.Pair<Point, ArrayList<URL>>(dragSpot, res));

                        final ArrayList<URL> uriList = res;
                        SceneLayoutApp.TIMER.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                URLConnection urlConnection = null;
                                try {
                                    urlConnection = url.openConnection();

                                } catch (IOException e) {
                                    e.printStackTrace();  //TODO: Verify for a purpose
                                }
                                assert urlConnection != null;
                                System.err.println(urlConnection.getContentType());

                            }
                        }, 250);

                        ImageIcon icon = null;
                        icon = new ImageIcon(url);

                        images.put(url, icon);

                        final List<Pair<Point, ArrayList<URL>>> pairs = panes.get(component);

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
                    return;
                }
            }
        }
    }

    /**
     * User: jim
     * Date: May 14, 2009
     * Time: 1:00:28 AM
     */
    public static class CreateSceneWindow extends AbstractAction {


        public void actionPerformed(final ActionEvent actionEvent) {

            final JComboBox source = (JComboBox) actionEvent.getSource();

            final Object o = source.getSelectedItem();
            final String[] strings = o
                    .toString().split(":");

            final JInternalFrame vw = new JInternalFrame();

            final JPanel iView = new ScenePanel();
            iView.setLayout(null);
            vw.setContentPane(iView);

            final Integer w = Integer.decode(strings[0]);
            final Integer h = Integer.decode(strings[1]);

            vw.setResizable(false);


            SceneLayoutApp.desktopPane.add(vw);

            vw.show();
            vw.setClosable(true);
            vw.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

            final Dimension dimension = new Dimension(w, h);
            iView.setPreferredSize(dimension);
            iView.setMaximumSize(dimension);

            vw.pack();
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