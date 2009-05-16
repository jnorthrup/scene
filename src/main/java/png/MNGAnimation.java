package png;/*
* png.MNGAnimation.java -- MNG �p�� Animation �N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.io.*;
import java.net.*;
import java.awt.*;

public class MNGAnimation extends Animation {
	private final boolean debug_flag = false;
	private final int debug_lv = 0;

	private MNGLib mng;
	private MNGParam param;

	private Image screen;
	private Graphics screenG;
	private MediaTracker mt;
	private Color bg;

	private long counter;
	private long delay;
	private boolean eoa;
	private boolean need_setup;

	public MNGAnimation(URL url) {
		this(url, false);
	}
	public MNGAnimation(URL url, boolean flag_cache) {
		initialize();
		if(MNGLib.checkSignature(url)) {
			mng = new MNGLib(url);
			mng.setCaching(flag_cache);
		} else {
			eoa = true;
			screen = MNGObject.getImage(url);
			if(screen != null) {
				mt = new MediaTracker(new Canvas());
				mt.addImage(screen, 0);
			}
		}
	}
	public MNGAnimation(File file) {
		initialize();
		if(MNGLib.checkSignature(file)) {
			mng = new MNGLib(file);
		} else {
			eoa = true;
			screen = MNGObject.getImage(file);
			if(screen != null) {
				mt = new MediaTracker(new Canvas());
				mt.addImage(screen, 0);
			}
		}
	}
	private void initialize() {
		mng = null;
		param = null;

		screen = null;
		screenG = null;
		mt = null;
		bg = Color.white;

		counter = 0;
		delay = 0;
		eoa = false;
		need_setup = true;
	}

	public void setDefaultBackground(Color c) { bg = c; }

	public void paintFrame(Graphics g) {
		if((screen == null) || (mt == null)) { return; }
		try { mt.waitForID(0); } catch(InterruptedException e) {}
		g.drawImage(screen, 0, 0, null);
	}
	public void fetchNextFrame() {
		if(eoa) { return; }
		try {
			if(need_setup) {
				if(!setup()) { eoa = true; return; }
			}
			if(!drawNextFrame()) { eoa = true; }
		} catch(IOException e) { eoa = true; }
	}
	public long getInterFrameDelay() { return delay; }
	public boolean isEndOfAnimation() { return eoa; }

	private boolean drawNextFrame() throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.MNGAnimation.drawNextFrame");
		}

		while(!mng.isEOS()) {
			// ���ߍ��݉摜��T���BFRAM, DEFI �̓ǂݍ��݂������
			MNGObject mng_obj = mng.getMNGObject();
			param = mng.getInfo();

			// �t���[���̊����ƃt���[���Ԃ̒x��(inter-frame delay)
			if((param.tps != 0) && mng.wasFRAM()
			    && ((param.before_f_mode == 2) || (param.before_f_mode == 4))) {
				//** 4/10 Furumizo, �ύX
				//** 5/17 Ikeda,    �ύX
				delay = (long)((1.0 / param.tps) * 1000.0 * param.f_delay);
				return true;
			}

			// ���ߍ��݉摜��������Ȃ������Ƃ��̓��[�v���I������
			if(mng_obj == null) { break; }

			Image img = mng_obj.getImage();
			if(img == null) { continue; }

			// �X�N���[����w�i�ŃN���A����
			//** 4/10 Furumizo �ǉ�
			//** 5/12 Ikeda    �ύX
			if((param.f_mode == 3) || ((param.f_mode == 4) && mng.wasFRAM())) {
				screenG.fillRect(0, 0, param.width, param.height);
			}

			// ���ߍ��݉摜���X�N���[���ɕ`�悷��
			screenG.drawImage(img, param.x_location, param.y_location, null);

			// �t���[���̊����ƃt���[���Ԃ̒x��(inter-frame delay)
			if((param.tps != 0) && ((param.f_mode == 1) || (param.f_mode == 3))) {
				//** 4/10 Furumizo, �ύX
				//** 5/17 Ikeda,    �ύX
				delay = (long)((1.0 / param.tps) * 1000.0 * param.f_delay);
				return true;
			}
		}

		if(debug_flag) {
			System.out.println("OUT: png.MNGAnimation.drawNextFrame");
		}

		return termination();
	}
	private boolean termination() throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.MNGAnimation.tremination");
		}

		mng.closeStream();
		need_setup = true;
		delay = 0;

		if(param.tps == 0) { return false; }
		if((param.action == 3) && !(debug_flag && (debug_lv > 0))) {
			if((counter < param.limit) || (param.limit == 0)) {
				//** 4/10 Furumizo �ύX
				//** 5/12 Ikeda    �ύX
				if(param.delay - param.f_delay > 0) { // �ŏI�x���̏d�Ȃ�h�~
					delay = (long)((1.0 / param.tps) * 1000.0
					               * (param.delay - param.f_delay));
				}
				counter += (param.limit == 0)?0:1;
				return true;
			}
		}
		if((param.action == 2) || (param.after == 2)) {
			fetchNextFrame();
			mng.closeStream();
		}
		if((param.action == 1) || (param.after == 1)) {
			screenG.fillRect(0, 0, param.width, param.height);
		}

		if(debug_flag) {
			System.out.println("OUT: png.MNGAnimation.termination");
		}

		return false;
	}
	private boolean setup() throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.MNGAnimation.setup");
		}

		mng.openStream();
		if(!mng.existsStream()) { return false; }

		if(!mng.readInfo()) {
			mng.closeStream();
			return false;
		}
		param = mng.getInfo();

		screen = createImage(param.width, param.height);
		screenG = screen.getGraphics();
		mt = new MediaTracker(new Canvas());
		mt.addImage(screen, 0);

		if(param.back != null) {
			/*
			int red   = param.back[0] & 0x00FF;
			int green = param.back[1] & 0x00FF;
		    int blue  = param.back[2] & 0x00FF;
			*/
			// another bug ?
			int red   = (param.back[0] >> 8) & 0xFF;
			int green = (param.back[1] >> 8) & 0xFF;
		    int blue  = (param.back[2] >> 8) & 0xFF;
			bg = new Color(red, green, blue);
		}
		screenG.setColor(bg);
		screenG.fillRect(0, 0, param.width, param.height);

		if(debug_flag) {
			System.out.println("OUT: png.MNGAnimation.setup");
		}

		need_setup = false;
		return true;
	}
	private Image createImage(int width, int height) {
		Frame frm = new Frame();
		frm.pack();
		Image img = frm.createImage(width, height);
		frm.dispose();
		frm = null;
		return img;
	}
}
