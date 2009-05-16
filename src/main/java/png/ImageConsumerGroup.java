package png;/*
* png.ImageConsumerGroup.java -- ������ ImageConsumer ����܂Ƃ߂Ɉ������߂̃N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.awt.image.*;
import java.util.*;

public class ImageConsumerGroup implements ImageConsumer {
	private final boolean debug_flag = false;
	private final int debug_lv = 0;

	private Vector icg;

	public ImageConsumerGroup() {
		icg = new Vector();
	}

	public void addConsumer(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.addConsumer");
			System.out.println("ic: " + ic);
			System.out.println("icg_size(before): " + icg.size());
		}

		if(!icg.contains(ic)) { icg.addElement(ic); }

		if(debug_flag) {
			System.out.println("icg_size(after): " + icg.size());
			System.out.println("OUT: png.ImageConsumerGroup.addConsumer");
		}
	}
	public boolean isConsumer(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.isConsumer");
			System.out.println("ic: " + ic);
		}

		return icg.contains(ic);
	}
	public void removeConsumer(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.removeConsumer");
			System.out.println("ic: " + ic);
			System.out.println("icg_size(before): " + icg.size());
		}

		if(icg.contains(ic)) { icg.removeElement(ic); }

		if(debug_flag) {
			System.out.println("icg_size(after): " + icg.size());
			System.out.println("OUT: png.ImageConsumerGroup.removeConsumer");
		}
	}

	public void setDimensions(int width, int height) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.setDimensions");
			System.out.println("width :" + width);
			System.out.println("height:" + height);
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.setDimensions(width, height);
		}

		if(debug_flag) {
			System.out.println("OUT: png.ImageConsumerGroup.setDimensions");
		}
	}
	public void setProperties(Hashtable props) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.setProperties");
			System.out.println("props:" + props);
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.setProperties(props);
		}

		if(debug_flag) {
			System.out.println("OUT: png.ImageConsumerGroup.setProperties");
		}
	}
	public void setColorModel(ColorModel model) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.setColorModel");
			System.out.println("model:" + model);
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.setColorModel(model);
		}

		if(debug_flag) {
			System.out.println("OUT: png.ImageConsumerGroup.setColorModel");
		}
	}
	public void setHints(int hintflags) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.setHints");
			System.out.println("hintflags:" + hintflags);
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.setHints(hintflags);
		}

		if(debug_flag) {
			System.out.println("OUT: png.ImageConsumerGroup.setHints");
		}
	}
	public void setPixels(int x, int y, int w, int h,
	                             ColorModel model,
	                             byte pixels[], int off, int scansize) {
		if(debug_flag && (debug_lv > 0)) {
			System.out.println("IN: png.ImageConsumerGroup.setPixels(byte)");
		}
		if(debug_flag && (debug_lv == 1)) {
			System.out.println("x: " + x + " y: " + y);
			System.out.println("w: " + w + " h: " + h);
			System.out.println("model: " + model);
			System.out.println("pixels is not null: " + (pixels != null));
			if(pixels != null) {
				System.out.println("pixels.length: " + pixels.length);
			}
			System.out.println("off: " + off + " scansize: " + scansize);
		}
		if(debug_flag && (debug_lv > 1)) {
			for(int j = 0; j < h; j++) {
				for(int i = 0; i < w; i++) {
					int idx = j * scansize + i + off;
					long pix = model.getRGB(pixels[idx] & 0xFF) & 0xFFFFFFFFL;
					System.out.print(Long.toString(pix, 16).toUpperCase());
					System.out.print(" ");
				}
				System.out.println();
			}
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.setPixels(x, y, w, h, model, pixels, off, scansize);
		}

		if(debug_flag && (debug_lv > 0)) {
			System.out.println("OUT: png.ImageConsumerGroup.setPixels(byte)");
		}
	}
	public void setPixels(int x, int y, int w, int h,
	                             ColorModel model,
	                             int pixels[], int off, int scansize) {
		if(debug_flag && (debug_lv > 0)) {
			System.out.println("IN: png.ImageConsumerGroup.setPixels(int)");
		}
		if(debug_flag && (debug_lv == 1)) {
			System.out.println("x: " + x + " y: " + y);
			System.out.println("w: " + w + " h: " + h);
			System.out.println("model: " + model);
			System.out.println("pixels is not null: " + (pixels != null));
			if(pixels != null) {
				System.out.println("pixels.length: " + pixels.length);
			}
			System.out.println("off: " + off + " scansize: " + scansize);
		}
		if(debug_flag && (debug_lv > 1)) {
			for(int j = 0; j < h; j++) {
				for(int i = 0; i < w; i++) {
					int idx = j * scansize + i + off;
					long pix = model.getRGB(pixels[idx]) & 0xFFFFFFFFL;
					System.out.print(Long.toString(pix, 16).toUpperCase());
					System.out.print(" ");
				}
				System.out.println();
			}
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.setPixels(x, y, w, h, model, pixels, off, scansize);
		}

		if(debug_flag && (debug_lv > 0)) {
			System.out.println("OUT: png.ImageConsumerGroup.setPixels(int)");
		}
	}
	public void imageComplete(int status) {
		if(debug_flag) {
			System.out.println("IN: png.ImageConsumerGroup.imageComplete");
			if (status == ImageConsumer.SINGLEFRAME)
				System.out.println("status: SINGLEFRAME");
			else if (status == ImageConsumer.IMAGEERROR)
				System.out.println("status: IMAGEERROR");
			else if (status == ImageConsumer.SINGLEFRAMEDONE)
				System.out.println("status: SINGLEFRAMEDONE");
			else if (status == ImageConsumer.STATICIMAGEDONE)
				System.out.println("status: STATICIMAGEDONE");
			else if (status == ImageConsumer.IMAGEABORTED)
				System.out.println("status: IMAGEABORTED");
		}

		Enumeration ics = icg.elements();
		while(ics.hasMoreElements()) {
			ImageConsumer ic = (ImageConsumer)ics.nextElement();
			ic.imageComplete(status);
		}

		if(debug_flag) {
			System.out.println("OUT: png.ImageConsumerGroup.imageComplete");
		}
	}

	private void setDefaultColorModelPixels(int x, int y, int w, int h,
	                                        ColorModel model,
	                            byte pixels[], int off, int scansize) {
		int pix[] = new int[pixels.length];
		for(int i = 0; i < pixels.length; i++) {
			pix[i] = model.getRGB(pixels[i] & 0xFF);
		}
		ColorModel cm = ColorModel.getRGBdefault();
		setPixels(x, y, w, h, cm, pix, off, scansize);
	}
	private void setDefaultColorModelPixels(int x, int y, int w, int h,
	                                        ColorModel model,
	                            int pixels[], int off, int scansize) {
		int pix[] = new int[pixels.length];
		for(int i = 0; i < pixels.length; i++) {
			pix[i] = model.getRGB(pixels[i]);
		}
		ColorModel cm = ColorModel.getRGBdefault();
		setPixels(x, y, w, h, cm, pix, off, scansize);
	}
}
