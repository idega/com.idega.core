package com.idega.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Mar 30, 2004
 */
public class ZipInputStreamIgnoreClose extends ZipInputStream {

	/**
	 * @param in
	 */
	public ZipInputStreamIgnoreClose(InputStream in) {
		super(in);
	}
	
	public void close() {
		// ignore it
	}
	
	public void closeStream() throws IOException {
		super.close();
	}
		
}
