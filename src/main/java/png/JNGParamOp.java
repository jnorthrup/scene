package png;/*
* png.JNGParamOp.java -- JNG �̃p�����[�^�Ɋւ��鏈�����󂯎��N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

public class JNGParamOp extends JNGParam {
	public boolean readJHDR(int len, byte data[]) {
		width      = PNGFamily.byte2int(data, 0);
		height     = PNGFamily.byte2int(data, 4);
		color      = data[8];
		depth      = data[9];
		compress   = data[10];
		interace   = data[11];
		a_depth    = data[12];
		a_compress = data[13];
		a_filter   = data[14];
		a_interace = data[15];

		return true;
	}

	public JNGParam getParameter() {
		JNGParam param = new JNGParam();
		copyParameter(this, param);
		return param;
	}
	protected void copyParameter(JNGParam src, JNGParam dst) {
		copyJHDR(src, dst);
	}
	protected void copyJHDR(JNGParam src, JNGParam dst) {
		dst.width      = src.width;
		dst.height     = src.height;
		dst.color      = src.color;
		dst.depth      = src.depth;
		dst.compress   = src.compress;
		dst.interace   = src.interace;
		dst.a_depth    = src.a_depth;
		dst.a_compress = src.a_compress;
		dst.a_filter   = src.a_filter;
		dst.a_interace = src.a_interace;
	}
}
