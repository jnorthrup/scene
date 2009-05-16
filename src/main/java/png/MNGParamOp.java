package png;/*
* png.MNGParamOp.java -- MNG �̃p�����[�^�Ɋւ��鏈�����󂯎��N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

public class MNGParamOp extends MNGParam {
	private final boolean debug_flag = false;

	public boolean readMHDR(int len, byte data[]) {
		width   = PNGFamily.byte2int(data, 0);
		height  = PNGFamily.byte2int(data, 4);
		tps     = PNGFamily.byte2int(data, 8);
		layers  = PNGFamily.byte2int(data, 12);
		frames  = PNGFamily.byte2int(data, 16);
		times   = PNGFamily.byte2int(data, 20);
		profile = PNGFamily.byte2int(data, 24);

		return true;
	}
	public boolean readBACK(int len, byte data[]) {
		back = new short[3];
		back[0] = PNGFamily.byte2short(data, 0);
		back[1] = PNGFamily.byte2short(data, 2);
		back[2] = PNGFamily.byte2short(data, 4);

		if(len > 6) {
			back_flag = data[6];
		}

		return true;
	}
	public boolean readTERM(int len, byte data[]) {
		action = data[0];
		if(data[0] == 3) {
			after = data[1];
			delay = PNGFamily.byte2int(data, 2);
			limit = PNGFamily.byte2int(data, 6);
		}

		return true;
	}
	public boolean readPLTEg(int len, byte data[]) {
		if(len % 3 != 0) { return false; }

		p_len = len / 3;
		pallet = new byte[3][p_len];
		for(int i = 0; i < p_len; i++) {
			pallet[0][i] = data[i * 3];
			pallet[1][i] = data[i * 3 + 1];
			pallet[2][i] = data[i * 3 + 2];
		}

		if(debug_flag) {
			System.out.println("png.MNGParamOp.p_len: " + p_len);
			for(int i = 0; i < p_len; i++) {
				System.out.print(i + " : " + pallet[0][i] + " ");
				System.out.print(pallet[1][i] + " ");
				System.out.println(pallet[2][i]);
			}
		}

		return true;
	}
	public boolean readtRNSg(int len, byte data[]) {
		t_len = len;
		trans = new byte[(p_len > len)?p_len:len];
		for(int i = 0; i < len; i++) {
			trans[i] = data[i];
		}
		for(int i = len; i < p_len; i++) {
			trans[i] = (byte)0xFF;
		}

		if(debug_flag) {
			System.out.println("png.MNGParamOp.readtRNSg: " );
			System.out.println("t_len=" + t_len + "  p_len=" + p_len );
			for(int i = 0; i < t_len; i++)
				System.out.println(i + " : " + trans[i]);
		}
		return true;
	}
	public boolean readDEFI(int len, byte data[]) {
		setDefaultDEFI();

		oid = PNGFamily.byte2short(data, 0);
		if(len > 2) { invisible = data[2]; }
		if(len > 3) { delta_png = data[3]; }
		if(len > 4) {
			x_location = PNGFamily.byte2int(data, 4);
			y_location = PNGFamily.byte2int(data, 8);
		}
		if(len > 12) {
			cb_left   = PNGFamily.byte2int(data, 12);
			cb_right  = PNGFamily.byte2int(data, 16);
			cb_top    = PNGFamily.byte2int(data, 20);
			cb_bottom = PNGFamily.byte2int(data, 24);
		}

		return true;
	}
	public boolean readFRAM(int len, byte data[]) {
		//** 4/10 Furumizo, FRAM �`�����N�ǎ惋�[�`���ǉ�
		//** 5/12 Ikeda,    ���

		if(data[0] != 0) {
			before_f_mode = f_mode;
			f_mode = data[0];
		}

		if(len <= 1) {
			sf_name = null;
			return true;
		}

		int name_len = 0;
		for(int i = 1; (data[i] != 0) && (i < len); i++) {
			++name_len;
		}
		if(name_len > 80) { return false; }

		if(name_len > 0) {
			sf_name = new byte[name_len];
			System.arraycopy(data, 1, sf_name, 0, name_len);
		} else {
			sf_name = null;
		}

		if(len <= name_len + 1) {
			return true;
		}

		int pos = name_len + 2;

		flag_f_delay = data[pos];
		flag_term    = data[pos + 1];
		flag_sfb     = data[pos + 2];
		flag_sync_id = data[pos + 3];
		pos += 4;

		if(flag_f_delay != 0) {
			f_delay = PNGFamily.byte2int(data, pos);
			pos += 4;
		}
		if(flag_term != 0) {
			timeout = PNGFamily.byte2int(data, pos);
			pos += 4;
		}
		if(flag_sfb != 0) {
			flag_diff_sfb = data[pos];
			sfb_left      = PNGFamily.byte2int(data, pos + 1);
			sfb_right     = PNGFamily.byte2int(data, pos + 5);
			sfb_top       = PNGFamily.byte2int(data, pos + 9);
			sfb_bottom    = PNGFamily.byte2int(data, pos + 13);
			pos += 17;

			if(flag_diff_sfb == 1) {
				cb_left   += sfb_left;
				cb_right  += sfb_right;
				cb_top    += sfb_top;
				cb_bottom += sfb_bottom;
			} else {
				cb_left   = sfb_left;
				cb_right  = sfb_right;
				cb_top    = sfb_top;
				cb_bottom = sfb_bottom;
			}
		}
		if(flag_sync_id != 0) {
			int id_cnt = len - pos / 4;
			if(id_cnt == 0) {
				sync_id = null;
				return true;
			}
			sync_id = new int[id_cnt];
			for(int i = 0; i < id_cnt; i++) {
				sync_id[i] = PNGFamily.byte2int(data, pos);
				pos += 4;
			}
		}

		return true;
	}

	public void setDefaultDEFI() {
		oid = 0;
		invisible = 0;
		delta_png = 0;
		x_location = 0;
		y_location = 0;
		cb_left = 0;
		cb_right = width;
		cb_top = 0;
		cb_bottom = height;
	}
	public void setDefaultFRAM() {
		//** �t���[�����[�h���f�t�H���g�Z�b�g4/10 Furumizo
		//** 5/12 Ikeda, ���
		f_mode       = 1;
		sf_name      = null;
		f_delay      = 1;
		sfb_left     = 0;
		sfb_right    = width;
		sfb_top      = 0;
		sfb_bottom   = height;
		flag_term    = 1;
		timeout      = INFINITE_U32;
		sync_id      = null;
	}


	public MNGParam getParameter() {
		MNGParam param = new MNGParam();
		copyParameter(this, param);
		return param;
	}
	protected void copyParameter(MNGParam src, MNGParam dst) {
		copyMHDR(src, dst);
		copyBACK(src, dst);
		copyTERM(src, dst);
		copyPLTEg(src, dst);
		copytRNSg(src, dst);
		copyDEFI(src, dst);
		copyFRAM(src, dst);
		dst.before_f_mode = src.before_f_mode;
	}
	protected void copyMHDR(MNGParam src, MNGParam dst) {
		dst.width   = src.width;
		dst.height  = src.height;
		dst.tps     = src.tps;
		dst.layers  = src.layers;
		dst.frames  = src.frames;
		dst.times   = src.times;
		dst.profile = src.profile;
	}
	protected void copyBACK(MNGParam src, MNGParam dst) {
		if(src.back == null) {
			dst.back = null;
		} else {
			dst.back = new short[src.back.length];
			System.arraycopy(src.back, 0, dst.back, 0, src.back.length);
			dst.back_flag = src.back_flag;
		}
	}
	protected void copyTERM(MNGParam src, MNGParam dst) {
		dst.action = src.action;
		dst.after  = src.after;
		dst.delay  = src.delay;
		dst.limit  = src.limit;
	}
	protected void copyPLTEg(MNGParam src, MNGParam dst) {
		dst.p_len = src.p_len;
		if(src.p_len > 0) {
			dst.pallet = new byte[3][src.p_len];
			for(int i = 0; i < 3; i++) {
				System.arraycopy(src.pallet[i], 0, dst.pallet[i], 0,
				                 src.p_len);
			}
		} else {
			dst.pallet = null;
		}
	}
	protected void copytRNSg(MNGParam src, MNGParam dst) {
		dst.t_len = src.t_len;
		if(src.t_len > 0) {
			dst.trans = new byte[src.trans.length];
			System.arraycopy(src.trans, 0, dst.trans, 0, src.trans.length);
		} else {
			dst.trans = null;
		}
	}
	protected void copyDEFI(MNGParam src, MNGParam dst) {
		dst.oid        = src.oid;
		dst.invisible  = src.invisible;
		dst.delta_png  = src.delta_png;
		dst.x_location = src.x_location;
		dst.y_location = src.y_location;
		dst.cb_left    = src.cb_left;
		dst.cb_right   = src.cb_right;
		dst.cb_top     = src.cb_top;
		dst.cb_bottom  = src.cb_bottom;
	}
	protected void copyFRAM(MNGParam src, MNGParam dst) {
		dst.f_mode = src.f_mode;
		if(src.sf_name == null) {
			dst.sf_name = null;
		} else {
			dst.sf_name = new byte[src.sf_name.length];
			System.arraycopy(src.sf_name, 0, dst.sf_name, 0,
			                 src.sf_name.length);
		}
		dst.flag_f_delay  = src.flag_f_delay;
		dst.flag_term     = src.flag_term;
		dst.flag_sfb      = src.flag_sfb;
		dst.flag_sync_id  = src.flag_sync_id;
		dst.f_delay       = src.f_delay;
		dst.timeout       = src.timeout;
		dst.flag_diff_sfb = src.flag_diff_sfb;
		dst.sfb_left      = src.sfb_left;
		dst.sfb_right     = src.sfb_right;
		dst.sfb_top       = src.sfb_top;
		dst.sfb_bottom    = src.sfb_bottom;
		if(src.sync_id == null) {
			dst.sync_id = null;
		} else {
			dst.sync_id = new int[src.sync_id.length];
			System.arraycopy(src.sync_id, 0, dst.sync_id, 0,
			                 src.sync_id.length);
		}
	}
}
