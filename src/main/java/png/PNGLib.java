package png;/*
* png.PNGLib.java -- PNG �f�[�^�E�X�g���[�����������߂̃N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.io.*;
import java.net.*;
import java.util.zip.*;

public class PNGLib extends PNGFamily {
	private final boolean debug_flag = false;
	private final int debug_lv = 0;

	public static final byte SIGNATURE[] = {
		(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47,
		(byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A
	};

	private PNGParamOp param;

	// IDAT Chunk
	private byte idat_prior[];
	private Inflater z;

	public PNGLib(URL url) {
		super(url, SIGNATURE);
		initialize();
		param = new PNGParamOp();
	}
	public PNGLib(File file) {
		super(file, SIGNATURE);
		initialize();
		param = new PNGParamOp();
	}
	public PNGLib(MNGLib ml) {
		super(ml, SIGNATURE);
		initialize();
		param = new PNGParamOp(ml);
	}
	private void initialize() {
		idat_prior = null;
		z = null;
	}

	public void closeStream() throws IOException {
		initialize();
		super.closeStream();
	}

	public static boolean checkSignature(URL url) {
		return (new PNGLib(url)).checkSignature();
	}
	public static boolean checkSignature(File file) {
		return (new PNGLib(file)).checkSignature();
	}

	public boolean isSupported() {
		if(param.compress != 0) { return false; }
		if(param.filter != 0) { return false; }
		if(param.interace != 0) { return false; }

		return true;
	}

	public PNGParam getInfo() {
		return param.getParameter();
	}
	public boolean readInfo() throws IOException {
		return readChunkLoop();
	}
	public int encounterChunk() throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.PNGLib.encounterChunk");
		}

		byte type[] = getChunkType();
		int len = getChunkLength();

		if(debug_flag) {
			System.out.println("type = " + (new String(type, 0, 4)));
			System.out.println("len  = " + len);
		}

		if(compareChunkType("IEND", type, 0)) {
			return LOOP_BREAK_EOS;
		} else if(compareChunkType("IDAT", type, 0)) {
			return LOOP_BREAK_FOUND;
		}

		byte data[] = new byte[len];
		int rbytes = readChunkData(data, 0, len);
		if(rbytes < len) { return LOOP_CONTINUE; }

		if(compareChunkType("IHDR", type, 0)) {
			param.readIHDR(len, data);
		} else if(compareChunkType("PLTE", type, 0)) {
			param.readPLTE(len, data);
		} else if(compareChunkType("tRNS", type, 0)) {
			param.readtRNS(len, data);
		} else {
			;
		}

		if(debug_flag) { System.out.println("OUT: png.PNGLib.encounterChunk"); }

		return LOOP_CONTINUE;
	}

	public byte[] readLine() throws IOException, DataFormatException {
		if(debug_flag) {
			System.out.println("IN: png.PNGLib.readLine");
		}

		int bpl = getBytePerLine();

		if((idat_prior == null) || (z == null)) {
			idat_prior = new byte[bpl];
			z = new Inflater();
			for(int i = 0; i < bpl; i++) { idat_prior[i] = 0; }
		}

		// ���C���o�b�t�@�̃t�B��(�t�B���ł��Ȃ���� null ��Ԃ�)
		// (�擪 1Byte �̓t�B���^�[�̃A���S���Y��)
		byte buf[] = new byte[bpl + 1];
		int total = 0;
		while(total < buf.length) {
			if((z.needsInput() && (getChunkRemain() <= 0)) ||
			           !compareChunkType("IDAT", getChunkType(), 0)) {
				boolean flag = readChunkLoop();
				if(!flag || isEOS()) {
					z.end();
					idat_prior = null;
					z = null;
					return null;
				}
			}

			if(debug_flag) {
				System.out.println("needs:  " + (buf.length - total) + " ");
			}

			int rbytes = readIDAT(buf, total, buf.length - total);
			if(rbytes > 0) { total += rbytes; }

			if(debug_flag) {
				System.out.println("rbytes: " + rbytes);
			}
		}

		// �t�B���^�[�̉���
		byte pix[] = reverseFilter(buf[0], buf, 1, bpl);

		if(debug_flag) {
			System.out.println("OUT: png.PNGLib.readLine");
		}

		return pix;
	}
	private int readIDAT(byte b[], int offset, int len)
	                     throws IOException, DataFormatException {
		if(debug_flag) {
			System.out.println("IN: png.PNGLib.readIDAT");
		}

		if(isEOS()) { return -1; }

		// �I�������� (1) �w�肳�ꂽ�T�C�Y�̓ǂݍ��݂������B
		//            (2) �`�����N�E�f�[�^�̎c�肪�[���B
		//            (3) InputStream �Ƀf�[�^���c���Ă��Ȃ��B
		int total = 0;
		while(total < len) {
			if(debug_flag) {
				System.out.println("z_needsInput   : " + z.needsInput());
				System.out.println("z_getRemaining = " + z.getRemaining());
				System.out.println("chunk_reamin   = " + getChunkRemain());
				//System.out.println("in_available   = " + in.available());
			}

			if(z.needsInput()) {
				byte buf[] = new byte[1024];
				int rbytes = readChunkData(buf, 0, buf.length);
				if(rbytes <= 0) { return total; }
				z.setInput(buf, 0, rbytes);
			}
			total += z.inflate(b, total + offset, len - total);
		}

		if(debug_flag) {
			System.out.println("OUT: png.PNGLib.readIDAT");
		}

		return total;
	}
	private byte[] reverseFilter(byte algo, byte src[], int offset, int len) {
		if(debug_flag) {
			System.out.println("png.PNGLib.reverseFilter");

			System.out.println("algo: " + algo);

			/*
			System.out.println("src:" + len);
			for(int i = 0; i < len; i++) {
				System.out.print(Integer.toString(src[i + offset] & 0xFF, 16)
				                        .toUpperCase() + " ");
			}
			System.out.println();

			System.out.println("prior:");
			for(int i = 0; i < len; i++) {
				System.out.print(Integer.toString(idat_prior[i] & 0xFF, 16)
				                        .toUpperCase() + " ");
			}
			System.out.println();
			*/
		}

		byte dst[] = new byte[len];
		int bpp = getBytePerPixel();
		switch(algo) {
			case 0:    // none
				System.arraycopy(src, offset, dst, 0, len);
				break;
			case 1:    // sub
				for(int i = 0; i < bpp; i++) {
					dst[i] = src[i + offset];
				}
				for(int i = bpp; i < len; i++) {
					dst[i] = (byte)((src[i + offset] & 0xFF)
					                + (dst[i - bpp] & 0xFF));
				}
				break;
			case 2:    // up
				for(int i = 0; i < len; i++) {
					dst[i] = (byte)((src[i + offset] & 0xFF)
					                + (idat_prior[i] & 0xFF));
				}
				break;
			case 3:    // average
				byte avg;
				for(int i = 0; i < len; i++) {
					if(i < bpp) {
						avg = (byte)((idat_prior[i] & 0xFF) >>> 1);
					} else {
						avg = (byte)((dst[i - bpp] & 0xFF)
						             + (idat_prior[i] & 0xFF) >>> 1);
					}
					dst[i] = (byte)((src[i + offset] & 0xFF) + (avg & 0xFF));
				}
				break;
			case 4:    // peath
				byte peath;
				for(int i = 0; i < len; i++) {
					if(i < bpp) {
						peath = idat_prior[i];
					} else {
						int a = dst[i - bpp]        & 0xFF;
						int b = idat_prior[i]       & 0xFF;
						int c = idat_prior[i - bpp] & 0xFF;
						peath = peathFilter(a, b, c);
					}
					dst[i] = (byte)((src[i + offset] & 0xFF) + (peath & 0xFF));
				}
				break;
			default:
				return null;
		}
		System.arraycopy(dst, 0, idat_prior, 0, len);

		if(debug_flag) {
			if(debug_lv > 1) {
				System.out.println("dst:");
				for(int i = 0; i < len; i++) {
					System.out.print(Integer.toString(dst[i] & 0xFF, 16)
					                        .toUpperCase() + " ");
				}
				System.out.println();
			}
			System.out.println("OUT: reverseFilter");
		}

		return dst;
	}
	private byte peathFilter(int a, int b, int c) {
		byte peath;
		int p, pa, pb, pc;

		p = a + b - c;
		pa = p - a; pa = (pa < 0)?-pa:pa;
		pb = p - b; pb = (pb < 0)?-pb:pb;
		pc = p - c; pc = (pc < 0)?-pc:pc;

		if((pa <= pb) && (pa <= pc)) {
			peath = (byte)a;
		} else if(pb <= pc) {
			peath = (byte)b;
		} else {
			peath = (byte)c;
		}

		return peath;
	}
	public int getSamplePerPixel() {
		switch(param.color) {
			case 0:  return 1;
			case 2:  return 3;
			case 3:  return 1;
			case 4:  return 2;
			case 6:  return 4;
			default:         ;
		}
		return 0;
	}
	private int getBytePerPixel() {
		int bpp = param.depth * getSamplePerPixel();
		if(bpp % 8 == 0) {
			bpp /= 8;
		} else {
			bpp = bpp / 8 + 1;
		}
		return bpp;
	}
	private int getBytePerLine() {
		int bpl = param.depth * getSamplePerPixel();
		if(bpl % 8 == 0) {
			bpl = (bpl / 8) * param.width;
		} else if((bpl * param.width) % 8 == 0) {
			bpl = (bpl * param.width) / 8;
		} else {
			bpl = (bpl * param.width) / 8 + 1;
		}
		return bpl;
	}
}
