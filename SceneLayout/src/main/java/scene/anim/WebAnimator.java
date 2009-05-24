package scene.anim;

import scene.action.RecordWebScrollerAction;
import scene.dnd.WebViewDropTargetListener;
import scene.SceneLayoutApp;
import static scene.SceneLayoutApp.permText;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:26:29 PM
 */
public class WebAnimator extends JInternalFrame {
    private JToolBar bar = new JToolBar();
    private JTextField urlText = new JTextField();
    private JLabel qrCode = new JLabel();
    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel content = new JPanel(new BorderLayout());
    private JEditorPane jEditorPane = new JEditorPane();
    private JScrollPane jScrollPane = new JScrollPane(jEditorPane);
    private DataFlavor[] dataFlavors = new DataFlavor[]{
            DataFlavor.javaFileListFlavor,
            new DataFlavor("application/x-java-url;class=java.net.URL", "URL"),
            new DataFlavor("text/x-uri-list; class=java.util.List", "URI List"),
            //            new DataFlavor("text/x-java-file-list; class=java.util.List", "Java FileList"),
            new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
//
//                                new DataFlavor("text/x-moz-url; class=java.lang.String", "Mozilla URL"),
//                                DataFlavor.imageFlavor,
            //       flavor4 new DataFlavor("image/x-java-image; class=java.util.List", "URI List"),
            /*      new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                    new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
                    new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
            */
    };

    public WebAnimator(Object... a) {
        super("Create Web Animation");
        this.init();
    }

