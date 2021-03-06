package scene.anim;

import scene.action.*;
import scene.dnd.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:26:29 PM
 */
public class WebAnimatorV1 extends WebAnimatorImpl {
    private JToolBar bar = new JToolBar();
    private JTextField urlText = new JTextField();
    private JLabel qrCode = new JLabel();
    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel content = new JPanel(new BorderLayout());
    { setHtmlPanel( new JEditorPane());}
    {jScrollPane = new JScrollPane(getHtmlPanel());}
    private DataFlavor[] dataFlavors = new DataFlavor[]{
            DataFlavor.javaFileListFlavor,
            new DataFlavor("application/x-java-url;class=java.net.URL", "URL"),
            new DataFlavor("text/x-uri-list; class=java.util.List", "URI List"),
            new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
    };
    public JSlider startSlider = new JSlider();
    public JSlider stopSlider = new JSlider();
    private JEditorPane htmlPanel
            
                ;

    public WebAnimatorV1(Object... a) {
        super("Create Web Animation V1");
        this.init();
    }

      protected void init() {

        htmlPanel.setEditable(false);
        panel.add(getQrCode(), BorderLayout.WEST);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(jScrollPane, BorderLayout.CENTER);
        bar.add(urlText);
        content.add(panel, BorderLayout.CENTER);
        content.add(bar, BorderLayout.NORTH);

        final TitledBorder border1 = BorderFactory.createTitledBorder("Beg");
        startSlider.setBorder(border1);
                startSlider.setOrientation(SwingConstants.VERTICAL);
                startSlider.setInverted(true);

        final TitledBorder border2 = BorderFactory.createTitledBorder("End");
        stopSlider.setBorder(border2);
                stopSlider.setOrientation(SwingConstants.VERTICAL);
                stopSlider.setInverted(true);
        
        final JSplitPane jSplitPane = new JSplitPane(SwingConstants.VERTICAL, startSlider, stopSlider);
        content.add( jSplitPane, BorderLayout.WEST);
//        

        final JScrollBar verticalScrollBar = jScrollPane.getVerticalScrollBar();
        final AdjustmentListener[] listeners = verticalScrollBar.getAdjustmentListeners();

        verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                startSlider.setMaximum(verticalScrollBar.getMaximum()-verticalScrollBar.getModel().getExtent());
//                     startSlider.setExtent(verticalScrollBar.getModel().getExtent());
                stopSlider.setMaximum(verticalScrollBar.getMaximum()-verticalScrollBar.getModel().getExtent());
//                     stopSlider.setExtent(verticalScrollBar.getModel().getExtent());
                       }
        });

        startSlider.addChangeListener(new MyChangeListener(verticalScrollBar));
        stopSlider.addChangeListener(new MyChangeListener(verticalScrollBar));


        setContentPane(content);
        final JInternalFrame f = this;

        bar.add(new RecordWebScrollerGifAnim(this)  );
        bar.add(new RecordWebScrollerPngDir(this)  );
        // f.setPreferredSize(getPreferredSize());
        f.setMaximizable(true);
        f.setClosable(true);
        f.setIconifiable(true);
        f.setResizable(true);
        f.setToolTipText("This window creates a small barcoded banner from an html web link");
        urlText.setToolTipText("Enter a URL here and hit enter to update the layouts");
        bar.setToolTipText("Press Record to create a scrolling banner in the sizes of the layouts in this view.  Record will save an animated gif file.");

        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        f.setDropTarget(new DropTarget(f, DnDConstants.ACTION_LINK, new WebViewDropTargetListener((WebAnimatorV1) f), true));
        new DropTarget(qrCode, DnDConstants.ACTION_COPY_OR_MOVE,
                new DropTargetListener() {
                    /**
                     * @param dtde the <code>DropTargetDragEvent</code>
                     */
                    @Override
                    public void dragEnter(DropTargetDragEvent dtde) {
                        for (DataFlavor dataFlavor : dataFlavors) {

                            if (dtde.isDataFlavorSupported(dataFlavor)) {
                                return;
                            }
                        }
                        dtde.rejectDrag();
                    }

                    @Override
                    public void dragOver(DropTargetDragEvent dtde) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void dropActionChanged(DropTargetDragEvent dtde) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    @Override
                    public void dragExit(DropTargetEvent dte) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

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

    public JComponent getJEditorPane() {
        return htmlPanel;
    }

    public void setJEditorPane(JEditorPane jEditorPane) {
        this.htmlPanel = jEditorPane;
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

    public void updateEditor(URL url1) {
        if (url1 == null) {
            return;
        } 
        try {                                     
            ((JEditorPane) getJEditorPane()).setPage(url1);
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url1);
        }
        getUrlText().setText(url1.toExternalForm());
        try {
            final ImageIcon icon;
            Dimension viewSize = getContentPane().getSize();
            getContentPane().setPreferredSize(viewSize);

            int v = (int) viewSize.getHeight();
            url1 = new URL("http://chart.apis.google.com/chart?cht=qr&chs=" + v + "&chl=" + url1.toString());
            icon = new ImageIcon(url1);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getQrCode().setIcon(icon);
                    getQrCode().invalidate();
                    getQrCode().repaint();
                }
            });
        } catch (MalformedURLException ignored) {
        }
    }

    @Override
    public JComponent getHtmlPanel() {
        return htmlPanel;  //ToDo: verify for a purpose
    }

    @Override
    public void setHtmlPanel(JComponent htmlPanel) {
        this.htmlPanel= (JEditorPane) htmlPanel;
    }
 
    private static class MyChangeListener implements ChangeListener {
        private final JScrollBar verticalScrollBar;

        public MyChangeListener(JScrollBar verticalScrollBar) {
            this.verticalScrollBar = verticalScrollBar;
        }

        @Override
            public void stateChanged(ChangeEvent e) {
            final int value = ((JSlider) e.getSource()).getValue();
            verticalScrollBar.setValue(value);

        }
    }
}
