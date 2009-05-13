package scene;

import com.thoughtworks.xstream.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static final XStream XSTREAM = new XStream();

    public App() {
        super();    //TODO: verify for a purpose
    }

    public static void main(String[] args) {

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

        jMenu.add(

                new JMenuItem(

                        new AbstractAction("Quit") {
                            public void actionPerformed(ActionEvent actionEvent) {

                                System.exit(0);


                            }
                        }));

        panel.add(new JScrollPane(desktopPane), BorderLayout.CENTER);
        final JToolBar bar = new JToolBar();

        final JPanel panel1 = new JPanel(new FlowLayout());


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
                new AbstractAction() {
                    public void actionPerformed(final ActionEvent actionEvent) {

                        final Object o = comboNewWindow.getSelectedItem();
                        final String[] strings = o
                                .toString().split(":");

                        final JInternalFrame internalFrame = new JInternalFrame(
                                "new " + o
                        );

                        final Integer x = Integer.decode(strings[0]);
                        final Integer y = Integer.decode(strings[1]);

                        
                         internalFrame.setResizable(false);
                        final Image image = panel.createImage(x, y);
                        internalFrame.setContentPane(
                                                    new 
                        );
                        desktopPane.add(internalFrame);
                        internalFrame.show();
                        internalFrame.setClosable(true);
                        internalFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

                        final DropTarget dropTarget = new DropTarget();

                        internalFrame.setDropTarget(dropTarget);

                        dropTarget.setActive(true);
                        dropTarget.setDefaultActions(DnDConstants.ACTION_LINK);

                        try {
                            dropTarget.addDropTargetListener(new DropTargetListener() {
                                private final DataFlavor urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL", "URL");

                                /**
                                 * The canvas only supports Files and URL's
                                 * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
                                 */
                                public void dragEnter
                                        (DropTargetDragEvent  event) {
                                    if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || event.isDataFlavorSupported(urlFlavor)) {
                                        return;
                                    }
                                    event.rejectDrag();
                                }

                                /**
                                 * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
                                 */
                                public void dragExit (DropTargetEvent event) {
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
                                 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
                                 */
                                public void drop(DropTargetDropEvent event) {

                                    final Point location = event.getLocation();

                                    final Graphics g = internalFrame.getGraphics();
                                    g.setColor(Color.RED);
                                    g.drawRect(location.x,location.y,location.x+64,location.y+64);
                                    
                                    // important to first try urlFlavor
                                    if (event.isDataFlavorSupported(urlFlavor)) {
                                        try {
                                            event.acceptDrop(DnDConstants.ACTION_LINK);
                                            Transferable trans = event.getTransferable();
                                            URL url = (URL) (trans.getTransferData(urlFlavor));
                                            String urlStr = url.toString();
                                            System.err.println(urlStr);
                                            event.dropComplete(true);
                                        } catch (Exception e) {
                                            event.dropComplete(false);
                                        }
                                    } else
                                    if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                        try {
                                            event.acceptDrop(DnDConstants.ACTION_LINK);
                                            Transferable trans = event.getTransferable();
                                            java.util.List<File> list = (List<File>) (trans.getTransferData(DataFlavor.javaFileListFlavor));

                                            for (File o1 : list) {
                                                System.err.println(o1.getAbsolutePath());
                                            }
//                                            File  droppedFile = (File) list.get(0); // More than one file -> get only first file
//                                            System.err.println(droppedFile.getAbsolutePath());
                                            event.dropComplete(true);
                                        } catch (Exception e) {
                                            event.dropComplete(false);
                                        }
                                    }
                                }
                            });
                        } catch (TooManyListenersException e) {
                            e.printStackTrace();  //TODO: Verify for a purpose
                        }
                    }
                }

        );
        comboNewWindow.setBorder(BorderFactory.createTitledBorder("Create New Window"));
        bar.add(comboNewWindow);

        comboNewWindow.setMaximumSize(comboNewWindow.getPreferredSize());

        jf.setVisible(true);

    }
}
