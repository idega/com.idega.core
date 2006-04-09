package com.idega.io;

import java.io.InputStream;
import java.io.IOException;
/**
 * A utility class to use for temporary buffering and connecting streams in memory.
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class MemoryInputStream extends InputStream {

	private MemoryFileBuffer buffer;
	private int position = 0;

	public MemoryInputStream(MemoryFileBuffer buffer) {
		this.buffer = buffer;
	}

	public int read() {
		//System.out.println("Calling read1 on MemoryInputStream");
		if (this.position < this.buffer.length()) {
			return this.buffer.read(this.position++);
		} else {
			return -1;
		}
	}

	public int read(byte[] b) {
		//System.out.println("Calling read2 on MemoryInputStream");
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) {
		//System.out.println("Calling read3 on MemoryInputStream");
		int bufferlength = this.buffer.length();
		if (this.position < bufferlength) {
			int oldPos = this.position;
			this.position += len;
			return this.buffer.read(b, oldPos + off, len);
		} else {
			return -1;
		}
	}

	public int available() throws IOException {
		//System.out.println("Calling available on MemoryInputStream");
		return this.buffer.length() - this.position;
	}

	public void close() {
		try {
			reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Calling close on MemoryInputStream");
	}

	public synchronized void mark(int p0) {
		//System.out.println("Calling mark on MemoryInputStream");
	}

	public synchronized void reset() throws IOException {
		this.position = 0;
		//System.out.println("Calling reset on MemoryInputStream");
	}

	public boolean markSupported() {
		//System.out.println("Calling markSupported on MemoryInputStream");
		return false;
	}
}