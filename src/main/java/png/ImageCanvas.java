package png;/*
* png.ImageCanvas.java -- �摜�\���p�� Canvas �N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.awt.*;

public class ImageCanvas extends Canvas implements Runnable {
	private final boolean debug_flag = false;
	private final int debug_lv = 0;

	private static final String DEFAULT_INITIAL_MESSAGE = "Now loading...";

	private Image offImg;
	private Graphics offG;
	private Thread th;

	private Image img;
	private Animation anim;
	private String msg;

	public ImageCanvas() {
		offImg = null;
		offG = null;
		th = null;

		img = null;
		anim = null;
		msg = null;
	}

	public void validate() {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.validate");
		}
		super.validate();

		//Dimension size = getSize();
		Dimension size = size();
		if(size.width > 0 && size.height > 0) {
			offImg = createImage(size.width, size.height);
			offG = offImg.getGraphics();
			//offG.setClip(0, 0, size.width, size.height);
		}

		if(debug_flag) {
			System.out.println("width :" + size.width);
			System.out.println("height:" + size.height);
			System.out.println("OUT: png.ImageCanvas.validate");
		}
	}
	public void invalidate() {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.invalidate");
		}

		super.invalidate();
		if(offG != null) { offG.dispose(); }
		if(offImg != null) { offImg.flush(); }
		offG = null;
		offImg = null;

		if(debug_flag) {
			System.out.println("OUT: png.ImageCanvas.invalidate");
		}
	}
	public void paint(Graphics g) {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.paint");
		}
		if(offImg == null) { validate(); }

		super.paint(offG);
		if(img != null) { offG.drawImage(img, 0, 0, this); }
		if(msg != null) { paintMessage(offG); }
		if(anim != null) { anim.paintFrame(offG); }
		g.drawImage(offImg, 0, 0, null);

		if(debug_flag) {
			System.out.println("OUT: png.ImageCanvas.paint");
		}
	}
	public void update(Graphics g) {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.update");
		}

		paint(g);

		if(debug_flag) {
			System.out.println("OUT: png.ImageCanvas.update");
		}
	}

	public void showImage(Image i) {
		anim = null;
		msg = null;

		img = i;
		repaint();
	}
	public void playAnimation(Animation a) {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.palyAnimation");
			System.out.println("Animation: " + a);
		}

		img = null;
		msg = DEFAULT_INITIAL_MESSAGE;
		repaint();

		anim = a;
		th = new Thread(this);
		th.start();

		if(debug_flag) {
			System.out.println("OUT: png.ImageCanvas.palyAnimation");
		}
	}
	public synchronized void stopAnimation() {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.stopAnimation");
		}

		th = null;

		if(debug_flag) {
			System.out.println("OUT: png.ImageCanvas.stopAnimation");
		}
	}
	public void run() {
		if(debug_flag) {
			System.out.println("IN: png.ImageCanvas.run");
		}

		Thread current = Thread.currentThread();
		while (current == th) {
			anim.fetchNextFrame();
			if(msg != null) {
				clearCanvas(getGraphics());
				msg = null;
			}
			repaint();
			try {
				Thread.sleep(anim.getInterFrameDelay());
			} catch (InterruptedException e) {
				if(debug_flag) { e.printStackTrace(); }
			}
			if(anim.isEndOfAnimation()) { break; }
	    }
		if(th == null) { anim = null; }

		if(debug_flag) {
			System.out.println("OUT: png.ImageCanvas.run");
		}
	}

	private void paintMessage(Graphics g) {
		int xl = 10, yl = 20;
		clearCanvas(g);

		Color tmp = g.getColor();
		Color bg = getBackground();
		Color fg = new Color(~bg.getRGB() & 0x00FFFFFF);
		g.setColor(fg);
		g.drawString(msg, xl, yl);
		g.setColor(tmp);
	}
	private void clearCanvas(Graphics g) {
		if (g == null)
			return; // damien 23/1/2003 pour IE/WindozeXP
		Color tmp = g.getColor();
		Color bg = getBackground();
		g.setColor(bg);
		//g.fillRect(0, 0, setSize().width, setSize().height);
		g.fillRect(0, 0, size().width, size().height);
		g.setColor(tmp);
	}

	public boolean imageUpdate(Image i, int f, int x, int y, int w, int h) {
		if(debug_flag && (debug_lv > 0)) {
			System.out.println("IN: png.ImageCanvas.iamgeUpdate");
			System.out.println("Image: " + img);
			System.out.println("flag: " + f);
			System.out.println("x: " + x + "; y: " + y);
			System.out.println("width: " + w + "; height: " + h);
			System.out.println("OUT: png.ImageCanvas.iamgeUpdate");
		}

		return super.imageUpdate(i, f, x, y, w, h);
	}
}
