package png;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

abstract class JNGImageDecoder {
	private static final boolean debug_flag = false;
	private static final int debug_lv = 0;
	private static int debug_jng_no = 0;
	private static int debug_no_max = 0;

	public static Image getImage(JNGLib jng) {
		if(debug_flag) { System.out.println("IN: JNGImageDecoder.getImage"); }

		try {
			if(!jng.existsStream() && !jng.isEmbeded()) {
				jng.openStream();
				if(!jng.existsStream()) { return null; }
			}
			if(!jng.readInfo() || !jng.isSupported()) {
				if(!jng.isEmbeded()) { jng.closeStream(); }
				return null;
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		byte img_data[] = new byte[0];
		try {
			while(!jng.isEOS()) {
				byte buf[] = new byte[1024];
				int rbytes = jng.readJDAT(buf, 0, buf.length);
				if(rbytes < 0) { break; }
				if(rbytes > 0) {
					img_data = bytecat(img_data, 0, img_data.length,
					                   buf,      0, rbytes);
				}
			}
			if(debug_flag && (debug_jng_no < debug_no_max)) {
				String name = "jng" + debug_jng_no + ".jpg";
				OutputStream out = new FileOutputStream(name);
				out.write(img_data, 0, img_data.length);
				out.flush();
				out.close();
				debug_jng_no++;
			}
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		try {
			if(!jng.isEmbeded()) { jng.closeStream(); }
		} catch(IOException e) {
			e.printStackTrace();
		}

		Toolkit tk = Toolkit.getDefaultToolkit();
		Image img = tk.createImage(img_data, 0, img_data.length);

		JNGParam param = jng.getInfo();
		MediaTracker mt = new MediaTracker(new Canvas());
		mt.addImage(img, 0);
		tk.prepareImage(img, param.width, param.height, null);
		try { mt.waitForID(0); } catch(InterruptedException e) {}

		if(debug_flag) {
			System.out.println("checkAll  : " + mt.checkAll());
			System.out.println("isErrorAny: " + mt.isErrorAny());
		}

		//mt.removeImage(img);

		if(debug_flag) {
			if(debug_lv > 1) {
				debug_printPixels(img, param.width, param.height);
			}
			System.out.println("OUT: JNGImageDecoder.getImage");
		}

		return img;
	}
	protected static byte[] bytecat(byte b1[], int ofs1, int len1,
	                                byte b2[], int ofs2, int len2) {
		byte b[] = new byte[len1 + len2];
		System.arraycopy(b1, ofs1, b, 0,    len1);
		System.arraycopy(b2, ofs2, b, len1, len2);
		return b;
	}

	private static void debug_printPixels(Image img, int width, int height) {
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, false);

		try {
			pg.grabPixels();
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("status:" + pg.getStatus());

		Object obj = pg.getPixels();
		int w = pg.getWidth();
		int h = pg.getHeight();
		ColorModel cm = pg.getColorModel();

		if(obj instanceof byte[]) {
			debug_printBytePixels((byte[])obj, w, cm);
		} else if(obj instanceof int[]) {
			debug_printIntPixels((int[])obj, w, cm);
		}
	}
	private static void debug_printBytePixels(byte pix[], int w,
	                                          ColorModel cm) {
		for(int i = 0; i < pix.length; i++) {
			long val = cm.getRGB(pix[i] & 0xFF) & 0xFFFFFFFFL;
			System.out.print(Long.toString(val, 16));
			if((i + 1) % w == 0) {
				System.out.println();
			} else {
				System.out.print(" ");
			}
		}
	}
	private static void debug_printIntPixels(int pix[], int w,
	                                         ColorModel cm) {
		for(int i = 0; i < pix.length; i++) {
			long val = cm.getRGB(pix[i]) & 0xFFFFFFFFL;
			System.out.print(Long.toString(val, 16));
			if((i + 1) % w == 0) {
				System.out.println();
			} else {
				System.out.print(" ");
			}
		}
	}
}
