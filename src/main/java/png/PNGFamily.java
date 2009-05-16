package png;/*
* PNGFamily.java -- �N���X png.MNGLib �� �N���X png.PNGLib �̃X�[�p�[�E�N���X
* Copyright (C) 2000�N�A�r�c�i��
*
* �g�p���� : GNU Library General Public License
* ���ӎ��� : �{���C�u�����͖��ۏ؂ł��B
*/

import java.io.*;
import java.net.*;

abstract class PNGFamily {
	private final boolean debug_flag = false;

	protected static final int LOOP_CONTINUE    = 0;
	protected static final int LOOP_BREAK_EOS   = 1;
	protected static final int LOOP_BREAK_FOUND = 2;
	protected static final int LOOP_BREAK_ERROR = 3;

	private URL src_url;
	private File src_file;
	private byte sig[];

	private InputStream in;
	private boolean flag_eos;

	private int c_len;
	private byte c_type[];
	private byte c_crc[];
	private int c_remain;
	private boolean flag_find;

	private PNGFamily host;
	private PNGFamily embed;

	private byte src_data[];
	private boolean flag_cache;

	protected PNGFamily(URL url, byte[] s) {
		initialize();
		src_url = url;
		sig = s;
	}
	protected PNGFamily(File file, byte[] s) {
		initialize();
		src_file = file;
		sig = s;
	}
	protected PNGFamily(PNGFamily pf, byte[] s) {
		initialize();
		pf.setEmbeded(this);
		sig = s;
	}
	protected void finalize() throws Throwable {
		if(host == this) {
			closeStream();
			if(embed != this) { embed.finalize(); }
		} else if(embed == this) {
			host.unsetEmbeded();
		}
		super.finalize();
	}
	private void initialize() {
		src_url = null;
		src_file = null;
		sig = null;

		in = null;
		flag_eos = false;

		c_len = 0;
		c_type = new byte[4];
		c_crc = new byte[4];
		c_remain = 0;
		flag_find = false;

		host = this;
		embed = this;

		src_data = null;
		flag_cache = false;
	}

	protected void setEmbeded(PNGFamily pf) {
		if(host != this) { return; }
		if(embed != this) { unsetEmbeded(); }
		pf.host = this;
		this.embed = pf;
	}
	protected void unsetEmbeded() {
		if(host != this) { return; }
		embed.host = embed;
		this.embed = this;
	}
	public boolean isEmbeded() {
		return (host != this);
	}

	abstract public int encounterChunk() throws IOException;

	public boolean readChunkLoop() throws IOException {
		if(host != this) {
			return host.readChunkLoop();
		}

		if(debug_flag) {
			System.out.println("IN: PNGFamily.readChunkLoop");
		}

		while(in.available() > 0) {
			/*
			System.out.println("c_len    = " + c_len);
			System.out.println("c_type   = " + (new String(c_type, 0, 4)));
			System.out.println("c_remain = " + c_remain);
			System.out.println("flag_find: " + flag_find);
			*/

			if(flag_find) {
				if((c_remain != c_len) || (c_len == 0)) {
					if((c_remain > 0)) {
						in.skip(c_remain);
						c_remain = 0;
					}
					in.skip(c_crc.length);
				}
				flag_find = false;
			}

			if((c_remain != c_len) || (c_len == 0)) {
				byte buf[] = new byte[8];
				int rbytes = in.read(buf, 0, 8);
				if(rbytes < 8) { return false; }

				c_len = byte2int(buf, 0);
				System.arraycopy(buf, 4, c_type, 0, c_type.length);
				c_remain = c_len;
			}

			if(debug_flag) {
				System.out.println("c_len    = " + c_len);
				System.out.println("c_type   = " + (new String(c_type, 0, 4)));
				System.out.println("c_remain = " + c_remain);
			}

			int flag = embed.encounterChunk();

			if(debug_flag) {
				System.out.println("flag: " + flag);
			}

			if((flag == LOOP_CONTINUE) || (flag == LOOP_BREAK_EOS)) {
				in.skip(c_remain);
				c_remain = 0;
				in.skip(c_crc.length);
			}
			if(flag == LOOP_BREAK_EOS) {
				embed.flag_eos = true;
			}
			if(flag == LOOP_BREAK_FOUND) {
				flag_find = true;
			}

			if(flag == LOOP_BREAK_ERROR) {
				break;
			} else if(flag != LOOP_CONTINUE) {
				return true;
			}
		}

		if(debug_flag) {
			System.out.println("OUT: PNGFamily.readChunkLoop");
		}

		return false;
	}

