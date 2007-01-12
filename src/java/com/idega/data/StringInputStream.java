package com.idega.data;
import java.io.*;

/**
 * @author Idega Software
 *
 *  A wrapper around a StringReader e.g. for use with CLOB objects when using statement.setAsciiStream(index, stream, stringValue.length());
 * 
 */
public class StringInputStream extends InputStream {
	private Reader reader;
	private int available;
	
	public StringInputStream(String stringValue) {
		this.reader = new StringReader(stringValue);
		this.available = stringValue.length();
	}
	public int available() {
		return this.available;
	}
	public void close() throws IOException {
		this.reader.close();
	}
	public void mark(int readlimit) {
		try {
			this.reader.mark(readlimit);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean markSupported() {
		return this.reader.markSupported();
	}
	public int read() throws IOException {
		return this.reader.read();
	}
	public int read(byte[] b) throws IOException {
		char[] c = new char[b.length];
		int theReturn = this.reader.read(c);
		convertCharArrayToByteArray(c, b);
		return theReturn;
	}
	public int read(byte[] b, int off, int len) throws IOException {
		char[] c = new char[b.length];
		int theReturn = this.reader.read(c, off, len);
		convertCharArrayToByteArray(c, b);
		return theReturn;
	}
	public void reset() throws IOException {
		this.reader.reset();
	}
	public long skip(long n) throws IOException {
		return this.reader.skip(n);
	}
	
	public char[] convertToCharArray(byte[] byteArray) {
		char[] charArray = new char[byteArray.length];
		for (int i = 0; i < byteArray.length; i++) {
			charArray[i] = (char) byteArray[i];
		}
		return charArray;
	}
	public void convertCharArrayToByteArray(char[] charArray, byte[] byteArray) {
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
	}
}