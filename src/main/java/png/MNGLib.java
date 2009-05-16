package png;/*
* png.MNGLib.java -- MNG �f�[�^�E�X�g���[�����������߂̃N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.io.*;
import java.net.*;

public class MNGLib extends PNGFamily {
	private final boolean debug_flag = false;

	public static final byte SIGNATURE[] = {
		(byte)0x8A, (byte)0x4D, (byte)0x4E, (byte)0x47,
		(byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A
	};

	private static final int PROFILE_VLC     = 0x00000001;
	private static final int PROFILE_LC      = 0x00000002;
	private static final int PROFILE_FULL    = 0x00000004;
	private static final int PROFILE_TRANS   = 0x00000008;
	private static final int PROFILE_JNG     = 0x00000010;
	private static final int PROFILE_DELTA   = 0x00000020;
	private static final int PROFILE_RESERVE = 0x0000FFC0;
	private static final int PROFILE_EXPERIM = 0x7FFF0000;

	private MNGParamOp param;
	private boolean flag_info;
	private boolean flag_fram;
	private boolean flag_defi;

	public MNGLib(URL url) {
		super(url, SIGNATURE);
		initialize();
	}
	public MNGLib(File file) {
		super(file, SIGNATURE);
		initialize();
	}
	private void initialize() {
		param = new MNGParamOp();
		flag_info = false;
		flag_fram = false;
		flag_defi = false;
	}

	public static boolean checkSignature(URL url) {
		return (new MNGLib(url)).checkSignature();
	}
	public static boolean checkSignature(File file) {
		return (new MNGLib(file)).checkSignature();
	}

	public boolean isSupported() {
		if(param.profile < 0) { return false; }

		if((param.profile & (PROFILE_VLC | PROFILE_LC)) == 0) {
			return false;
		}
		if((param.profile & PROFILE_FULL)    != 0) { return false; }
		if((param.profile & PROFILE_JNG)     != 0) { return false; }
		if((param.profile & PROFILE_DELTA)   != 0) { return false; }
		if((param.profile & PROFILE_RESERVE) != 0) { return false; }

		return true;
	}

	public int encounterChunk() throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.MNGLib.encounterChunk");
		}

		int len = getChunkLength();
		byte type[] = getChunkType();

		if(debug_flag) {
			System.out.println("type = " + (new String(type, 0, 4)));
			System.out.println("len  = " + len);
		}

		if(compareChunkType("MEND", type, 0)) {
			return LOOP_BREAK_EOS;
		} else if(compareChunkType("IHDR", type, 0)) {
			return LOOP_BREAK_FOUND;
		} else if(compareChunkType("JHDR", type, 0)) {
			return LOOP_BREAK_FOUND;
		}

		byte data[] = new byte[len];
		int rbytes = readChunkData(data, 0, len);
		if(rbytes < len) { return LOOP_CONTINUE; }

		if(compareChunkType("DEFI", type, 0)) {
			flag_defi = true;
			param.readDEFI(len, data);
		} else if(compareChunkType("FRAM", type, 0)) {
			if(debug_flag) {
				System.out.println("encounter FRAM " + len);
			}

			flag_fram = true;
			//** 4/10 Furumizo FRAM �`�����N�ǎ�
			param.readFRAM(len, data);
		} else if(flag_info) {
			return LOOP_CONTINUE;
		} else if(compareChunkType("MHDR", type, 0)) {
			param.readMHDR(len, data);
		} else if(compareChunkType("BACK", type, 0)) {
			param.readBACK(len, data);
		} else if(compareChunkType("TERM", type, 0)) {
			param.readTERM(len, data);
		} else if(compareChunkType("PLTE", type, 0)) {
			param.readPLTEg(len, data);
		} else if(compareChunkType("tRNS", type, 0)) {
			param.readtRNSg(len, data);
		} else {
			;
		}

		if(debug_flag) { System.out.println("OUT: png.MNGLib.encounterChunk"); }

		return LOOP_CONTINUE;
	}

	public void reopenStream() throws IOException {
		if(flag_info) {
			if(!flag_defi) { param.setDefaultDEFI(); }
			if(!flag_fram) { param.setDefaultFRAM(); }
		}
		super.reopenStream();
	}
	public boolean wasFRAM() {
		return flag_fram;
	}
	public MNGParam getInfo() {
		return param.getParameter();
	}

	public boolean readInfo() throws IOException {
		if(debug_flag) { System.out.println("IN: png.MNGLib.readInfo"); }

		unsetEmbeded();

		flag_defi = false;
		flag_fram = false;
		flag_info = readChunkLoop();
		if(flag_info) {
			if(!flag_defi) { param.setDefaultDEFI(); }
			if(!flag_fram) { param.setDefaultFRAM(); }
		}

		if(debug_flag) { System.out.println("OUT: png.MNGLib.readInfo"); }

		return flag_info;
	}
	public MNGObject getMNGObject() throws IOException {
		if(debug_flag) { System.out.println("IN: png.MNGLib.getEmbededImage"); }

		unsetEmbeded();

		if(!flag_info) {
			if(!readInfo()) { return null; }
		}

		while(!isEOS()) {
			MNGObject mng_obj = null;

			if(compareChunkType("IHDR", getChunkType(), 0)) {
				mng_obj = MNGObject.getInstance(MNGObject.PNG, this);
			} else if(compareChunkType("JHDR", getChunkType(), 0)) {
				mng_obj = MNGObject.getInstance(MNGObject.JNG, this);
			}

			if(mng_obj != null) {
				Object obj = mng_obj.getMNGObject();
				setEmbeded((PNGFamily)obj);
				return mng_obj;
			}

			flag_defi = false;
			flag_fram = false;
			if(!readChunkLoop() || isEOS()) { return null; }
		}

		if(debug_flag) { System.out.println("OUT: png.MNGLib.getEmbededImage"); }

		return null;
	}
}