    private void init() {

        jEditorPane.setEditable(false);
        panel.add(getQrCode(), BorderLayout.WEST);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(jScrollPane, BorderLayout.CENTER);
        bar.add(urlText);
        content.add(panel, BorderLayout.CENTER);
        content.add(bar, BorderLayout.NORTH);
        final JToolBar startStopTools = new JToolBar();
        final JScrollBar jScrollBar = jScrollPane.createVerticalScrollBar();
        content.add(jScrollBar, BorderLayout.WEST); 
             jEditorPane
                     .addPropertyChangeListener(new PropertyChangeListener() {
                         @Override
                         public void propertyChange(PropertyChangeEvent evt) {
                             permText.setText (permText.getText()+"\n++ eee:"+evt.getPropertyName()+":"+
                                     evt.getOldValue()+":"+ evt.getNewValue());
                         }
                     });


        setContentPane(content);
        final JInternalFrame f = this;

        bar.add(new RecordWebScrollerAction(this) {{
            setToolTipText("Record a scrolling view of the contents in the window you have sized and shaped"
            );
        }});
//                f.setPreferredSize(getPreferredSize());
        f.setMaximizable(true);
        f.setClosable(true);
        f.setIconifiable(true);
        f.setResizable(true);
        f.setToolTipText("This window creates a small barcoded banner from a genuine html web link");
        urlText.setToolTipText("Enter a URL here and hit enter to update the layouts");
        bar.setToolTipText("Press Record to create a scrolling banner in the sizes of the layouts in this view.  Record will save an animated gif file.");

        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        f.setDropTarget(new DropTarget(f, DnDConstants.ACTION_LINK, new WebViewDropTargetListener((WebAnimator) f), true));
        new DropTarget(qrCode, DnDConstants.ACTION_COPY_OR_MOVE,
                new DropTargetListener() {
                    /**
                     * Called while a drag operation is ongoing, when the mouse pointer enters
                     * the operable part of the drop site for the <code>DropTarget</code>
                     * registered with this listener.
                     *
                     * @param dtde the <code>DropTargetDragEvent</code>
                     */
                    @Override
                    public void dragEnter(DropTargetDragEvent dtde) {
                        for (DataFlavor dataFlavor : dataFlavors) {

                            if (dtde.isDataFlavorSupported(dataFlavor)) {
                                return ;

                            }
                        }
                        dtde.rejectDrag();
                    }

                    /**
                     * Called when a drag operation is ongoing, while the mouse pointer is still
                     * over the operable part of the drop site for the <code>DropTarget</code>
                     * registered with this listener.
                     *
                     * @param dtde the <code>DropTargetDragEvent</code>
                     */
                    @Override
                    public void dragOver(DropTargetDragEvent dtde) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    /**
                     * Called if the user has modified
                     * the current drop gesture.
                     * <p/>
                     *
                     * @param dtde the <code>DropTargetDragEvent</code>
                     */
                    @Override
                    public void dropActionChanged(DropTargetDragEvent dtde) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    /**
                     * Called while a drag operation is ongoing, when the mouse pointer has
                     * exited the operable part of the drop site for the
                     * <code>DropTarget</code> registered with this listener.
                     *
                     * @param dte the <code>DropTargetEvent</code>
                     */
                    @Override
                    public void dragExit(DropTargetEvent dte) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    /**
                     * Called when the drag operation has terminated with a drop on
                     * the operable part of the drop site for the <code>DropTarget</code>
                     * registered with this listener.
                     * <p/>
                     * This method is responsible for undertaking
                     * the transfer of the data associated with the
                     * gesture. The <code>DropTargetDropEvent</code>
                     * provides a means to obtain a <code>Transferable</code>
                     * object that represents the data object(s) to
                     * be transfered.<P>
                     * From this method, the <code>DropTargetListener</code>
                     * shall accept or reject the drop via the
                     * acceptDrop(int dropAction) or rejectDrop() methods of the
                     * <code>DropTargetDropEvent</code> parameter.
                     * <p/>
                     * Subsequent to acceptDrop(), but not before,
                     * <code>DropTargetDropEvent</code>'s getTransferable()
                     * method may be invoked, and data transfer may be
                     * performed via the returned <code>Transferable</code>'s
                     * getTransferData() method.
                     * <p/>
                     * At the completion of a drop, an implementation
                     * of this method is required to signal the success/failure
                     * of the drop by passing an appropriate
                     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
                     * dropComplete(boolean success) method.
                     * <p/>
                     * Note: The data transfer should be completed before the call  to the
                     * <code>DropTargetDropEvent</code>'s dropComplete(boolean success) method.
                     * After that, a call to the getTransferData() method of the
                     * <code>Transferable</code> returned by
                     * <code>DropTargetDropEvent.getTransferable()</code> is guaranteed to
                     * succeed only if the data transfer is local; that is, only if
                     * <code>DropTargetDropEvent.isLocalTransfer()</code> returns
                     * <code>true</code>. Otherwise, the behavior of the call is
                     * implementation-dependent.
                     * <p/>
                     *
                     * @param dtde the <code>DropTargetDropEvent</code>
                     */
                    @Override
                    public void drop(DropTargetDropEvent dtde) {
                        for (DataFlavor flavor : dataFlavors) {

                            if (dtde.isDataFlavorSupported(flavor)) {

                                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                                final Transferable transferable = dtde.getTransferable();
                                Object data = null;
                                try {
                                    data = transferable.getTransferData(flavor);
                                } catch (UnsupportedFlavorException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                final ImageIcon icon = (ImageIcon) qrCode.getIcon();

                                while (data instanceof Iterable) {

                                    data = ((Iterable) data).iterator().next();

                                }

                                if (data != null) {
                                    if (!(data instanceof File)) {
                                    } else {
                                        try {
                                            data = ((File) data).toURI().toURL();
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                icon.setImage(Toolkit.getDefaultToolkit().
                                        createImage(String.valueOf(data)));
                            }
                        }
                    }
                }, true);
    }


    public JToolBar getBar() {
        return bar;
    }

    public void setBar(JToolBar bar) {
        this.bar = bar;
    }

    public JTextField getUrlText() {
        return urlText;
    }

    public void setUrlText(JTextField urlText) {
        this.urlText = urlText;
    }

    public JEditorPane getJEditorPane() {
        return jEditorPane;
    }

    public void setJEditorPane(JEditorPane jEditorPane) {
        this.jEditorPane = jEditorPane;
    }

    public JScrollPane getJScrollPane() {
        return jScrollPane;
    }

    public void setJScrollPane(JScrollPane jScrollPane) {
        this.jScrollPane = jScrollPane;
    }

    public JLabel getQrCode() {
        return qrCode;
    }

    public void setQrCode(JLabel qrCode) {
        this.qrCode = qrCode;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public void updateEditor(URL url) {
        if (url == null) {
            return;
        }
        try {
            getJEditorPane().setPage(url.toExternalForm());
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url.toExternalForm());
        }
        getUrlText().setText(url.toExternalForm());
        try {
            final ImageIcon icon;
            Dimension viewSize = getContentPane().getSize();
            getContentPane().setPreferredSize(viewSize);

            int v = (int) viewSize.getHeight();
            URL url1 = new URL("http://chart.apis.google.com/chart?cht=qr&chs=" + v + "&chl=" + url.toString());
            icon = new ImageIcon(url1);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getQrCode().setIcon(icon);
                    getQrCode().invalidate();
                    getQrCode().repaint();
                }
            });
        } catch (MalformedURLException ignored) {          }
    }
}
