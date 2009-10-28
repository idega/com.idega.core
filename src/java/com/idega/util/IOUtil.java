package com.idega.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.ZipInputStream;

import com.idega.io.ZipInstaller;

public class IOUtil {
	
	public static final void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		
		try {
			closeable.close();
		} catch (IOException e) {}
	}
	
	public static final void closeInputStream(InputStream stream) {
		close(stream);
	}
	
	public static final void closeOutputStream(OutputStream stream) {
		close(stream);
	}
	
	public static final void closeReader(Reader reader) {
		close(reader);
	}
	
	public static final void closeWriter(Writer writer) {
		close(writer);
	}
	
	public static final InputStream getStreamFromCurrentZipEntry(ZipInputStream zipStream) throws IOException {
		if (zipStream == null) {
			throw new IOException("Unable to read from provided zip stream");
		}
		
		ZipInstaller zipInstaller = new ZipInstaller();
		
		ByteArrayOutputStream memory = new ByteArrayOutputStream();
		zipInstaller.writeFromStreamToStream(zipStream, memory);
		return new ByteArrayInputStream(memory.toByteArray());
	}

}
