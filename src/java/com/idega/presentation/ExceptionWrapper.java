/*
 * $Id: ExceptionWrapper.java,v 1.3 2003/05/06 16:30:41 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */

package com.idega.presentation;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.text.PreformattedText;
import com.idega.presentation.text.Text;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.2
 */
public class ExceptionWrapper extends ExpandContainer {

  private Exception _exception;
  private String _errorStyle;

  public ExceptionWrapper() {
  	super();
  }

	public ExceptionWrapper(Exception ex) {
		super();
		setException(ex);
	}

  public ExceptionWrapper(Exception ex, PresentationObject thrower) {
    setException(ex);
    add(thrower);
  }

	protected void initialize(IWContext iwc) {
		super.initialize(iwc);
		IWResourceBundle iwrb = getBundle().getResourceBundle(iwc);
		
		Text error = new Text(iwrb.getLocalizedString("error.exception_thrown","An exception was thrown"));
		if (_exception != null) {
			PreformattedText stackTrace = new PreformattedText(getStackTrace(_exception));
			if (_errorStyle != null)
				stackTrace.setStyleAttribute(_errorStyle);
			add(stackTrace);
			String errorString = _exception.getClass().getName();
			error.addToText(": "+errorString.substring(errorString.lastIndexOf(".") + 1));
		}
		setHeader(error);
	}
	
	private String getStackTrace(Exception exception) {
		StringBuffer buffer = new StringBuffer();
		StackTraceElement[] trace = exception.getStackTrace();
		for (int a = 0; a < trace.length; a++) {
			StackTraceElement element = trace[a];
			if (a > 0)
				buffer.append("\n        ");
			buffer.append(element.toString());
		}
		return buffer.toString();
	}

  public void setException(Exception exception) {
    this._exception = exception;
  }

	public void setErrorStyle(String style) {
		_errorStyle = style;
	}
}