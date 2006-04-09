/**
 * A PrintWriter that does not print anything but writes everything to a stringbuffer that can then be fetched as an InputStream.<br>
 * You construct it with an output stream , e.g. the real stream to the browser that you can then later use for output.
 * This object is used by passed it into IWContext as the default output writer with iwc.setWriter(StringBufferWriter). 
 * @author <a href="mailto:eiki@idega.is>Eirikur Hrafnsson</a>
 */
package com.idega.servlet.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class StringBufferWriter extends PrintWriter {

		private OutputStream underlying;
		private StringBuffer buffer;
		private String newLine = "\n";

		public StringBufferWriter(OutputStream underlying) {
			super(underlying);
			this.underlying = underlying;
			this.buffer = new StringBuffer();
		}
		
		

		public void print(boolean b) {
			print(String.valueOf(b));
		}

		public void print(char c) {
			print(String.valueOf(c));
		}

		public void print(char[] s) {
			print(String.valueOf(s));
		}

		public void print(double d) {
			print(String.valueOf(d));
		}

		public void print(float f) {
			print(String.valueOf(f));
		}

		public void print(int i) {
			print(String.valueOf(i));
		}

		public void print(long l) {
			print(String.valueOf(l));
		}

		public void print(Object o) {
			print(String.valueOf(o));
		}

		public void print(String s) {
			//underlying.print(s);
			this.buffer.append(s);
		}

		public void println() {
			//underlying.println();
			this.buffer.append(this.newLine);
		}

		public void println(boolean b) {
			println(String.valueOf(b));
		}

		public void println(char c) {
			println(String.valueOf(c));
		}

		public void println(char[] s) {
			println(String.valueOf(s));
		}

		public void println(double d) {
			println(String.valueOf(d));
		}

		public void println(float f) {
			println(String.valueOf(f));
		}

		public void println(int i) {
			println(String.valueOf(i));
		}

		public void println(long l) {
			println(String.valueOf(l));
			
		}

		public void println(Object o) {
			println(String.valueOf(o));
		}

		public void println(String s) {
			print(s);
			println();
		}

		public void setError() {
			super.setError();
		}

		public void write(char[] buf) {
			print(buf);
		}
		
		public InputStream getInputStream(){
			return new ByteArrayInputStream(this.buffer.toString().getBytes());
		}
		
		public OutputStream getOutputStream(){
			return this.underlying;
		}

		public void write(char[] buf, int off, int len) {
			char[] newarray = new char[len];
			System.arraycopy(buf, off, newarray, 0, len);
			write(newarray);
		}

		public void write(int c) {
			print(c);
		}

		public void write(String s) {
			print(s);
		}

		public void write(String s, int off, int len) {
			write(s.substring(off, off + len));
		}

	}