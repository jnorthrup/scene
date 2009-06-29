package scene.dnd;

import scene.ScenePanel;
import scene.action.WebAnimUrlTextAction;
import scene.anim.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:04:41 PM
 */
public class WebViewDropTargetListener implements DropTargetListener {

    private static final Charset UTF16 = Charset.forName("UTF16");
    public final WebAnimatorImpl frame;

    public WebViewDropTargetListener(WebAnimatorImpl
            frame) {
        this.frame = frame;
        this.frame.getUrlText().addActionListener(new WebAnimUrlTextAction(this));
    }

    /**
     * The canvas only supports Files and URL's
     *
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
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
    @Override
    public void dragExit(DropTargetEvent event) {
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }

    /**
     * The file or URL has been dropped.
     *
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    @Override
    public void drop(final DropTargetDropEvent event) {
        DropTarget dt = (DropTarget) event.getSource();

        JComponent component = (JComponent) dt.getComponent();
        Point dragSpot = event.getLocation();

        int x1 = dragSpot.x;
        int y1 = dragSpot.y;

        // important to first try uriListFlavor


        Collection<URL> res = null;
        Transferable transferable = event.getTransferable();

        for (DataFlavor flavor : ScenePanel.FLAVORS) {
            if (transferable.isDataFlavorSupported(flavor)) {
                String str = null;
                Class<?> representationClass = flavor.getRepresentationClass();
                if (representationClass.isAssignableFrom(Iterable.class)) {
                    try {
                        Collection<URL> ar = new ArrayList<URL>();
                        for (Object o : (Iterable<?>) transferable.getTransferData(flavor)) {
                            str = String.valueOf(o);
                            URL u = new URL(str);
                            ar.add(u);
                            res = ar;
                        }
                    } catch (Exception ignored) {
                    }
                } else {
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
                            while (charBuffer.hasRemaining() && !Character.isWhitespace(c = charBuffer.get()) && c > 1)
                                ;
                            str = charBuffer.limit(charBuffer.position() - 1).position(0).toString();

                        } else {
                            str = String.valueOf(data);
                        }
                        (res = new ArrayList<URL>(1)).add(new URL(str));
                    } catch (Exception e) {
                        e.printStackTrace();  //TODO: verify for a purpose
                    }
                }
                System.err.println("dump: " + String.valueOf(res));

                if (res != null) {
                    URL url = res.iterator().next();
                    frame.setTitle(url.toExternalForm());
                    frame.updateEditor(url);
                }
                return;
            }
        }
    }


    public WebAnimator getFrame() {
        return frame;
    }
}
