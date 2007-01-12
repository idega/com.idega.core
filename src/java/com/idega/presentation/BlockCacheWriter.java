/*
 * $Id: BlockCacheWriter.java,v 1.1.2.1 2007/01/12 19:31:35 idegaweb Exp $
 * Created on 13.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.PrintWriter;


public class BlockCacheWriter extends java.io.PrintWriter {

	private PrintWriter underlying;
	private StringBuffer buffer;

	public BlockCacheWriter(PrintWriter underlying, StringBuffer buffer) {
		super(underlying);
		this.underlying = underlying;
		this.buffer = buffer;
	}

	public boolean checkError() {
		return this.underlying.checkError();
	}

	public void close() {
		this.underlying.close();
	}

	public void flush() {
		this.underlying.flush();
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
		this.underlying.print(s);
		this.buffer.append(s);
	}

	public void println() {
		this.underlying.println();
		this.buffer.append(Block.newline);
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
		;
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