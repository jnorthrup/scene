package png;/*
* png.MNG_LC_Player.java -- MNG, PNG �\���p�̃A�v���b�g
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU General Public Licence
* ���ӎ��� : �{�v���O�����͖��ۏ؂ł�
*/

import java.awt.*;
import java.applet.*;
import java.net.*;

public class MNG_LC_Player extends Applet {
	private final boolean debug_flag = false;

	public static final String APPLET_NAME = "MNG-LC Player";
	public static final String APPLET_VERSION = "1.3.6";

	private ImageCanvas icanv;

	public MNG_LC_Player() {
		icanv = null;
	}

	public void init() {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.init"); }

		Color bc = null;
		String sbc = getParameter("bgcolor");
		if((sbc != null) && (sbc.length() > 0)) {
			try {
				if(sbc.charAt(0) == '#') {
					sbc = sbc.substring(1, sbc.length());
				}
				int ibc = Integer.parseInt(sbc, 16);
				bc = new Color(ibc);
			} catch(IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}

		icanv = new ImageCanvas();
		if(bc != null) {
			icanv.setBackground(bc);
		}

		setLayout(new BorderLayout());
		add("Center", icanv);

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.init"); }
	}
	public void start() {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.start"); }

		String src = getParameter("src");
		if((src == null) || (src.length() <= 0)) { return; }

		boolean flag_cache = false;
		String cache = getParameter("caching");
		if((cache != null) && cache.equalsIgnoreCase("on")) {
			flag_cache = true;
		}

		try {
			URL url = new URL(getDocumentBase(), src);
			MNGAnimation anim = new MNGAnimation(url, flag_cache);
			anim.setDefaultBackground(icanv.getBackground());
			icanv.playAnimation(anim);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.start"); }
	}
	public void stop() {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.stop"); }

		if(icanv != null) { icanv.stopAnimation(); }

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.stop"); }
	}
	public void destroy() {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.destroy"); }

		removeAll();
		icanv = null;
		System.gc();

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.destroy"); }
	}
	public boolean mouseDown(Event evt, int x , int y) {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.mouseDown"); }

		if(evt.shiftDown()) {
			showStatus(APPLET_NAME + " Version " + APPLET_VERSION);
			return true;
		}

		String href = getParameter("href");
		if((href != null) && (href.length() > 0)) {
			AppletContext context = getAppletContext();
			String target = getParameter("target");
			if((target == null) || (target.length() <= 0)) { target = "_self"; }
			try {
				URL url = new URL(getDocumentBase(), href);
				context.showDocument(url, target);
			} catch(MalformedURLException e) {
				e.printStackTrace();
			}
		}

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.mouseDown"); }
		return true;
	}
	public boolean mouseEnter(Event evt, int x, int y) {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.mouseEnter"); }

		String href = getParameter("href");
		if((href != null) && (href.length() > 0)) {
			try {
				URL url = new URL(getDocumentBase(), href);
				showStatus(url.toExternalForm());
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			} catch(MalformedURLException e) {
				e.printStackTrace();
			} catch(Throwable e) {
				if(debug_flag) { e.printStackTrace(); }
			}
		}

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.mouseEnter"); }
		return true;
	}
	public boolean mouseExit(Event evt, int x, int y) {
		if(debug_flag) { System.out.println("IN: png.MNG_LC_Player.mouseExit"); }

		String href = getParameter("href");
		if((href != null) && (href.length() > 0)) {
			showStatus("");
			try {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} catch(Throwable e) {
				if(debug_flag) { e.printStackTrace(); }
			}
		}

		if(debug_flag) { System.out.println("OUT: png.MNG_LC_Player.mouseExit"); }
		return true;
	}
}
