package scene.action;

import scene.SceneLayoutApp;
import scene.ScenePanel;
import scene.gif.AnimatedGifEncoder;

import javax.swing.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import java.awt.*;
import static java.awt.BorderLayout.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Future;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 17, 2009
 * Time: 12:51:29 AM
 */
public class createWebView extends AbstractAction {

    public createWebView() {
        super("Web");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        final JScrollPane jScrollPane;
        final JPanel outerPanel, panel;
        final JInternalFrame jInternalFrame;
        final JTextField urlText;
        final JEditorPane jEditorPane;
        final JLabel qrCode;
        jInternalFrame = new JInternalFrame("CreateWeb");
        jInternalFrame.setMaximizable(true);
        jInternalFrame.setClosable(true);
        jInternalFrame.setIconifiable(true);
        jInternalFrame.setResizable(true);
        jInternalFrame.setSize(400, 400);
        jInternalFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        panel = new JPanel(new BorderLayout());

        outerPanel = new JPanel(new BorderLayout());
        outerPanel.add(panel, CENTER);

        jEditorPane = new JEditorPane();
        jEditorPane.setEditable(false);
        urlText = new JTextField();
        JToolBar bar = new JToolBar();


        bar.add(urlText);
        jInternalFrame.setContentPane(outerPanel);
        outerPanel.add(bar, NORTH);
        jScrollPane = new JScrollPane(jEditorPane);
        qrCode = new JLabel();
        panel.add(qrCode, WEST);
        panel.add(jScrollPane, CENTER);

        SceneLayoutApp.desktopPane.add(jInternalFrame);

        jInternalFrame.pack();

        jInternalFrame.show();

        new DropTarget(jInternalFrame, DnDConstants.ACTION_LINK, new
                DropTargetListener() {
                    {
                        urlText.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    updateEditor(new URL(urlText.getText()));
                                } catch (Exception ignored) {
                                }
                            }
                        });
                    }

                    private final Charset UTF16 = Charset.forName("UTF16");

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
                                    jInternalFrame.setTitle(url.toExternalForm());
                                    updateEditor(url);
                                }
                                return;
                            }
                        }
                    }


                    private void updateEditor(URL url) {
                        if (url != null) {
                            try {
                                jEditorPane.setPage(url.toExternalForm());
                            } catch (IOException e) {
                                System.err.println("Attempted to read a bad URL: " + url.toExternalForm());
                            }
                        } else {
                            System.err.println("Couldn't find file: " + url.toExternalForm());
                        }
                        urlText.setText(url.toExternalForm());
                        try {
                            final ImageIcon icon;
                            Dimension viewSize = jInternalFrame.getContentPane().getSize();
                            jInternalFrame.getContentPane().setPreferredSize(viewSize);

                            int v = (int) viewSize.getHeight();
                            URL url1 = new URL("http://chart.apis.google.com/chart?cht=qr&chs=" + v + "&chl=" + url.toString());
                            icon = new ImageIcon(url1);

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    qrCode.setIcon(icon);
                                    qrCode.invalidate();
                                    qrCode.repaint();
                                }
                            });
                        } catch (MalformedURLException ignored) {
                        }
                    }

                }, true);
        bar.add(new AbstractAction("Record") {
            @Override
            public void actionPerformed(ActionEvent e) {


                final Exchanger<BufferedImage> engine = new Exchanger<BufferedImage>();

                final Adjustable slider = jScrollPane.getVerticalScrollBar();


                slider.setValue(slider.getMinimum());

                Runnable painterThread = new Runnable() {
                    public void run() {
                        BufferedImage image = null;
                        double end = 0.9 * slider.getMaximum();
                        while (slider.getValue() < end) {


                            if (image == null)
                                image = (BufferedImage) panel.createImage(panel.getWidth(), panel.getHeight());

                            panel.paint(image.getGraphics());
                            try {
                                image = engine.exchange(image);
                            } catch (InterruptedException e1) {
                                return;
                            }
                            int maximum = slider.getMaximum();
                            int value = slider.getValue();

                            slider.setValue(value + 1);
                        }

                        try {
                            engine.exchange(null);
                        } catch (InterruptedException e1) {
                            return;
                        }

                    }
                };

                final JFileChooser chooser = new JFileChooser("/tmp/");

                if (APPROVE_OPTION == chooser.showSaveDialog(SceneLayoutApp.desktopPane)) {
                    Runnable canvasThread = new Runnable() {
                        public void run() {
                            File selectedFile = chooser.getSelectedFile();
//                    Callable writeCallable = new Callable() {
//                        public Object call() throws Exception {
                            AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
                            gifEncoder.setFrameRate(10);
                            gifEncoder.setQuality(20);
                            gifEncoder.setDelay(100);
                            gifEncoder.start(selectedFile.getAbsolutePath());
                            BufferedImage image = null;
                            try {
                                do {
                                    image = engine.exchange(image);
                                    gifEncoder.addFrame(image);

                                }
                                while (image != null);
                            } catch (Exception e1) {
                            }
                            System.out.println("gifencoder.finish() =" + gifEncoder.finish());
                        }
                    };
                    Future<Object> future = (Future<Object>) SceneLayoutApp.threadPool.submit(painterThread);
                    SceneLayoutApp.threadPool.submit(canvasThread);
                }
            }
        });
    }
}
