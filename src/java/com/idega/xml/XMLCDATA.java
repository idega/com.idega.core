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
}