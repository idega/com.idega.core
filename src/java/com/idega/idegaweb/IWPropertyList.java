/*
 * $Id: IWPropertyList.java,v 1.5 2001/06/18 15:49:46 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.idegaweb;

import java.util.List;
import java.util.Vector;
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
import com.idega.util.FileUtil;

/**
 *@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 *@version 0.8 - Under development
 */
public class IWPropertyList {
  private Document xmlDocument;
  private File xmlFile;
  private Element rootElement;
  private Element dict;
  private static String rootElementTag = "pxml";
  private static String dictTag = "dict";
  private static String nameTag = "name";
  private static String arrayTag = "array";
  private static String keyTag = "key";
  private static String valueTag = "value";
  private static String typeTag = "type";
  private static String stringTag = "string";
  private static String stringString = "java.lang.String";

  public IWPropertyList(){
  }

  public IWPropertyList(String fileNameWithPath) {
    load(fileNameWithPath);
  }

  /**
   * Creates the file and superfolders if createFileAndFolder is true
   */
  public IWPropertyList(String path,String fileNameWithoutFullPath,boolean createFileAndFolder) {
    File file=null;
    if(createFileAndFolder){
      try{
        file = new File(path,fileNameWithoutFullPath);
        if(!file.exists()){
          file = FileUtil.getFileAndCreateIfNotExists(path,fileNameWithoutFullPath);
          FileOutputStream stream = new FileOutputStream(file);
          char[] array = ((String)"<"+rootElementTag+"></"+rootElementTag+">").toCharArray();
          for (int i = 0; i < array.length; i++) {
            stream.write((int)array[i]);
          }
          stream.flush();
          stream.close();
        }


      }
      catch(IOException ex){
        ex.printStackTrace();
      }
    }
    else{
        file = new File(path+FileUtil.getFileSeparator()+fileNameWithoutFullPath);
    }
    load(file);
  }

  public IWPropertyList(File file){
    load(file);
  }

  public void setProperty(String key, Object value) {
    setProperty(key,value.toString(),value.getClass().getName());
  }

  public void setProperty(String key, String value) {
    setProperty(key,value,stringString);
  }

  public void setProperty(String key,Object[] value){
      //setProperty(key,value,"array");
    Element keyElement = findKeyElement(key);
    if (keyElement == null) {
      addProperty(key,value,arrayTag);
    }
    else {
      keyElement.removeChild(nameTag);
      keyElement.removeChild(valueTag);
      keyElement.removeChild(typeTag);
      addNewProperty(keyElement,key,value,arrayTag);
    }
  }

  public void setProperty(String key, int value) {
    setProperty(key, new Integer(value));
  }

  private Element createValueElement(Element parent){
    Element valueElement = new Element(valueTag);
    parent.addContent(valueElement);
    return valueElement;
  }

  private Element createArrayElement(Element valueElement){
    Element arrayElement = new Element(arrayTag);
    valueElement.addContent(arrayElement);
    return arrayElement;
  }


  /**
   * Use to set an array property with only one "String" value to begin with
   */
  public void setArrayProperty(String key, Object value){
      setProperty(key,value,arrayTag);
  }


  private void setProperty(String key, Object value,String type) {
    Element keyElement = findKeyElement(key);
    if (keyElement == null) {
      addProperty(key,value,type);
    }
    else {
      Element typeElement = keyElement.getChild(typeTag);
      if(typeElement.getText().equals(arrayTag)){
        Element valueElement = keyElement.getChild(valueTag);
      }
      else{
        keyElement.removeChild(nameTag);
        keyElement.removeChild(valueTag);
        keyElement.removeChild(typeTag);
        addNewProperty(keyElement,key,value,type);
      }
    }
  }

  private void addProperty(String key, String value) {
    addProperty(key,value,stringString);
  }

  private void addProperty(String key, Object value) {
    addProperty(key,value.toString(),value.getClass().getName());
  }

  private void addProperty(String key, int value) {
    addProperty(key,new Integer(value));
  }

  private Element createKeyElement(){
       Element keyElement = new Element(keyTag);
       dict.addContent(keyElement);
       return keyElement;
  }

  private void addProperty(String key, Object value,String type) {
    //Element property = new Element(dictTag);
    Element keyElement = createKeyElement();
    //keyElement.addContent(key);
    addNewProperty(keyElement,key,value,type);
    //rootElement.addContent(property);
  }



