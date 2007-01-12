/*
 * $Id: XMLAttribute.java,v 1.2.6.1 2007/01/12 19:32:21 idegaweb Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import org.jdom.Attribute;
import org.jdom.DataConversionException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLAttribute {
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
