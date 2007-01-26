/*
 * $Id: XMLAttribute.java,v 1.5 2007/01/26 05:43:44 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.io.Serializable;
import org.jdom.Attribute;
import org.jdom.DataConversionException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLAttribute implements Serializable{
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1883155348794852652L;
private Attribute _attribute = null;

  public XMLAttribute(String name, String value) {
    this._attribute = new Attribute(name,value);
  }

  XMLAttribute(Attribute attribute) {
    this._attribute = attribute;
  }

  public String getName() {
    if (this._attribute != null) {
			return(this._attribute.getName());
		}

    return(null);
  }

  public String getValue() {
    if (this._attribute != null) {
			return(this._attribute.getValue());
		}

    return(null);
  }
  
  public void setValue(String value) {
	  if (this._attribute == null) {
		  return;
	  }
	  this._attribute.setValue(value);
  }

  public int getIntValue() throws XMLException {
    try {
      if (this._attribute != null) {
				return(this._attribute.getIntValue());
			}
    }
    catch(DataConversionException e) {
      throw new XMLException(e.getMessage());
    }

    return(0);
  }

  Attribute getAttribute() {
    return(this._attribute);
  }

  void setAttribute(Attribute attribute) {
    this._attribute = attribute;
  }
}
