/*
 * $Id: IWPropertyList.java,v 1.2 2001/05/10 10:45:28 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 0.8 - Under development
 */
public class IWPropertyList {
  private Document xmlDocument;
  private File xmlFile;
  private Element rootElement;
  //private static String rootElementTag = "plist";
  private static String propertyString = "property";
  private static String keyString = "key";
  private static String valueString = "value";
  private static String typeString = "type";
  private static String stringString = "java.lang.String";

  public IWPropertyList() {
  }

  public IWPropertyList(String path) {
    load(path);
  }

  public void setProperty(String key, Object value) {
    setProperty(key,value.toString(),value.getClass().getName());
  }

  public void setProperty(String key, String value) {
    setProperty(key,value,stringString);
  }

  public void setProperty(String key, int value) {
    setProperty(key, new Integer(value));
  }

  public void setProperty(String key, String value,String type) {
    Element property = findPropertyElement(key);
    if (property == null) {
      addProperty(key,value,type);
    }
    else {
      property.removeChild(valueString);
      property.removeChild(typeString);
      setProperty(property,value,type);
    }
  }

  public void addProperty(String key, String value) {
    addProperty(key,value,stringString);
  }

  public void addProperty(String key, Object value) {
    addProperty(key,value.toString(),value.getClass().getName());
  }

  public void addProperty(String key, int value) {
    addProperty(key,new Integer(value));
  }

  public void addProperty(String key, String value,String type) {
    Element property = new Element(propertyString);
    Element keyElement = new Element(keyString);
    keyElement.addContent(key);
    property.addContent(keyElement);
    setProperty(property,value,type);
    rootElement.addContent(property);
  }

  private void setProperty(Element property, String value,String type) {
    Element valueElement = new Element(valueString);
    valueElement.addContent(value);
    Element typeElement = new Element(typeString);
    typeElement.addContent(type);
    property.addContent(valueElement);
    property.addContent(typeElement);
  }

  public String getPropertyType(String key) {
    return findPropertyElement(key).getChild(typeString).getText();
  }

  public String getProperty(String key) {
    try {
      return findPropertyElement(key).getChild(valueString).getText();
    }
    catch(NullPointerException ex) {
      return null;
    }
  }

  /**
   * @return null if no match
   */
  private Element findPropertyElement(String key) {
    List list = rootElement.getChildren();
    Iterator iter = list.iterator();

    while(iter.hasNext()) {
      Element property = (Element)iter.next();
      Element keyElement = property.getChild(keyString);
      if (keyElement.getText().equalsIgnoreCase(key)) {
        return property;
      }
    }

    return null;
  }

  public void load(String path) {
    SAXBuilder builder = new SAXBuilder(false);
    xmlFile = new File(path);
    try {
      xmlDocument = builder.build(xmlFile);
    }
    catch(JDOMException e) {
      e.printStackTrace();
    }
    rootElement = xmlDocument.getRootElement();
  }

  public void store() {
    try {
      store(new FileOutputStream(xmlFile));
    }
    catch(FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void store(OutputStream stream) {
    try {
      XMLOutputter outputter = new XMLOutputter();
      outputter.output(xmlDocument,stream);
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
}
