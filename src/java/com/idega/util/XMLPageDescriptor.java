/*
 * $Id: XMLPageDescriptor.java,v 1.1 2001/05/15 09:38:31 palli Exp $
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




  /**
   * A test method. Will be removed.
   *
   * @param filename Name of the XML file to read and display the tree for.
   */
  public void ReadDocument(String filename) {
    try {
      SAXBuilder b = new SAXBuilder();
      Document doc = b.build(filename);

      Element rootElement = doc.getRootElement();
      List rootAttributes = rootElement.getAttributes();
      List rootChildren = rootElement.getChildren();
      Iterator it = rootAttributes.iterator();

      System.out.println("Root: " + rootElement.getName());

      while (it.hasNext()) {
        Attribute at = (Attribute)it.next();
        System.out.println("At: " + at.getName() + " = " + at.getValue());
      }

      it = rootChildren.iterator();
      while (it.hasNext()) {
        Element e = (Element)it.next();
        parseElements(e, "");
      }
    }
    catch(org.jdom.JDOMException e) {
      e.printStackTrace();
    }
  }

  /*
   * Parses the Elements recursively and prints out information
   * about the element, its attributes and the values it contains.
   *
   * @param e The element to parse.
   * @param space The number of spaces to insert in front of the text printed out.
   */
  private void parseElements(Element e, String space) {
    space = space.concat(" ");
    System.out.println(space + "El: " + e.getName());
    List attr = e.getAttributes();
    Iterator it = attr.iterator();
    while (it.hasNext()) {
      Attribute at = (Attribute)it.next();
      System.out.println(space + "At: " + at.getName() + " = " + at.getValue());
    }

    String s = e.getTextTrim();
    if (s != null && !s.equals(""))
      System.out.println(space + "Val : " + s);

    if (e.hasChildren()) {
      List children = e.getChildren();
      it = children.iterator();
      while (it.hasNext()) {
        Element el = (Element)it.next();
        parseElements(el,space);
      }
    }

    System.out.println(space + "El: /" +  e.getName());
  }

  public static void main(String args[]) {
    XMLPageDescriptor x = new XMLPageDescriptor(true);
    x.ReadDocument(args[0]);
  }
}

/*      SAXParserFactory f = SAXParserFactory.newInstance();
      f.setValidating(true);
      SAXParser p = f.newSAXParser();
      Parser p1 = p.getParser();
      p1.parse();*/
//      System.out.println("Document = " + doc.toString());
