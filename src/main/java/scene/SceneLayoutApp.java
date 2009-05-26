package scene;

import com.thoughtworks.xstream.*;
import scene.action.*;
import scene.alg.*;
import scene.anim.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

/**
 * Hello world!
 */
public class SceneLayoutApp {
    static private JInternalFrame dumpWindow;
    public static final JTextPane permText = new JTextPane();
    public static final Timer TIMER = new Timer();

    public final static JDesktopPane desktopPane;
    private static SceneLayoutApp instance;
    public static ExecutorService threadPool = Executors.newCachedThreadPool();

    public SceneLayoutApp() {
        super();

        final JFrame frame = new JFrame("Scene Layout");


        final JPanel panel = new JPanel(new BorderLayout());
        frame.setContentPane(panel);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setMaximumSize(screenSize);
        frame.setSize(screenSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JMenuBar mb = new JMenuBar();
        frame.setJMenuBar(mb);


        final JMenu jMenu = new JMenu("File");
        mb.add(jMenu);
        mb.add(new JMenu("Edit"));
        mb.add(new JMenu("Help"));
        JMenu menu = new JMenu("Look and Feel");


        //
        // Get all the available look and feel that we are going to use for 
        // creating the JMenuItem and assign the action listener to handle
        // the selection of menu item to change the look and feel.
        //
        UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < lookAndFeelInfos.length; i++) {
            final UIManager.LookAndFeelInfo lookAndFeelInfo = lookAndFeelInfos[i];
            JMenuItem item = new JMenuItem(lookAndFeelInfo.getName());
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        //
                        // Set the look and feel for the frame and update the UI 
                        // to use a new selected look and feel.
                        //
                        UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                        SwingUtilities.updateComponentTreeUI(frame);
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedLookAndFeelException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            menu.add(item);
        }

        mb.add(menu);
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
                new CreateSceneWindowAction()

        );
        comboNewWindow.setBorder(BorderFactory.createTitledBorder("Create New Window"));
        bar.add(comboNewWindow);
        bar.add(new AbstractAction("Progress Bars") {

            /**
             * Invoked when an action occurs.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProgressBarAnimator();
            }
        });
        bar.add(new AbstractAction("Sliders") {

            /**
             * Invoked when an action occurs.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                new SliderBarAnimator();
            }
        });

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
        bar.add(new CreateWebViewAction());
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
                        ScenePanel.panes.put(
                                c, (List<Pair<Point, ArrayList<URL>>>) in[1]);

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
//        JMenuBar menuBar = new JMenuBar();


//        getContentPane().add(menuBar);

        dumpWindow.setJMenuBar(m);
        frame.setVisible(true);

    }

    public static final XStream XSTREAM;

    static {
        XSTREAM = new XStream();
//        XSTREAM.aliasType( "triple", Triple.class);
        final JEditorPane ed = new JEditorPane();

//        final SimpleUserAgentContext userAgentContext = new SimpleUserAgentContext();
//        final SimpleHtmlRendererContext rendererContext = new SimpleHtmlRendererContext(ed, userAgentContext);

        desktopPane = new JDesktopPane() {


            Callable<Image> callable = new Callable<Image>() {
                public Image call() throws Exception {
                    setOpaque(false);
                    ed.setSize(getSize());
                    ed.setOpaque(true);
//                    ed.setBackground(Color.black);
                    final String s = Arrays.toString(System.getenv().values().toArray());
                    final String s1 = URLEncoder.encode(s);
//                    rendererContext.navigate("http://www.hideftvads.com");
ed.setPage("http://www.hideftvads.com");
//                        ed.setEnabled(false);

                    ed.invalidate();
                    ed.repaint();
                    return null;
                }
            };
            Future<Image> future =

                    (Future<Image>) SceneLayoutApp.threadPool.submit(callable);

            @Override
            public void paint(Graphics g) {
                
                try {
                    final Image o = future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //TODO: Verify for a purpose
                } catch (ExecutionException e) {
                    e.printStackTrace();  //TODO: Verify for a purpose
                }
                ed.setSize(getSize());
                ed.paint(g);  super.paint(g);
            }

        };
        desktopPane.invalidate();
    }

    static public void main(String[] args) {

//        final BrowserFrame browserFrame = new BrowserFrame();

        new SceneLayoutApp();

    }

    static public SceneLayoutApp getInstance() {
        return instance == null ? instance = new SceneLayoutApp() : instance;
    }

}

