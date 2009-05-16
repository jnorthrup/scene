package png;/*
* png.FullyInputStream.java -- ?w???T?C?Y?????????u???b?N???? InputStream
* Copyright (C) 2000?N?A?r?c?i??
*
* ?g?p???? : GNU Library General Public License
* ??????? : ?{???C?u?????????????B
*/

import java.io.*;

public class FullyInputStream extends FilterInputStream {
	private final boolean debug_flag = false;

	public FullyInputStream(InputStream in) {
		super(in);
	}

	public int read(byte buf[], int offset, int len) throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.FullyInputStream.read");
		}

		int total = 0;
		do {
			if(debug_flag) {
				System.out.println("total        = " + total);
				System.out.println("in_available = " + in.available());
			}

			int rbytes = in.read(buf, offset + total, len - total);
			if(rbytes > 0) { total += rbytes; }

			if(debug_flag) {
				System.out.println("rbytes       = " + rbytes);
			}

			if(rbytes < 0) { break; }
		} while(total < len);

		if(debug_flag) {
			System.out.println("OUT: png.FullyInputStream.read");
		}

		if((total == 0) && (len > 0)) { return -1; }
		return total;
	}
	public int read(byte buf[]) throws IOException {
		return read(buf, 0, buf.length);
	}
	public int read() throws IOException {
		byte b[] = new byte[1];
		return (int)((read(b, 0, 1) < 0)?-1:(b[0] & 0xFF));
	}
	public long skip(long len) throws IOException {
		if(debug_flag) {
			System.out.println("IN: png.FullyInputStream.skip");
		}

		byte b[] = new byte[1024];

		long total = 0;
		while(total < len) {
			int rbytes = 0;
			if(len - total > b.length) {
				rbytes = read(b, 0, b.length);
			} else {
				rbytes = read(b, 0, (int)(len - total));
			}
			if(rbytes < 0) { break; } else { total += rbytes; }
		}

		if(debug_flag) {
			System.out.println("sbytes: " + total);
			System.out.println("OUT: png.FullyInputStream.skip");
		}

		return total;
	}
}
