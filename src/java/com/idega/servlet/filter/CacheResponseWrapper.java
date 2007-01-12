package com.idega.servlet.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CacheResponseWrapper
    extends HttpServletResponseWrapper {
  protected HttpServletResponse origResponse = null;
  protected ServletOutputStream stream = null;
  protected PrintWriter writer = null;
  protected OutputStream cache = null;

  public CacheResponseWrapper(HttpServletResponse response,
      OutputStream cache) {
    super(response);
    this.origResponse = response;
    this.cache = cache;
  }

  public ServletOutputStream createOutputStream()
      throws IOException {
    return (new CacheResponseStream(this.origResponse, this.cache));
  }

  public void flushBuffer() throws IOException {
    this.stream.flush();
  }

  public ServletOutputStream getOutputStream()
      throws IOException {
    if (this.writer != null) {
      throw new IllegalStateException(
        "getWriter() has already been called!");
    }

    if (this.stream == null) {
		this.stream = createOutputStream();
	}
    return (this.stream);
  }

  public PrintWriter getWriter() throws IOException {
    if (this.writer != null) {
      return (this.writer);
    }

    if (this.stream != null) {
      throw new IllegalStateException(
        "getOutputStream() has already been called!");
    }

   this.stream = createOutputStream();
   this.writer = new PrintWriter(new OutputStreamWriter(this.stream, "UTF-8"));
   return (this.writer);
  }
}
