/*
 * $Id: XMLParser.java,v 1.3 2002/12/09 18:10:51 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.io.InputStream;
import java.io.StringReader;
import java.io.File;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLParser {
  private SAXBuilder _builder = null;

  /**
   *
   */
  public XMLParser() {
    _builder = new SAXBuilder();
  }

  /**
   *
   */
  public XMLParser(boolean verify) {
    _builder = new SAXBuilder(verify);
  }

  /**
   *
   */
  public XMLDocument parse(String URI) throws XMLException {
    Document doc = null;
    try {
      doc = _builder.build(URI);
    }
    catch(JDOMException e) {
      throw new XMLException(e.getMessage());
    }

    XMLDocument xdoc = new XMLDocument(doc);

    return(xdoc);
  }

  /**
   *
   */
  public XMLDocument parse(InputStream stream) throws XMLException {
    Document doc = null;
    try {
      doc = _builder.build(stream);
    }
    catch(JDOMException e) {
      throw new XMLException(e.getMessage());
    }

    XMLDocument xdoc = new XMLDocument(doc);

    return(xdoc);
  }

  /**
   *
   */
  public XMLDocument parse(StringReader reader) throws XMLException {
    Document doc = null;
    try {
      doc = _builder.build(reader);
    }
    catch(JDOMException e) {
      throw new XMLException(e.getMessage());
    }

    XMLDocument xdoc = new XMLDocument(doc);

    return(xdoc);
  }

  /**
   *
   */
  public XMLDocument parse(File file) throws XMLException {
    Document doc = null;
    try {
      doc = _builder.build(file);
    }
    catch(JDOMException e) {
    	e.getCause().printStackTrace();
      throw new XMLException(e.getMessage());
    }

    XMLDocument xdoc = new XMLDocument(doc);

    return(xdoc);
  }
}
