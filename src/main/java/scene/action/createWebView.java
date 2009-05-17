package scene.action;

import scene.SceneLayoutApp;
import scene.ScenePanel;

import javax.swing.*;
import java.awt.*;
import static java.awt.BorderLayout.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
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
 * Date: May 17, 2009
 * Time: 12:51:29 AM
 */
public class createWebView extends AbstractAction {
    //    private JTextComponent editor = new JEditorPane();
    private JPanel panel;
    private JInternalFrame jInternalFrame;
    private JTextField urlText;
    private JEditorPane jEditorPane;
    private JLabel qrCode;
    private JScrollPane editorScrollPane;

    public createWebView() {
        super("Web");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        jInternalFrame = new JInternalFrame("CreateWeb");
        jInternalFrame.setMaximizable(true);
        jInternalFrame.setClosable(true);
        jInternalFrame.setIconifiable(true);
        jInternalFrame.setResizable(true);
        jInternalFrame.setSize(400, 400);
        panel = new JPanel(new BorderLayout());
        jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        urlText = new JTextField();
        final JToolBar bar = new JToolBar();
        bar.add(urlText);
        jInternalFrame.setContentPane(panel);
        panel.add(bar, NORTH);
        editorScrollPane = new JScrollPane(jEditorPane);
        qrCode = new JLabel();
        panel.add(qrCode, WEST);
        panel.add(editorScrollPane, CENTER);

        urlText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateEditor(new URL(urlText.getText()));
                } catch (Exception ignored) {
                }
            }
        });
        SceneLayoutApp.desktopPane.add(jInternalFrame);

        jInternalFrame.pack();

        jInternalFrame.show();

        new DropTarget(jInternalFrame, DnDConstants.ACTION_LINK, new
                DropTargetListener() {
                    private final Charset UTF16 = Charset.forName("UTF16");

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

                        final JComponent component = (JComponent) dt.getComponent();
                        final Point dragSpot = event.getLocation();

                        final int x1 = dragSpot.x;
                        final int y1 = dragSpot.y;

                        // important to first try uriListFlavor


                        Collection<URL> res = null;
                        final Transferable transferable = event.getTransferable();

                        for (final DataFlavor flavor : ScenePanel.FLAVORS) {
                            if (transferable.isDataFlavorSupported(flavor)) {
                                String str = null;
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
                                            while (charBuffer.hasRemaining() && !java.lang.Character.isWhitespace(c = charBuffer.get()) && c > 1)
                                                ;
                                            str = charBuffer.limit(charBuffer.position() - 1).position(0).toString();

                                        } else

                                            str = String.valueOf(data);
                                        (res = new ArrayList<URL>(1)).add(new URL(str));
                                    } catch (Exception e) {
                                        e.printStackTrace();  //TODO: verify for a purpose
                                    }
                                } else {
                                    try {
                                        ArrayList<URL> ar = new ArrayList<URL>();
                                        for (Object o : (Iterable<?>) transferable.getTransferData(flavor)) {
                                            str = String.valueOf(o);
                                            final URL u = new URL(str);
                                            ar.add(u);
                                            res = ar;
                                        }
                                    } catch (Exception ignored) {
                                    }
                                }
                                System.err.println("dump: " + String.valueOf(res));

                                if (res != null) {
                                    URL url = res.iterator().next();
                                    jInternalFrame.setTitle(url.toExternalForm());
                                    updateEditor(url);


                                }
                                return;
                            }
                        }
                    }
                }, true);
    }

    private void updateEditor(URL url) {
        if (url != null) {
            try {
                jEditorPane.setPage(url);
            } catch (IOException e) {
                System.err.println("Attempted to read a bad URL: " + url.toExternalForm());
            }
        } else {
            System.err.println("Couldn't find file: " + url.toExternalForm());
        }
        urlText.setText(url.toExternalForm());
        try {
            final ImageIcon icon;
            final Dimension viewSize = editorScrollPane.getViewport().getViewSize();
            final int v = (int) viewSize.getHeight();
            final URL url1 = new URL("http://chart.apis.google.com/chart?cht=qr&chs=" + v + "&chl=" + url.toExternalForm());
            icon = new ImageIcon(url1);

//            jEditorPane.setPreferredSize(viewSize);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    qrCode.setIcon(icon);
                    qrCode.invalidate();
                    qrCode.repaint();
                }
            });
        } catch (MalformedURLException ignored) {
        }
    }

}
