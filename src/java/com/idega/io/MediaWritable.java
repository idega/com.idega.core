package com.idega.io;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import com.idega.presentation.IWContext;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public interface MediaWritable {

  public final static String PRM_WRITABLE_CLASS = "wrcls";
  public String getMimeType();
  public void init(HttpServletRequest req, IWContext iwc);
  public void writeTo(OutputStream out) throws java.io.IOException;

}
