package png;/*
* png.PNGParam.java -- PNG �̃p�����[�^���i�[���邽�߂̃N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

public class PNGParam {
	// IHDR Chunk
	public int  width;
	public int  height;
	public byte depth;
	public byte color;
	public byte compress;
	public byte filter;
	public byte interace;

	// PLTE Chunk
	public int  p_len;
	public byte pallet[][];

	// tRNS Chunk
	public int  t_len;
	public byte trans[];

	public PNGParam() {
		p_len = 0;
		t_len = 0;
	}
}
