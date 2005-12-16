/*
 * $Id: XMLParser.java,v 1.6 2005/12/16 17:00:41 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLParser implements Serializable{
  /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 25401278597901991L;
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
		catch(IOException e) {
			e.printStackTrace();
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
		catch(IOException e) {
			e.printStackTrace();
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
		catch(IOException e) {
			e.printStackTrace();
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
    catch(IOException e) {
    	e.printStackTrace();
			throw new XMLException(e.getMessage());
    }

    XMLDocument xdoc = new XMLDocument(doc);

    return(xdoc);
  }
}
