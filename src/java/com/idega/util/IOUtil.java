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
	
	public static final int BUFFER_SIZE = 4096;

	/**
	 * Copy the contents of the given InputStream into a new byte array.
	 * Leaves the stream open when done.
	 * @param in the stream to copy from
	 * @return the new byte array that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static byte[] copyToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		copy(in, out);
		return out.toByteArray();
	}
	
	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Leaves both streams open when done.
	 * @param in the InputStream to copy from
	 * @param out the OutputStream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException {
		if (in == null) {
			throw new IOException("No InputStream specified");
		}
		if (out == null) {
			throw new IOException("No OutputStream specified");
		}
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}
	
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