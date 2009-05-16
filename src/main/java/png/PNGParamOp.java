package png;/*
* png.PNGParamOp.java -- PNG �̃p�����[�^�Ɋւ��鑀����󂯎��N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

public class PNGParamOp extends PNGParam {
	private final boolean debug_flag = false;

	private MNGLib mng;

	public boolean readIHDR(int len, byte data[]) {
		width    = PNGFamily.byte2int(data, 0);
		height   = PNGFamily.byte2int(data, 4);
		depth    = data[8];
		color    = data[9];
		compress = data[10];
		filter   = data[11];
		interace = data[12];

		return true;
	}
	public boolean readPLTE(int len, byte data[]) {
		if(len == 0) {
			MNGParam mng_param = null;
			if(mng != null) { mng_param = mng.getInfo(); }
			if((mng_param != null) && (mng_param.p_len > 0)) {
				p_len  = mng_param.p_len;
				pallet = mng_param.pallet;
				t_len  = mng_param.t_len;
				trans  = mng_param.trans;
				return true;
			} else {
				p_len  = 0;
				pallet = null;
				return false;
			}
		}
		if(len % 3 != 0) { return false; }

		p_len  = len / 3;
		pallet = new byte[3][p_len];
		for(int i = 0; i < p_len; i++) {
			pallet[0][i] = data[i * 3];
			pallet[1][i] = data[i * 3 + 1];
			pallet[2][i] = data[i * 3 + 2];
		}

		if(debug_flag) {
			System.out.println("png.PNGParamOp.p_len: " + p_len);
			for(int i = 0; i < p_len; i++) {
				System.out.print(pallet[0][i] + " ");
				System.out.print(pallet[1][i] + " ");
				System.out.println(pallet[2][i]);
			}
		}

		return true;
	}
	public boolean readtRNS(int len, byte data[]) {
		t_len = len;
		trans = new byte[(p_len > len)?p_len:len];
		for(int i = 0; i < len; i++) {
			trans[i] = data[i];
		}
		for(int i = len; i < p_len; i++) {
			// tRNS �`�����N�Ŏw�肳��Ȃ����̂͊��S�ȕs�����F(0xFF)
			trans[i] = (byte)0xFF;
		}

		return true;
	}


	public PNGParamOp() { mng = null; }
	public PNGParamOp(MNGLib ml) { mng = ml; }

	public PNGParam getParameter() {
		PNGParam param = new PNGParam();
		copyParameter(this, param);
		return param;
	}
	protected void copyParameter(PNGParam src, PNGParam dst) {
		copyIHDR(src, dst);
		copyPLTE(src, dst);
		copytRNS(src, dst);
	}
	protected void copyIHDR(PNGParam src, PNGParam dst) {
		dst.width    = src.width;
		dst.height   = src.height;
		dst.depth    = src.depth;
		dst.color    = src.color;
		dst.compress = src.compress;
		dst.filter   = src.filter;
		dst.interace = src.interace;
	}
	protected void copyPLTE(PNGParam src, PNGParam dst) {
		dst.p_len = src.p_len;
		if(src.p_len > 0) {
			dst.pallet = new byte[3][src.p_len];
			for(int i = 0; i < 3; i++) {
				System.arraycopy(src.pallet[i], 0, dst.pallet[i], 0,
				                 src.p_len);
			}
		}
	}
	protected void copytRNS(PNGParam src, PNGParam dst) {
		dst.t_len = src.t_len;
		if(src.t_len > 0) {
			dst.trans = new byte[src.trans.length];
			System.arraycopy(src.trans, 0, dst.trans, 0, src.trans.length);
		}
	}
}
