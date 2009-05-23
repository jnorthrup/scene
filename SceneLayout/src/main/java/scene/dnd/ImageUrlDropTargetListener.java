package scene.dnd;

import scene.SceneLayoutApp;
import static scene.SceneLayoutApp.XSTREAM;
import static scene.SceneLayoutApp.permText;
import scene.ScenePanel;
import static scene.ScenePanel.panes;
import scene.alg.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * User: jim
 * Date: May 13, 2009
 * Time: 7:04:11 PM
 */
public class ImageUrlDropTargetListener implements DropTargetListener {
    private static final Charset UTF16 = Charset.forName("UTF16");

    /**
     * The canvas only supports Files and URL's
     *
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragEnter(DropTargetDragEvent event) {
        for (DataFlavor flavor : ScenePanel.FLAVORS) {
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


        ArrayList<URL> res = new ArrayList<URL>(1);
        Transferable transferable = event.getTransferable();

        for (DataFlavor flavor : ScenePanel.FLAVORS) {
            if (transferable.isDataFlavorSupported(flavor)) {
                String str;
                if (flavor.getRepresentationClass().isAssignableFrom(List.class)) {
                    try {
                        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        final List data = (List) transferable.getTransferData(flavor);
                        for (final Object o : data) {
                            final URL u;
                            if (o instanceof File) {

                                u = ((((File) o).toURI().toURL()));
                            } else {

                                str = String.valueOf(o);
                                u = URI.create(str).toURL();
                            }
                            res.add(u);

                        }
                    } catch (UnsupportedFlavorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                        Object data = transferable.getTransferData(flavor);
                        URL u = null;
                        if (data instanceof ByteBuffer) {
                            ByteBuffer buffer = (ByteBuffer) data;
                            /* if (buffer.get(0) == 0 || buffer.get(1) == 0) {
                               str = buffer.asCharBuffer().toString();
                           } else*/
                            CharBuffer charBuffer = buffer.order(ByteOrder.nativeOrder()).asCharBuffer();

                            char c;//= charBuffer.get();
                            while (charBuffer.hasRemaining() && !Character.isWhitespace(c = charBuffer.get()) && c > 1)                                ;
                            str = charBuffer.limit(charBuffer.position() - 1).position(0).toString();
                            u = URI.create(str).toURL();
                        } else if (data instanceof File)
                            u = ((File) data).toURI().toURL();
                        else
                            u = URI.create(String.valueOf(data)).toURL();

                        (res/* = new ArrayList<URL>(1)*/).add(u);
                    } catch (UnsupportedFlavorException e) {
                        e.printStackTrace();  //TODO: verify for a purpose
                    } catch (IOException e) {
                        e.printStackTrace();  //TODO: verify for a purpose
                    }
                }
                System.err.println("dump: " + String.valueOf(res));


                if (res != null && !res.isEmpty()) {
                    final URL url;
                    url = res.iterator().next();

                    panes.get(component).add(new Pair<Point, ArrayList<URL>>(dragSpot, res));

                    SceneLayoutApp.TIMER.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            URLConnection urlConnection = null;
                            try {
                                urlConnection = url.openConnection();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            assert urlConnection != null;
                            System.err.println(urlConnection.getContentType());

                        }
                    }, 250);

                    ImageIcon icon = null;
                    icon = new ImageIcon(url);

                    ScenePanel.images.put(url, icon);

                    final java.util.List<Pair<Point, ArrayList<URL>>> pairs = panes.get(component);

                    final Object[] objects = {component.getMaximumSize(), pairs,};
                    final String s = XSTREAM.toXML(objects);
                    permText.setText(s);

                    final TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {

                            component.paint(component.getGraphics());
                        }
                    };

                    SceneLayoutApp.TIMER.schedule(timerTask, 250);
                    return;
                }
                return;
            }
        }
    }
}
