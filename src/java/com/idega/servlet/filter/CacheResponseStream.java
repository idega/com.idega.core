package com.idega.servlet.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class CacheResponseStream extends ServletOutputStream {
  protected boolean closed = false;
  protected HttpServletResponse response = null;
  protected ServletOutputStream output = null;
  protected OutputStream cache = null;

  public CacheResponseStream(HttpServletResponse response,
      OutputStream cache) throws IOException {
    super();
    closed = false;
    this.response = response;
    this.cache = cache;
  }

  public void close() throws IOException {
    if (closed) {
      throw new IOException(
        "This output stream has already been closed");
    }
    cache.close();
    closed = true;
  }

  public void flush() throws IOException {
    if (closed) {
      throw new IOException(
        "Cannot flush a closed output stream");
    }
    cache.flush();
  }

  public void write(int b) throws IOException {
    if (closed) {
      throw new IOException(
        "Cannot write to a closed output stream");
    }
    cache.write((byte)b);
  }

  public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte b[], int off, int len)
    throws IOException {
    if (closed) {
      throw new IOException(
       "Cannot write to a closed output stream");
    }
    cache.write(b, off, len);
  }

  public boolean closed() {
    return (this.closed);
  }
  
  public void reset() {
    //noop
  }
}
