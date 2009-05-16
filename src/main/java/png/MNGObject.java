package png;/*
* png.MNGParamOp.java -- MNG �̃I�u�W�F�N�g(PNG/JNG)�Ɋւ��鏈�����󂯎��N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;

public class MNGObject {
	private static final boolean debug_flag = false;

	public static final int UNKNOWN = 0;
	public static final int PNG     = 1;
	public static final int JNG     = 2;

	private Object obj;
	private int type;

	protected MNGObject() {}

	public static MNGObject getInstance(int type, MNGLib mng_lib) {
		MNGObject mng_obj = new MNGObject();
		Object obj = newInstance(type, mng_lib);
		if(obj != null) {
			mng_obj.obj = obj;
			mng_obj.type = type;
		}
		return mng_obj;
	}
	protected static Object newInstance(int type, Object mng_lib) {
		String name = new String();
		switch(type) {
			case PNG:
				name = "png.PNGLib";
				break;
			case JNG:
				name = "png.JNGLib";
				break;
			default:
				return null;
		}

		try {
			Class c = Class.forName(name);

			Class arg_types[] = new Class[1];
			Object arg_vals[] = new Object[1];

			arg_types[0] = Class.forName("png.MNGLib");
			arg_vals[0] = mng_lib;

			Constructor co = c.getConstructor(arg_types);
			return co.newInstance(arg_vals);
		} catch(Throwable e) {
			e.printStackTrace();
		}

		return null;
	}
	public Object getMNGObject() { return obj; }

	public Image getImage() {
		switch(type) {
			case PNG:    return getPNGImage(obj);
			case JNG:    return getJNGImage(obj);
			default:     ;
		}
		return null;
	}
	protected static Image getPNGImage(Object png_lib) {
		if(debug_flag) {
			System.out.println("IN: png.MNGObject.getPNGImage");
		}

		try {
			Class c = Class.forName("scene.util.PNGImageProducer");

			Class arg_types[] = new Class[1];
			Object arg_vals[] = new Object[1];

			arg_types[0] = Class.forName("png.PNGLib");
			arg_vals[0] = png_lib;

			Constructor co = c.getConstructor(arg_types);
			Object obj = co.newInstance(arg_vals);

			ImageProducer ip = (ImageProducer)obj;
			return Toolkit.getDefaultToolkit().createImage(ip);
		} catch(Throwable e) {
			e.printStackTrace();
		}

		if(debug_flag) {
			System.out.println("OUT: png.MNGObject.getPNGImage");
		}
		return null;
	}
	protected static Image getJNGImage(Object jng_lib) {
		if(debug_flag) {
			System.out.println("IN: png.MNGObject.getJNGImage");
		}

		try {
			Class c = Class.forName("JNGImageDecoder");

			Class arg_types[] = new Class[1];
			Object arg_vals[] = new Object[1];

			arg_types[0] = Class.forName("png.JNGLib");
			arg_vals[0] = jng_lib;

			Method m = c.getMethod("getImage", arg_types);
			Object obj = m.invoke(null, arg_vals);

			if(obj != null) { return (Image)obj; }
		} catch(Throwable e) {
			e.printStackTrace();
		}

		if(debug_flag) {
			System.out.println("OUT: png.MNGObject.getJNGImage");
		}
		return null;
	}

	public static Image getImage(File file) {
		return getImage((Object)file);
	}
	public static Image getImage(URL url) {
		return getImage((Object)url);
	}
	protected static Image getImage(Object obj) {
		Class arg_types[] = new Class[1];
		Object arg_vals[] = new Object[1];

		arg_types[0] = obj.getClass();
		arg_vals[0] = obj;

		try {
			switch(detectType(obj)) {
				case PNG:
					Class c = Class.forName("png.PNGLib");
					Constructor co = c.getConstructor(arg_types);
					Object png = co.newInstance(arg_vals);
					return getPNGImage(png);
				case JNG:
					c = Class.forName("png.JNGLib");
					co = c.getConstructor(arg_types);
					Object jng = co.newInstance(arg_vals);
					return getJNGImage(jng);
				default:
					;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	protected static int detectType(Object obj) {
		if(!(obj instanceof File) && !(obj instanceof URL)) {
			return UNKNOWN;
		}

		Class arg_types[] = new Class[1];
		Object arg_vals[] = new Object[1];

		arg_types[0] = obj.getClass();
		arg_vals[0] = obj;

		try {
			Class c = Class.forName("png.PNGLib");
			Method m = c.getMethod("checkSignature", arg_types);
			Object ret = m.invoke(null, arg_vals);
			boolean flag = ((Boolean)ret).booleanValue();
			if(flag) { return PNG; }
		} catch(Exception e) {}

		try {
			Class c = Class.forName("png.JNGLib");
			Method m = c.getMethod("checkSignature", arg_types);
			Object ret = m.invoke(null, arg_vals);
			boolean flag = ((Boolean)ret).booleanValue();
			if(flag) { return JNG; }
		} catch(Exception e) {}

		return UNKNOWN;
	}
}
