package scene.anim;

import org.lobobrowser.html.*;
import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.style.*;
import org.lobobrowser.html.test.*;
import scene.action.*;
import scene.dnd.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.net.*;
import java.beans.*;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:26:29 PM
 */
public class WebAnimatorV2 extends WebAnimatorImpl {
    private HtmlPanel htmlPanel = null;

    public WebAnimatorV2(Object... a) {
        super("Create Web Animation");
        this.init();
    }

    protected void init() {


        getPanel().add(getQrCode(), BorderLayout.WEST);
//        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        panel.add(jScrollPane, BorderLayout.CENTER);
        getBar().add(getUrlText());
        getContent().add(getPanel(), BorderLayout.CENTER);
        getContent().add(getBar(), BorderLayout.NORTH);

        final TitledBorder border1 = BorderFactory.createTitledBorder("Beg");
        getStartSlider().setBorder(border1);
        getStartSlider().setOrientation(SwingConstants.VERTICAL);
        getStartSlider().setInverted(true);

        final TitledBorder border2 = BorderFactory.createTitledBorder("End");
        getStopSlider().setBorder(border2);
        getStopSlider().setOrientation(SwingConstants.VERTICAL);
        getStopSlider().setInverted(true);

        final JSplitPane jSplitPane = new JSplitPane(SwingConstants.VERTICAL, getStartSlider(), getStopSlider());
        final JToolBar west = new JToolBar();
        getContent().add(west, BorderLayout.WEST);
//        
        west.add(jSplitPane);

//        final JScrollBar verticalScrollBar = jScrollPane.getVerticalScrollBar();
//        final AdjustmentListener[] listeners = verticalScrollBar.getAdjustmentListeners();
//
//        verticalScrollBar.addAdjustmentListener(new AdjustmentListener() {
//            @Override
//            public void adjustmentValueChanged(AdjustmentEvent e) {
//                startSlider.setMaximum(verticalScrollBar.getMaximum() - verticalScrollBar.getModel().getExtent());
//                     startSlider.setExtent(verticalScrollBar.getModel().getExtent());
//                stopSlider.setMaximum(verticalScrollBar.getMaximum() - verticalScrollBar.getModel().getExtent());
//                     stopSlider.setExtent(verticalScrollBar.getModel().getExtent());
//            }
//        });
//
//        startSlider.addChangeListener(new MyChangeListener(verticalScrollBar));
//        stopSlider.addChangeListener(new MyChangeListener(verticalScrollBar));
//

        setContentPane(getContent());
        final JInternalFrame f = this;

        getBar().add(new RecordWebScrollerGifAnim(this));
        getBar().add(new RecordWebScrollerPngDir(this));
        // f.setPreferredSize(getPreferredSize());
        f.setMaximizable(true);
        f.setClosable(true);
        f.setIconifiable(true);
        f.setResizable(true);
        f.setToolTipText("This window creates a small barcoded banner from an html web link");
        getUrlText().setToolTipText("Enter a URL here and hit enter to update the layouts");

        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        f.setDropTarget(new DropTarget(f, DnDConstants.ACTION_LINK, new WebViewDropTargetListener((WebAnimatorImpl) f), true));
//
//        htmlPanel.addSelectionChangeListener(new SelectionChangeListener() {
//            public void selectionChanged(final SelectionChangeEvent event) {
//                final Node selectionNode = htmlPanel.getSelectionNode();
//                System.err.println("selectionChanged(): selection node: " + selectionNode);
//
//
//            }
//        });

        final DropTarget dropTarget1 = new DropTarget(getQrCode(), DnDConstants.ACTION_COPY_OR_MOVE,
                new DropTargetListener() {
                    /**
                     * @param dtde the <code>DropTargetDragEvent</code>
                     */
                    @Override
                    public void dragEnter(DropTargetDragEvent dtde) {
                        for (DataFlavor dataFlavor : getDataFlavors()) {

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
                        for (DataFlavor flavor : getDataFlavors()) {

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

                                final ImageIcon icon = (ImageIcon) getQrCode().getIcon();

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


    public void updateEditor(URL url) {
        if (url != null) {

            
            if(getHtmlPanel() !=null)
            getPanel().remove(getHtmlPanel());
            final HtmlPanel htmlPanel1 = new HtmlPanel();
           setHtmlPanel(htmlPanel1) ;
            getPanel().add(htmlPanel1 );
            htmlPanel1.setDefaultOverflowY(RenderState.OVERFLOW_SCROLL);
            htmlPanel1.setDefaultMarginInsets(new Insets(0, 0, 0, 0));
            final SimpleUserAgentContext ucontext = new SimpleUserAgentContext();
            final HtmlRendererContext rcontext = new SimpleHtmlRendererContext((HtmlPanel) getHtmlPanel(), ucontext);

            rcontext.navigate(url, null);
                                                                getHtmlPanel().addPropertyChangeListener(new PropertyChangeListener() {
                                                                    @Override
                                                                    public void propertyChange(PropertyChangeEvent evt) {
                                                                         System.err.println(":"+evt.getPropertyName()+":"+evt.getOldValue()+":"+evt.getNewValue()  );
                                                                    }
                                                                });
            

            getUrlText().setText(url.toExternalForm());
            try {
                final ImageIcon icon;
                Dimension viewSize = getContentPane().getSize();
//                getContentPane().setPreferredSize(viewSize);

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
            } catch (MalformedURLException ignored) {
            }
        }
    }

    public JComponent getHtmlPanel() {
        return htmlPanel;
    }

    @Override
    public void setHtmlPanel(JComponent htmlPanel) {
        this.htmlPanel= (HtmlPanel) htmlPanel;
    }

    void setHtmlPanel(HtmlPanel htmlPanel) {
        this.htmlPanel = htmlPanel;
    }


}
