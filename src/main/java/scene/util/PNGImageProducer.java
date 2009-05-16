package scene.util;


import png.ImageConsumerGroup;
import png.PNGLib;
import png.PNGParam;
import png.TransDirectColorModel;

import java.awt.image.*;
import java.util.zip.*;
import java.io.*;

public class PNGImageProducer implements ImageProducer {
	private final boolean debug_flag = false;
	private final int debug_lv = 0;

	private ImageConsumerGroup icg;
	private PNGLib png;
	private PNGParam param;

	public PNGImageProducer(PNGLib pl) {
		icg = new ImageConsumerGroup();
		png = pl;
		param = null;
	}

	public void addConsumer(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: scene.util.PNGImageProducer.addConsumer");
			System.out.println("ic: " + ic);
		}

		icg.addConsumer(ic);

		if(debug_flag) {
			System.out.println("OUT: scene.util.PNGImageProducer.addConsumer");
		}
	}
	public boolean isConsumer(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: scene.util.PNGImageProducer.isConsumer");
			System.out.println("ic: " + ic);
		}

		return icg.isConsumer(ic);
	}
	public void removeConsumer(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: scene.util.PNGImageProducer.removeConsumer");
			System.out.println("ic: " + ic);
		}

		icg.removeConsumer(ic);

		if(debug_flag) {
			System.out.println("OUT: scene.util.PNGImageProducer.removeConsumer");
		}
	}
	public void startProduction(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println("IN: scene.util.PNGImageProducer.startProduction");
			System.out.println("ic: " + ic);
		}

		icg.addConsumer(ic);
		if(sendImage()) {
			icg.imageComplete(ImageConsumer.STATICIMAGEDONE);
		} else {
			icg.imageComplete(ImageConsumer.IMAGEERROR);
		}

		if(debug_flag) {
			System.out.println("OUT: scene.util.PNGImageProducer.startProduction");
		}
	}
	public void requestTopDownLeftRightResend(ImageConsumer ic) {
		if(debug_flag) {
			System.out.println(
				"IN: scene.util.PNGImageProducer.requestTopDownLeftRightResend"
			);
			System.out.println("ic: " + ic);
		}

		//icg.setHints(ImageConsumer.TOPDOWNLEFTRIGHT);

		if(debug_flag) {
			System.out.println(
				"OUT: scene.util.PNGImageProducer.requestTopDownLeftRightResend"
			);
		}
	}

	private boolean sendImage() {
		if(debug_flag) {
			System.out.println("IN: scene.util.PNGImageProducer.sendImage");
		}

		try {
			if(!png.existsStream() && !png.isEmbeded()) {
				png.openStream();
				if(!png.existsStream()) { return false; }
			}
			if(!png.readInfo() || !png.isSupported()) {
				png.closeStream();
				return false;
			}
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		param = png.getInfo();

		icg.setDimensions(param.width, param.height);

		ColorModel cm = getColorModel(param.color, param.depth);
		if(cm == null) {
			try { png.closeStream(); }
			catch(IOException e) { e.printStackTrace(); }
			return false;
		}

		icg.setColorModel(cm); // BUG FIX !!!

		if(param.interace == 0) {
			icg.setHints(ImageConsumer.TOPDOWNLEFTRIGHT);
		}

		try{
			for(int h = 0; (h < param.height) && !png.isEOS(); h++) {
				if(debug_flag && (debug_lv > 0)) { System.out.println("h = " + h); }

				byte buf[] = png.readLine();
				if(buf == null) { break; }

				if(!sendLine(0, h, cm, buf)) {
					png.closeStream();
					return false;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} catch(DataFormatException e) {
			e.printStackTrace();
			return false;
		}

		if(debug_flag) {
			System.out.println("OUT: scene.util.PNGImageProducer.sendImage");
		}

		try { png.closeStream(); }
		catch(IOException e) { e.printStackTrace(); }
		return true;
	}
	private boolean sendLine(int x, int y, ColorModel cm, byte[] buf) {
 		int bits = param.depth * png.getSamplePerPixel();
		if(debug_flag && (debug_lv > 1))
			System.out.println("bits="+bits);
		if(bits == 8) {
			if(debug_flag && (debug_lv > 1)) {
				System.out.println("cm: " + cm);
				System.out.print("buf: " );
				for (int i=0; i<param.width; i++)
					System.out.print(buf[i] + " ");
				System.out.println();
				System.out.println("icg.setPixels("+x+", "+y+", "+param.width+", 1, cm, buf, 0, "+param.width+")");
			}
			icg.setPixels(x, y, param.width, 1, cm, buf, 0, param.width);
		} else if(bits < 8) {
			byte mask = (byte)0x80;
			byte pix[] = new byte[param.width];
			for(int i = 0; i < param.width; i++) {
				pix[i] = 0;
				for(int j = 0; j < bits; j++) {
					// 対象のビットが含まれる配列の添え字を求める。
					int idx = (i * bits + j) / 8;

					pix[i] <<= 1;
					pix[i] |= ((buf[idx] & mask) == 0)?0:1;

					mask = (byte)((mask & 0xFF) >>> 1);
					if(mask == 0) { mask = (byte)0x80; }
				}
			}

			if(debug_flag && (debug_lv > 1)) {
				System.out.println("pixcel:");
				for(int i = 0; i < param.width; i++) {
					System.out.print(Integer.toString(pix[i] & 0xFF, 16)
					                        .toUpperCase() + " ");
				}
				System.out.println();
			}

			icg.setPixels(x, y, param.width, 1, cm, pix, 0, param.width);
		} else if(bits % 8 == 0) {
			int bpp = bits / 8;
			int pix[] = new int[param.width];
			for(int i = 0; i < param.width; i++) {
				pix[i] = toPixel(buf, i * bpp, bpp);
			}

			if(debug_flag && (debug_lv > 1)) {
				System.out.println("pixcel:");
				for(int i = 0; i < param.width; i++) {
					System.out.print(Long.toString(pix[i] & 0xFFFFFFFFL, 16)
					                     .toUpperCase() + " ");
				}
				System.out.println();
			}

			icg.setPixels(x, y, param.width, 1, cm, pix, 0, param.width);
		} else {
			return false;
		}

		return true;
	}
	private ColorModel getColorModel(int color, int depth) {
		ColorModel cm;

		if(debug_flag) {
			System.out.println("getColorModel color=" + color + " depth=" + depth);
		}
		switch(color) {
			case 0:
				switch(depth) {
					case 1:
						cm = new DirectColorModel(1, 0x01, 0x01, 0x01);
						break;
					case 2:
						cm = new DirectColorModel(2, 0x03, 0x03, 0x03);
						break;
					case 4:
						cm = new DirectColorModel(4, 0x0F, 0x0F, 0x0F);
						break;
					case 8:
						cm = new DirectColorModel(8, 0xFF, 0xFF, 0xFF);
						break;
					default:
						return null;
				}
				if(param.t_len == 2) {
					cm = new TransDirectColorModel((DirectColorModel)cm,
					                               (param.trans[1] & 0xFF));
				}
				break;
			case 2:
				cm = new DirectColorModel(24, 0xFF0000, 0x00FF00, 0x0000FF);
				if(param.t_len == 6) {
					int trans = (param.trans[1] & 0xFF) << 16
					          | (param.trans[3] & 0xFF) << 8
				              | (param.trans[5] & 0xFF);
					cm = new TransDirectColorModel((DirectColorModel)cm,
					                               trans);
				}
				break;
			case 3:
				if(param.p_len <= 0) { return null; }
				if(debug_flag)
					System.out.println("getColorModel param.t_len: " + param.t_len);
				if(param.t_len > 0) {
					if(debug_flag) {
						System.out.println("new IndexColorModel (" + depth + ", " + param.p_len + ", param.pallet[0]"+
							", param.pallet[1], param.pallet[2], param.trans)");
						System.out.print("trans: ");
						for (int i=0; i<param.p_len; i++)
							System.out.print(i+":"+Integer.toString(param.trans[i] & 0xFF, 16)
					                        .toUpperCase() + " ");
						System.out.println();
					}
					//cm = new IndexColorModel(depth, param.p_len,
					cm = new IndexColorModel(8, param.p_len, // BUG WORKAROUND
					                                param.pallet[0],
					                                param.pallet[1],
					                                param.pallet[2],
					                                param.trans);
				} else {
					cm = new IndexColorModel(depth, param.p_len,
					                                param.pallet[0],
					                                param.pallet[1],
					                                param.pallet[2]);
				}
				break;
			case 4:
				cm = new DirectColorModel(16, 0x00FF, 0x00FF, 0x00FF, 0xFF00);
				break;
			case 6:
				if(debug_flag)
					System.out.println("DirectColorModel(32, 0xFF000000, 0x00FF0000,0x0000FF00, 0x000000FF)");
				//cm = new DirectColorModel(32, 0xFF000000, 0x00FF000000,
				//                          0x0000FF00, 0x000000FF);
				// BUG FIX
				cm = new DirectColorModel(32, 0xFF000000, 0x00FF0000,
				                          0x0000FF00, 0x000000FF);
				break;
			default:
				return null;
		}

		return cm;
	}
	private int toPixel(byte buf[], int offset, int bpp) {
		int pix = 0;

		if(bpp <= 4) {
			for(int i = 0; i < bpp; i++) {
				pix <<= 8;
				pix |= (buf[i + offset] & 0xFF);
			}
		} else {
			for(int i = 0; i < bpp; i += (bpp / 3)) {
				pix <<= 8;
				pix |= (buf[i + offset] & 0xFF);
			}
		}

		return pix;
	}
}
