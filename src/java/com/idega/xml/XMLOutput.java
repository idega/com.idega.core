/*
 * $Id: XMLOutput.java,v 1.4 2003/06/30 14:01:58 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.xml;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class XMLOutput {
  XMLOutputter _output = null;

  /**
   *
   */
  public XMLOutput() {
    _output = new XMLOutputter();
  }

  /**
   *
   */
  public XMLOutput(String indent, boolean newlines) {
    _output = new XMLOutputter(indent,newlines);
  }

  /**
   *
   */
  public void setLineSeparator(String seperator) {
    if (_output != null)
      _output.setLineSeparator(seperator);
  }

  /**
   *
   */
  public void setTextNormalize(boolean normalize) {
    if (_output != null)
      _output.setTextNormalize(normalize);
  }

  /**
   *
   */
  public void output(XMLDocument document, OutputStream stream) throws IOException {
    if (_output != null)
      _output.output(document.getDocument(),stream);
  }
  
  public void output(XMLDocument document, Writer writer) throws IOException {
    if (_output != null)
      _output.output(document.getDocument(), writer);
  }

  public String outputString(XMLElement element) {
    if (_output != null) {
      Element el = element.getElement();
      if (el != null)
        return(_output.outputString(el));
      else
        return(null);
    }
    else
      return(null);
  }
  
  public void setEncoding(String encoding) {
  	if (_output != null)
  		_output.setEncoding(encoding);
  }
  
  public void setSkipEncoding(boolean skip) {
  	if (_output != null)
  		_output.setOmitEncoding(skip);
  }
}