  private void addNewProperty(Element key, String keyName,Object value,String type) {
    Element nameElement = new Element(nameTag);
    nameElement.addContent(keyName);
    Element typeElement = new Element(typeTag);
    typeElement.addContent(type);
    Element valueElement = new Element(valueTag);
    if(type.equals(arrayTag)){
      Element arrayElement = new Element(arrayTag);
      valueElement.addContent(arrayElement);
      try{
        Object[] theArray = (Object[])value;
        for (int i = 0; i < theArray.length; i++) {
            Element newValueElement = new Element(valueTag);
            setValue(newValueElement,theArray[i]);
            arrayElement.addContent(newValueElement);
        }
      }
      catch(ClassCastException ex){
            Element newValueElement = new Element(valueTag);
            setValue(newValueElement,value);
            arrayElement.addContent(newValueElement);
      }

    }
    else{

    setValue(valueElement,value);

    }
    key.addContent(nameElement);
    key.addContent(typeElement);
    key.addContent(valueElement);
  }

  public String getPropertyType(String key) {
    return findKeyElement(key).getChild(typeTag).getText();
  }

  /**
   * Returns null if key not found
   */
  public String getProperty(String key) {
    try {
      return findKeyElement(key).getChild(valueTag).getText();
    }
    catch(NullPointerException ex) {
      return null;
    }
  }

  /**
   * @return null if no match
   */
  private Element findKeyElement(String key) {
    List list = dict.getChildren();
    Iterator iter = list.iterator();

    while(iter.hasNext()) {
      Element keyElement = (Element)iter.next();
      Element nameElement = keyElement.getChild(nameTag);
      if (nameElement.getText().equalsIgnoreCase(key)) {
        return keyElement;
      }
    }
    return null;
  }

// added by Eirikur Hrafnsson eiki@idega.is
  protected Vector getKeys() {
    List list = dict.getChildren();
    Iterator iter = list.iterator();
    Vector keys = new Vector();

    while(iter.hasNext()) {
      Element keyElement = (Element)iter.next();
      Element nameElement = keyElement.getChild(nameTag);
      keys.addElement( nameElement.getText() );
    }

    return keys;
  }

  public void load(String path) {
    File file =new File(path);
    load(file);
  }

  public void load(File file){
    SAXBuilder builder = new SAXBuilder(false);
    xmlFile = file;
    try {
      xmlDocument = builder.build(xmlFile);
    }
    catch(JDOMException e) {
      e.printStackTrace();
    }
    catch(Throwable e) {
      e.printStackTrace();
    }
    rootElement = xmlDocument.getRootElement();
    dict = rootElement.getChild(dictTag);
    if(dict==null){
      dict = new Element(dictTag);
      rootElement.addContent(dict);
    }
  }

  public void removeProperty(String key){
    Element element = this.findKeyElement(key);
    if(element!=null){
      dict.removeContent(element);
    }
  }

  private boolean valueContains(Element valueElement,Object value){
    return valueElement.getText().equals(value.toString());
  }

  private void setValue(Element valueElement,Object value){
    valueElement.addContent(value.toString());
  }

  /**
   * Returns null if no match
   */

  private Element getArrayValueElement(Element arrayElement,Object value){
    List arrayList = arrayElement.getChildren();
    Iterator iter = arrayList.iterator();
    while (iter.hasNext()) {
      Element item = (Element)iter.next();
      if (valueContains(item,value)){
        return item;
      }
    }
    return null;
  }

  public Object getValueObject(Element valueElement){
    return getValueString(valueElement);
  }

  public String getValueString(Element valueElement){
    return valueElement.getText();
  }

  public void removeProperty(String key, Object value){
    Element element = this.findKeyElement(key);
    if(element!=null){
      Element typeElement = element.getChild(typeTag);
      Element valueElement = element.getChild(valueTag);
      /**
       * if it is an array
       */
      if(typeElement.getText().equals(arrayTag)){
        Element arrayElement = valueElement.getChild(arrayTag);
        Element newValueElement = this.getArrayValueElement(arrayElement,value);
        if(newValueElement!=null){
          arrayElement.removeContent(newValueElement);
        }
      }
      else{
        if(valueElement.getText().equals(value.toString())){
          dict.removeContent(element);
        }
      }
    }
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
      XMLOutputter outputter = new XMLOutputter("  ",true);
      outputter.setLineSeparator(System.getProperty("line.separator"));
      outputter.output(xmlDocument,stream);
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
}