	protected InputStream cacheStream(URL url) throws IOException {
		int len = url.openConnection().getContentLength();
		if(len <= 0) { len = (int)getURLLength(url); }
		return cacheStream(openStream(url), len);
	}
	protected InputStream cacheStream(File file) throws IOException {
		return cacheStream(openStream(file), (int)file.length());
	}
	protected InputStream cacheStream(InputStream in, int len)
		                                           throws IOException {
		if((in == null) || (len <= 0)) { return null; }
		src_data = new byte[len];
		int rbytes = in.read(src_data, 0, len);
		in.close();
		if(rbytes < len) { return null; }
		return (new ByteArrayInputStream(src_data, 0, len));
	}
	protected long getURLLength(URL url) throws IOException {
		InputStream in = openStream(url);
		long sbytes = in.skip(Long.MAX_VALUE);
		in.close();
		return sbytes;
	}
	public void setCaching(boolean flag) { flag_cache = flag; }

	protected InputStream openStream(URL url) throws IOException {
		URLConnection connect = url.openConnection();
		connect.setUseCaches(true);
		return new FullyInputStream(connect.getInputStream());
	}
	protected InputStream openStream(File file) throws IOException {
		return new FileInputStream(file);
	}
	public void openStream() throws IOException {
		if((in != null) || (host != this)) { return; }
		if(flag_cache) {
			if(src_data != null) {
				in = new ByteArrayInputStream(src_data, 0, src_data.length);
			} else if(src_url != null) {
				in = cacheStream(src_url);
			} else if(src_file != null) {
				in = cacheStream(src_file);
			}
		}
		if(in == null) {
			if(src_url != null) {
				in = openStream(src_url);
			} else if(src_file != null) {
				in = openStream(src_file);
			}
		}
		if(!checkSignature(in)) { closeStream(); }
	}
	public void closeStream() throws IOException {
		if((in == null) || (host != this)) { return; }
		in.close();

		in = null;
		flag_eos = false;

		c_len = 0;
		c_type = new byte[4];
		c_crc = new byte[4];
		c_remain = 0;
		flag_find = false;
	}
	public void reopenStream() throws IOException {
		closeStream();
		openStream();
	}
	public boolean existsStream() { return (in != null); }

	public boolean isEOS() throws IOException {
		return (flag_eos || (host.in.available() <= 0));
	}
	protected int getChunkLength() {
		return host.c_len;
	}
	protected byte[] getChunkType() {
		return host.c_type;
	}
	protected int getChunkRemain() {
		return host.c_remain;
	}
	protected int readChunkData(byte[] b, int offset, int len)
	                                                  throws IOException {
		if(host.in.available() <= 0) { return -1; }
		if(host.c_remain <= 0) { return 0; }

		int rbytes = 0;
		if(len < host.c_remain) {
			rbytes = host.in.read(b, offset, len);
		} else {
			rbytes = host.in.read(b, offset, host.c_remain);
		}
		if(rbytes > 0) { host.c_remain -= rbytes; }

		return rbytes;
	}
	protected long skipChunkData(long len) throws IOException {
		if(host.c_remain <= 0) { return 0; }

		long sbytes = 0;
		if(len < host.c_remain) {
			sbytes = host.in.skip(len);
		} else {
			sbytes = host.in.skip(host.c_remain);
		}
		if(sbytes > 0) { host.c_remain -= sbytes; }

		return sbytes;
	}

	public boolean checkSignature() {
		try {
			if(src_url != null) {
				return checkSignature(openStream(src_url));
			} else if(src_file != null) {
				return checkSignature(openStream(src_file));
			}
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	protected boolean checkSignature(InputStream in) {
		byte buf[] = new byte[sig.length];
		try {
			int rbytes = in.read(buf, 0, sig.length);
			if(rbytes < sig.length) { return false; }
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return bytecmp(buf, 0, sig, 0, sig.length);
	}
	protected boolean checkSignature(byte sig[], byte buf[], int offset) {
		return bytecmp(buf, offset, sig, 0, sig.length);
	}
	protected boolean compareChunkType(String type, byte buf[], int offset) {
		return bytecmp(buf, offset, type.getBytes(), 0, 4);
	}

	public static boolean bytecmp(byte b1[], int s1,
	                              byte b2[], int s2, int len) {
		for(int i = 0; i < len; i++) {
			if(b1[i + s1] != b2[i + s2]) { return false; }
		}
		return true;
	}
	public static int byte2int(byte b[], int offset) {
		int val = 0;
		for(int i = 0; i < 4; i++) {
			val <<= 8;
			val |= (b[i + offset] & 0xFF);
		}
		return val;
	}
	public static short byte2short(byte b[], int offset) {
		short val = 0;
		for(int i = 0; i < 2; i++) {
			val <<= 8;
			val |= (short)(b[i + offset] & 0xFF);
		}
		return val;
	}
}
