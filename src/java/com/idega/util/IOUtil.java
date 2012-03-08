package com.idega.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.zip.ZipInputStream;

import com.idega.io.ZipInstaller;

public class IOUtil {
	
	public static final void closeInputStream(InputStream stream) {
		if (stream == null) {
			return;
		}
		
		try {
			stream.close();
		} catch(IOException e) {}
	}
	
	public static final void closeOutputStream(OutputStream stream) {
		if (stream == null) {
			return;
		}
		
		try {
			stream.close();
		} catch(IOException e) {}
	}
	
	public static final void closeReader(Reader reader) {
		if (reader == null) {
			return;
		}
		
		try {
			reader.close();
		} catch(IOException e) {}
	}
	public static final boolean isStreamValid(InputStream stream) {
		try {
			return stream != null && stream.available() >= 0;
		} catch (Exception e) {}
		return false;
	}
	
	public static final void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		
		try {
			closeable.close();
		} catch (IOException e) {}
	}
	
	public static byte[] getBytesFromInputStream(InputStream stream) {
		if (!isStreamValid(stream)) {
			return null;
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			FileUtil.streamToOutputStream(stream, output);
			return output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stream);
			close(output);
		}
		
		return null;
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