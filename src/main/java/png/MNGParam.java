package png;/*
* png.MNGParam.java -- MNG �̃p�����[�^���i�[���邽�߂̃N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

public class MNGParam {
	public static final int INFINITE_U32 = 0x7FFFFFFF;

	// MHDR Chunk
	public int width;
	public int height;
	public int tps;
	public int layers;
	public int frames;
	public int times;
	public int profile;

	// BACK Chunk
	public short back[];
	public byte back_flag;

	// TERM Chunk
	public byte action;
	public byte after;
	public int delay;
	public int limit;

	// global PLTE Chunk
	public int p_len;
	public byte pallet[][];

	// global tRNS Chunk
	public int t_len;
	public byte trans[];

	// DEFI Chunk
	public short oid;
	public byte invisible;
	public byte delta_png;
	public int x_location;
	public int y_location;
	public int cb_left;
	public int cb_right;
	public int cb_top;
	public int cb_bottom;

	//** FRAM �`�����N�p 4/10 Furumizo
	//** 5/12 Ikeda ���
	public byte f_mode;    // �t���[�~���O���[�h
	public byte sf_name[];
	public byte flag_f_delay;
	public byte flag_term;
	public byte flag_sfb;
	public byte flag_sync_id;
	public int f_delay;    // �t���[���x��
	public int timeout;
	public byte flag_diff_sfb;
	public int sfb_left;
	public int sfb_right;
	public int sfb_top;
	public int sfb_bottom;
	public int sync_id[];

	public byte before_f_mode;

	public MNGParam() {
		back = null;
		p_len = 0;
		t_len = 0;
	}
}
