/*
 * $Id: XMLPageDescriptor.java,v 1.2 2001/05/16 18:58:06 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.util;

import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.input.SAXBuilder;

import java.util.List;
import java.util.Iterator;

import com.idega.exception.PageDescriptionDoesNotExists;

/**
 * A class that reads XML page descriptions from the database and returns
 * the elements/modules/applications it contains.
 *
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0alpha
 */

public class XMLPageDescriptor {

  private SAXBuilder builder = null;
  private Document xmlDocument = null;
  private Element rootElement = null;

  public XMLPageDescriptor(boolean verify) {
    builder = new SAXBuilder(verify);
  }

  /**
   * Sets the ...
   *
   * @param URI The path to the file containing the XML description of the page.
   *
   * @throws com.idega.exception.PageDescriptionDoesNotExists The given XML file does not exists.
   */
  public void setXMLPageDescriptionFile(String URI) throws PageDescriptionDoesNotExists {
    try {
      xmlDocument = builder.build(URI);
      rootElement = xmlDocument.getRootElement();
    }
    catch(org.jdom.JDOMException e) {
      System.err.println("JDOM Exception: " + e.getMessage());
      throw new PageDescriptionDoesNotExists();
    }
  }

  /**
   * A function that returns the root element for the given page description file.
   *
   * @return The root element. Null if the page description file is not set.
   * @todo Wrap the Element class to hide all implementation of the XML parser.
   */
  public Element getRootElement() {
    return(rootElement);
  }

  /**
   * A function that returns a list of child elements for a given element.
   *
   * @param element
   * @return A List of elements. Null if the element has no children or is null.
   * @todo Wrap the Element class to hide all implementation of the XML parser.
   */
  public List getChildren(Element element) {
    if (element == null)
      return(null);

    if (!element.hasChildren())
      return(null);

    List li = element.getChildren();

    return(li);
  }

  public List getAttributes(Element element) {
    if (element == null)
      return(null);

    List li = element.getAttributes();

    return(li);
  }
}

/*      SAXParserFactory f = SAXParserFactory.newInstance();
      f.setValidating(true);
      SAXParser p = f.newSAXParser();
      Parser p1 = p.getParser();
      p1.parse();*/
//      System.out.println("Document = " + doc.toString());
