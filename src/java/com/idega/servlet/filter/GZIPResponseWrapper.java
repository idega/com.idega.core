/*
 * Created on 30.3.2004
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author laddi
 */
public class GZIPResponseWrapper extends HttpServletResponseWrapper {

	protected HttpServletResponse origResponse = null;
	protected ServletOutputStream stream = null;
	protected PrintWriter writer = null;

	public GZIPResponseWrapper(HttpServletResponse response) {
		super(response);
		this.origResponse = response;
	}

	public ServletOutputStream createOutputStream() throws IOException {
		return (new GZIPResponseStream(this.origResponse));
	}

	public void finishResponse() {
		try {
			if (this.writer != null) {
				this.writer.close();
			}
			else {
				if (this.stream != null) {
					this.stream.close();
				}
			}
		}
		catch (IOException e) {
		}
	}

	public void flushBuffer() throws IOException {
		this.stream.flush();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (this.writer != null) {
			throw new IllegalStateException("getWriter() has already been called!");
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
			throw new IllegalStateException("getOutputStream() has already been called!");
		}

		this.stream = createOutputStream();
		this.writer = new PrintWriter(new OutputStreamWriter(this.stream, this.origResponse.getCharacterEncoding()));
		return (this.writer);
	}

	public void setContentLength(int length) {
	}
}