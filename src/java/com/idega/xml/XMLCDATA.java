/*
 * $Id:$
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import org.jdom.CDATA;

/**
 * This class does something very clever.....
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLCDATA {
	CDATA _data = null;
	
	public XMLCDATA(String content) {
		this._data = new CDATA(content);
	}
	
	public XMLCDATA(CDATA data) {
		this._data = data;	
	}
	
	CDATA getContentData() {
		return this._data;	
	}
	
  public synchronized Object clone() {
    if (this._data == null) {
		return(null);
	}

    CDATA data = (CDATA)this._data.clone();
    XMLCDATA xml = new XMLCDATA(data);
    return xml;
  }
  
  public String getText() {
  	if (this._data == null) {
		return null;
	}
  		
  	return this._data.getText();	
  }
  
  public XMLCDATA setText(String text) {
  	if (this._data != null) {
		this._data.setText(text);
	}
  		
  	return this;
  }
}