package png;/*
* png.TransDirectColorModel.java -- �����F������ DirectColorModel
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

public class TransDirectColorModel extends ColorModel {
	private final boolean debug_flag = false;
	private final int debug_lv = 0;

	private DirectColorModel model;
	private int trans;

	public TransDirectColorModel(DirectColorModel cm, int t) {
		super(cm.getPixelSize());
		model = cm;
		trans = t;
	}

	public int getRedMask() {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getRedMask");
		}
		return model.getRedMask();
	}
	public int getGreenMask() {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getGreenMask");
		}
		return model.getGreenMask();
	}
	public int getBlueMask() {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getBlueMask");
		}
		return model.getBlueMask();
	}
	public int getAlphaMask() {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getAlphaMask");
		}
		return model.getAlphaMask();
	}
	public int getRed(int pixel) {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getRed");
		}
		return model.getRed(pixel);
	}
	public int getGreen(int pixel) {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getGreen");
		}
		return model.getGreen(pixel);
	}
	public int getBlue(int pixel) {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getBlue");
		}
		return model.getBlue(pixel);
	}
	public int getAlpha(int pixel) {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getAlpha");
		}
		if(pixel == trans) { return 0; }
		return model.getAlpha(pixel);
	}
	public int getRGB(int pixel) {
		if(debug_flag) {
			System.out.println("IN: png.TransDirectColorModel.getRGB");
			if(debug_lv > 0) {
				System.out.println("pixel: " + Long.toString(pixel, 16));
				System.out.println("trans: " + Long.toString(trans, 16));
			}
		}

		int rgb = model.getRGB(pixel);

		if(debug_flag & (debug_lv > 0)) {
			System.out.println("rgb(before): "
			                   + Long.toString(rgb & 0xFFFFFFFFL, 16));
		}

		if(pixel == trans) { rgb &= 0x00FFFFFF; }

		if(debug_flag & (debug_lv > 0)) {
			System.out.println("rgb(after): "
			                   + Long.toString(rgb & 0xFFFFFFFFL, 16));
		}

		if(debug_flag) {
			System.out.println("OUT: png.TransDirectColorModel.getRGB");
		}

		return rgb;
	}
}
