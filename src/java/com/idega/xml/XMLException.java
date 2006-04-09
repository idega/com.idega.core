package com.idega.xml;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Idega hf
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */

public class XMLException extends Exception implements Serializable{
	
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6366683296379018233L;
private Throwable _cause = null;
	
  public XMLException() {
    super();
  }

  public XMLException(String message) {
    super(message);
  }
  
  public XMLException(String message, Throwable cause) {
	super(message);
  }
  
  
	public void printStackTrace() { 
		super.printStackTrace();
		if(this._cause != null){
			System.err.println("------ Root Cause -----");
			System.err.println(this._cause.getMessage());
			this._cause.printStackTrace();
		}
		
		
	}

	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		if(this._cause != null){
			s.println("------ Root Cause -----");
			s.println(this._cause.getMessage());
			this._cause.printStackTrace(s);
		}
	}
  
  
	public void printStackTrace(PrintWriter s) { 
		super.printStackTrace(s);
		if(this._cause != null){
			s.println("------ Root Cause -----");
			s.println(this._cause.getMessage());
			this._cause.printStackTrace(s);
		}
	}
  
  
}
