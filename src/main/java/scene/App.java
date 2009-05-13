package scene;

import com.thoughtworks.xstream.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Timer;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private JInternalFrame dumpWindow;
    final JTextPane permText = new JTextPane();
    private static final Timer TIMER = new Timer();


    public App() {
        super();    //TODO: verify for a purpose

        final JFrame jf = new JFrame("Scene Layout");
        final JDesktopPane desktopPane = new JDesktopPane();


        final JPanel panel = new JPanel(new BorderLayout());
        jf.setContentPane(panel);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jf.setMaximumSize(screenSize);
        jf.setSize(screenSize);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JMenuBar mb = new JMenuBar();
        jf.setJMenuBar(mb);

        final JMenu jMenu = new JMenu("File");
        mb.add(jMenu);
        mb.add(new JMenu("Edit"));
        mb.add(new JMenu("Help"));

        jMenu.add(new JMenuItem(new QuitAction()));

        panel.add(new JScrollPane(desktopPane), BorderLayout.CENTER);
        final JToolBar bar = new JToolBar();


        panel.add(bar, BorderLayout.NORTH);

        final
        JComboBox comboNewWindow = new JComboBox(


                new String[]{
                        "320:180",
                        "320:240",
                        "640:360",
                        "640:480",
                        "1280:720",
                        "1920:1080"
                });

        comboNewWindow.addActionListener(
                new CreateWindowAction(comboNewWindow, desktopPane)

        );
        comboNewWindow.setBorder(BorderFactory.createTitledBorder("Create New Window"));
        bar.add(comboNewWindow);


        final JCheckBox permaViz = new JCheckBox();
        permaViz.setText("Show the dump window");
        permaViz.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        dumpWindow = new JInternalFrame("perma dump window");
        dumpWindow.setContentPane(new JScrollPane(permText));

        permaViz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dumpWindow.setVisible(permaViz.isSelected());
            }
        });

        comboNewWindow.setMaximumSize(comboNewWindow.getPreferredSize());


        permaViz.setSelected(false);
        bar.add(permaViz);
        desktopPane.add(dumpWindow);
        dumpWindow.setSize(400, 400);
        dumpWindow.setResizable(true);
        dumpWindow.setClosable(false);
        dumpWindow.setIconifiable(false);
        final JMenuBar m = new JMenuBar();
        final JMenu cmenu = new JMenu("Create");
        m.add(cmenu);
        cmenu.add(new JMenuItem(new AbstractAction("new") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] in = (Object[]) XSTREAM.fromXML(permText.getText());

                final JInternalFrame ff = new JInternalFrame();

                final ScenePanel c = new ScenePanel(App.this);
                ff.setContentPane(c);
                desktopPane.add(ff);
                final Dimension d = (Dimension) in[0];
                c.setMaximumSize(d);
                c.setPreferredSize(d);

                ff.setSize(d.width + 50, d.height + 50);
                ScenePanel.panes.put(c, (List<Pair<Point, URL>>) in[1]);

                c.invalidate();
                c.repaint();


                ff.pack();
                ff.setClosable(true);

                ff.setMaximizable(false);
                ff.setIconifiable(false);
                ff.setResizable(false);
                ff.show();
            }
        }));


        dumpWindow.setJMenuBar(m);
        jf.setVisible(true);

    }


    private static final XStream XSTREAM = new XStream();


    static public void main(String[] args) {
        new App();

    }


    private class MyDropTargetListener implements DropTargetListener {
        private final DataFlavor urlFlavor;
        private final JPanel component;

        public MyDropTargetListener(final Component component) {
            this.component = (JPanel) component;
            urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL", "URL");
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
                    List<File> list = (List<File>) (trans.getTransferData(DataFlavor.javaFileListFlavor));

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

                final ImageIcon icon = new ImageIcon(url);
                ScenePanel.images.put(url, icon);
                ScenePanel.panes.get(component).add(new Pair<Point, URL>(dragSpot, url));

                List<Pair<Point, URL>> pairs = ScenePanel.panes.get(component);

                final Object[] objects = {component.getMaximumSize(), pairs,};
                final String s = XSTREAM.toXML(objects);
                permText.setText(s);

                final TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        component.paint(component.getGraphics());
                    }
                };
                TIMER.schedule(timerTask, 250);

            }
        }
    }

    static class QuitAction extends AbstractAction {
        public QuitAction() {
            super("Quit");
        }

        public void actionPerformed(ActionEvent actionEvent) {

            System.exit(0);


        }
    }

    class CreateWindowAction extends AbstractAction {
        private final JComboBox comboNewWindow;
        private final JDesktopPane desktopPane;

        public CreateWindowAction(JComboBox comboNewWindow, JDesktopPane desktopPane) {
            this.comboNewWindow = comboNewWindow;
            this.desktopPane = desktopPane;
        }

        public void actionPerformed(final ActionEvent actionEvent) {

            final Object o = comboNewWindow.getSelectedItem();
            final String[] strings = o
                    .toString().split(":");

            final JInternalFrame vw = new JInternalFrame();

            final JPanel iView = new ScenePanel(App.this);
            iView.setLayout(null);
            vw.setContentPane(iView);

            final Integer w = Integer.decode(strings[0]);
            final Integer h = Integer.decode(strings[1]);

            vw.setResizable(false);


            desktopPane.add(vw);

            vw.show();
            vw.setClosable(true);
            vw.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

            final DropTarget dropTarget = new DropTarget();

            iView.setDropTarget(dropTarget);

            vw.setSize(w + 100, h + 100);
            final Dimension preferredSize = new Dimension(w, h);
            iView.setPreferredSize(preferredSize);
            iView.setMaximumSize(new Dimension(w, h));

            vw.pack();

            dropTarget.setActive(true);
            dropTarget.setDefaultActions(DnDConstants.ACTION_LINK);

            try {
                dropTarget.addDropTargetListener(new MyDropTargetListener(iView));
            } catch (TooManyListenersException e) {
                e.printStackTrace();
            }
        }

    }

}
