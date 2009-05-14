package scene;

import com.sun.media.ui.*;

import javax.media.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * User: jim
 * Date: May 14, 2009
 * Time: 8:33:18 AM
 */
public class MDIApp extends Frame {

    /*************************************************************************
     * MAIN PROGRAM / STATIC METHODS
     *************************************************************************/
    
    public static void main(String args[]) {
	MDIApp mdi = new MDIApp();
    }

    static void Fatal(String s) {
	MessageBox mb = new MessageBox("JMF Error", s);
    }    

    /*************************************************************************
     * VARIABLES
     *************************************************************************/
    
    JMFrame jmframe = null;
    JDesktopPane desktop;
    FileDialog fd = null;
    CheckboxMenuItem cbAutoLoop = null;
    Player player = null;
    Player newPlayer = null;
    String filename;
    
    /*************************************************************************
     * METHODS
     *************************************************************************/
    
    public MDIApp() {
	super("Java Media Player");

	// Add the desktop pane
	setLayout( new BorderLayout() );
	desktop = new JDesktopPane();
	desktop.setDoubleBuffered(true);
	add("Center", desktop);
	setMenuBar(createMenuBar());
	setSize(640, 480);
	setVisible(true);
	
	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	} catch (Exception e) {
	    System.err.println("Could not initialize java.awt Metal lnf");
	}
	addWindowListener( new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
		System.exit(0);
	    }
	} );

	Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, new Boolean(true));
    }
    
    private MenuBar createMenuBar() {
	ActionListener al = new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command.equals("Open")) {
		    if (fd == null) {
			fd = new FileDialog(MDIApp.this, "Open File",
						       FileDialog.LOAD);
			fd.setDirectory("/movies");
		    }
		    fd.show();
		    if (fd.getFile() != null) {
			String filename = fd.getDirectory() + fd.getFile();
			openFile("file:" + filename);
		    }
		} else if (command.equals("Exit")) {
		    dispose();
		    System.exit(0);
		}
	    }
	};

	MenuItem item;
	MenuBar mb = new MenuBar();
	// File Menu
	Menu mnFile = new Menu("File");
	mnFile.add(item = new MenuItem("Open"));
	item.addActionListener(al);
	mnFile.add(item = new MenuItem("Exit"));
	item.addActionListener(al);

	// Options Menu	
	Menu mnOptions = new Menu("Options");
	cbAutoLoop = new CheckboxMenuItem("Auto replay");
	cbAutoLoop.setState(true);
	mnOptions.add(cbAutoLoop);
	
	mb.add(mnFile);
	mb.add(mnOptions);
	return mb;
    }			

    /**
     * Open a media file.
     */
    public void openFile(String filename) {
	String mediaFile = filename;
	Player player = null;
	// URL for our media file
	URL url = null;
	try {
	    // Create an url from the file name and the url to the
	    // document containing this applet.
	    if ((url = new URL(mediaFile)) == null) {
		Fatal("Can't build URL for " + mediaFile);
		return;
	    }
	    
	    // Create an instance of a player for this media
	    try {
		player = Manager.createPlayer(url);
	    } catch (NoPlayerException e) {
		Fatal("Error: " + e);
	    }
	} catch (MalformedURLException e) {
	    Fatal("Error:" + e);
	} catch (IOException e) {
	    Fatal("Error:" + e);
	}
	if (player != null) {
	    this.filename = filename;
	    JMFrame jmframe = new JMFrame(player, filename);
	    desktop.add(jmframe);
	}
    }
}

class JMFrame extends JInternalFrame implements ControllerListener {
    Player mplayer;
    Component visual = null;
    Component control = null;
    int videoWidth = 0;
    int videoHeight = 0;
    int controlHeight = 30;
    int insetWidth = 10;
    int insetHeight = 30;
    boolean firstTime = true;
    
    public JMFrame(Player player, String title) {
	super(title, true, true, true, true);
	getContentPane().setLayout( new BorderLayout() );
	setSize(320, 10);
	setLocation(50, 50);
	setVisible(true);
	mplayer = player;
	mplayer.addControllerListener((ControllerListener) this);
	mplayer.realize();
	addInternalFrameListener( new InternalFrameAdapter() {
	    public void internalFrameClosing(InternalFrameEvent ife) {
		mplayer.close();
	    }
	} );
		    
    }
    
    public void controllerUpdate(ControllerEvent ce) {
	if (ce instanceof RealizeCompleteEvent) {
	    mplayer.prefetch();
	} else if (ce instanceof PrefetchCompleteEvent) {
	    if (visual != null)
		return;
	    
	    if ((visual = mplayer.getVisualComponent()) != null) {
		Dimension size = visual.getPreferredSize();
		videoWidth = size.width;
		videoHeight = size.height;
		getContentPane().add("Center", visual);
	    } else
		videoWidth = 320;
	    if ((control = mplayer.getControlPanelComponent()) != null) {
		controlHeight = control.getPreferredSize().height;
		getContentPane().add("South", control);
	    }
	    setSize(videoWidth + insetWidth,
		    videoHeight + controlHeight + insetHeight);
	    validate();
	    mplayer.start();
	} else if (ce instanceof EndOfMediaEvent) {
	    mplayer.setMediaTime(new Time(0));
	    mplayer.start();
	}
    }
}