package scene;

import com.thoughtworks.xstream.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.List;
import java.util.Timer;

/**
 * Hello world!
 */
public class SceneLayoutApp {
    static private JInternalFrame dumpWindow;
    static final JTextPane permText = new JTextPane();
    static final Timer TIMER = new Timer();

    public final static JDesktopPane desktopPane = new JDesktopPane();
    private static SceneLayoutApp instance;

    public SceneLayoutApp() {
        super();

        final JFrame jf = new JFrame("Scene Layout");


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

        jMenu.add(new JMenuItem(new scene.action.QuitAction()));

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
                new scene.action.CreateWindowAction()

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
        final JMenuItem CreateAction = new JMenuItem(new AbstractAction("new") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        Object[] in = (Object[]) XSTREAM.fromXML(permText.getText());

                        final JInternalFrame ff = new JInternalFrame();

                        final ScenePanel c = new ScenePanel();
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
                };

                SwingUtilities.invokeLater(
                        runnable
                );
            }
        });
        cmenu.add(CreateAction);

        dumpWindow.setJMenuBar(m);
        jf.setVisible(true);
    }

    static final XStream XSTREAM = new XStream();

    static public void main(String[] args) {
        new SceneLayoutApp();

    }

    static public SceneLayoutApp getInstance() {
        return instance == null ? instance = new SceneLayoutApp() : instance;
    }
}

