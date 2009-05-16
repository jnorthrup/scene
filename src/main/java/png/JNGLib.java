package png;/*
* png.JNGLib.java -- JNG �f�[�^�E�X�g���[�����������߂̃N���X
* Copyright (C) 2000�N�A�r�c�i��

* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.io.*;
import java.net.*;

public class JNGLib extends PNGFamily {
	private final boolean debug_flag = false;

	public static final byte SIGNATURE[] = {
		(byte)0x8B, (byte)0x4A, (byte)0x4E, (byte)0x47,
		(byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A
	};

	private JNGParamOp param;
	private boolean flag_jsep;

	public JNGLib(URL url) {
		super(url, SIGNATURE);
		initialize();
	}
	public JNGLib(File file) {
		super(file, SIGNATURE);
		initialize();
	}
	public JNGLib(MNGLib ml) {
		super(ml, SIGNATURE);
		initialize();
	}
	private void initialize() {
		param = new JNGParamOp();
		flag_jsep = false;
	}

	public static boolean checkSignature(URL url) {
		return (new JNGLib(url)).checkSignature();
	}
	public static boolean checkSignature(File file) {
		return (new JNGLib(file)).checkSignature();
	}

	public boolean isSupported() {
		if((param.width  < 0) || (param.width  > 0xFFFF)) { return false; }
		if((param.height < 0) || (param.height > 0xFFFF)) { return false; }
		if((param.color != 8) && (param.color != 10)) { return false; }
		if(param.depth != 8) { return false; }
		if(param.compress != 8) { return false; }
		if(param.interace != 0) { return false; }

		return true;
	}

	public JNGParam getInfo() {
		return param.getParameter();
	}
	public boolean readInfo() throws IOException {
		return readChunkLoop();
	}
	public int encounterChunk() throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.JNGLib.encounterChunk");
		}

		byte type[] = getChunkType();
		int len = getChunkLength();

		if(debug_flag) {
			System.out.println("type = " + (new String(type, 0, 4)));
			System.out.println("len  = " + len);
		}

		if(compareChunkType("IEND", type, 0)) {
			return LOOP_BREAK_EOS;
		} else if(flag_jsep) {
			return LOOP_CONTINUE;
		} else if(compareChunkType("JDAT", type, 0)) {
			return LOOP_BREAK_FOUND;
		}

		byte data[] = new byte[len];
		int rbytes = readChunkData(data, 0, len);
		if(rbytes < len) { return LOOP_CONTINUE; }

		if(compareChunkType("JSEP", type, 0)) {
			flag_jsep = true;
		} else if(compareChunkType("JHDR", type, 0)) {
			param.readJHDR(len, data);
		} else {
			;
		}

		if(debug_flag) { System.out.println("OUT: png.JNGLib.encounterChunk"); }

		return LOOP_CONTINUE;
	}

	public int readJDAT(byte b[], int offset, int len) throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.JNGLib.readJDAT");
		}

		int total = 0;
		while(total < len) {
			if(debug_flag) {
				System.out.println("needs : " + (len - total));
				System.out.println("reamin: " + getChunkRemain());
			}

			if(!compareChunkType("JDAT", getChunkType(), 0)) {
				if(!readChunkLoop() || isEOS()) { break; }
				continue;
			}
			if(getChunkRemain() <= 0) {
				if(!readChunkLoop() || isEOS()) { break; }
				continue;
			}

			int rbytes = readChunkData(b, offset + total, len - total);
			if(rbytes > 0) { total += rbytes; }

			if(debug_flag) {
				System.out.println("rbytes: " + rbytes);
				System.out.println("reamin: " + getChunkRemain());
			}
		}
		if((total == 0) && (len != 0)) { total = -1; }

		if(debug_flag) {
			System.out.println("total : " + total);
			System.out.println("OUT: png.JNGLib.readJDAT");
		}

		return total;
	}
}
