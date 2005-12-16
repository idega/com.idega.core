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

import java.io.Serializable;
import org.jdom.CDATA;

/**
 * This class does something very clever.....
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLCDATA implements Serializable{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4174364764872333264L;
	CDATA _data = null;
	
	public XMLCDATA(String content) {
		_data = new CDATA(content);
	}
	
	public XMLCDATA(CDATA data) {
		_data = data;	
	}
	
	CDATA getContentData() {
		return _data;	
	}
	
  public synchronized Object clone() {
    if (_data == null)
      return(null);

    CDATA data = (CDATA)_data.clone();
    XMLCDATA xml = new XMLCDATA(data);
    return xml;
  }
  
  public String getText() {
  	if (_data == null)
  		return null;
  		
  	return _data.getText();	
  }
  
  public XMLCDATA setText(String text) {
  	if (_data != null)
  		_data.setText(text);
  		
  	return this;
  }
}