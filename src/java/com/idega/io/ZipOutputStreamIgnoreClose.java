package com.idega.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Apr 5, 2004
 */
public class ZipOutputStreamIgnoreClose extends ZipOutputStream {
	
	public ZipOutputStreamIgnoreClose(OutputStream in) {
		super(in);
	}
	
	public void close() {
		// ignore it
	}
	
	public void closeStream() throws IOException {
		super.close();
	}
		
}	
