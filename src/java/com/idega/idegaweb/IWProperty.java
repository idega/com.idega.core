//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


import java.util.List;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.8 - Under development
*/
public class IWProperty{

  private Element propertyElement;
  private IWPropertyList parentList;

   static String mapTag = IWPropertyList.mapTag;
   static String nameTag = IWPropertyList.nameTag;
   static String arrayTag = IWPropertyList.arrayTag;
   static String keyTag = IWPropertyList.keyTag;
   static String valueTag = IWPropertyList.valueTag;
   static String typeTag = IWPropertyList.typeTag;
   static String stringTag = IWPropertyList.stringTag;
   static String stringString = IWPropertyList.stringString;
   public static String MAP_TYPE = mapTag;

  IWProperty(IWPropertyList parentList){
    this(null,parentList);
  }

  IWProperty(Element propertyElement,IWPropertyList parentList){
    this.propertyElement = propertyElement;
    this.parentList=parentList;
  }

  private IWPropertyList getParentList(){
    return parentList;
  }

  public String getKey(){
     return this.getName();
  }

  public String getName(){
     return this.getPropertyName(getKeyElement());
  }

  public String getType(){
    return this.getPropertyType(getKeyElement());
  }

  public boolean getBooleanValue(){
    String value = getValue();
    if(value!=null){
      if(value.equalsIgnoreCase("true")){
        return true;
      }
      else if(value.equalsIgnoreCase("false")){
        return false;
      }
      else if(value.equalsIgnoreCase("y")){
        return true;
      }
      else if(value.equalsIgnoreCase("n")){
        return false;
      }
      else{
        return false;
      }
    }
    return false;


  }

  public String getValue(){
    return this.getPropertyValue(getKeyElement());
  }

  public void setValue(String sValue){
      Element key = getKeyElement();
      Element value = null;
      if(key==null){
        key = createKeyElement();
        value = new Element(valueTag);
      }
      else{
        value = key.getChild(valueTag);
        if(value == null){
          value = new Element(valueTag);
        }
      }
      value.setText(sValue);
      key.addContent(value);
      setType(stringString);
  }

  public void setValue(int iValue){
    setValue(new Integer(iValue));
  }

  public void setValue(boolean bValue){
    setValue(new Boolean(bValue));
  }

  public void setValue(Object oValue){
      Element key = getKeyElement();
      Element value = null;
      if(key==null){
        key = createKeyElement();
        value = new Element(valueTag);
      }
      else{
        value = key.getChild(valueTag);
        if(value == null){
          value = new Element(valueTag);
        }
      }
      value.setText(oValue.toString());
      key.addContent(value);
      setType(oValue.getClass().getName());
  }

  private void setType(String sType){
    setType(getKeyElement(),getParentList(),sType);
  }

  static void setType(Element key,IWPropertyList plist,String sType){
      Element type = null;
      if(key==null){
        key = createKeyElement(plist);
        type = new Element(typeTag);
      }
      else{
        type = key.getChild(typeTag);
        if(type == null){
          type = new Element(typeTag);
        }
      }
      type.setText(sType);
      key.addContent(type);
  }

  public void setName(String sName){
      Element key = getKeyElement();
      Element name = null;
      if(key==null){
        key = createKeyElement();
        name = new Element(nameTag);
      }
      else{
        name = key.getChild(nameTag);
        if(name == null){
          name = new Element(nameTag);
        }
      }
      name.setText(sName);
      key.addContent(name);
  }


  public void setProperty(String key, int value) {
    setProperty(key,new Integer(value));
  }

  public void setProperty(String key, boolean value) {
    setProperty(key,new Boolean(value));
  }

  public void setProperty(String key, String value) {
    setProperty(key,value,stringString);
  }

  public void setProperty(String key,Object[] value){
    Element keyElement = this.getKeyElement();
    setProperty(keyElement,key,value,getParentList());
  }


  private void setProperty(String key, Object value,String type) {
    Element keyElement = getKeyElement();
    setProperty(keyElement,key,value,type,getParentList());
  }

  static void setProperty(Element keyElement,String key, Object value,String type,IWPropertyList list) {
    if (keyElement == null) {
      addProperty(key,value,type,list);
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


  static void setProperty(Element keyElement,String key,Object[] value,IWPropertyList list){
    if (keyElement == null) {
      addProperty(key,value,arrayTag,list);
    }
    else {
      keyElement.removeChild(nameTag);
      keyElement.removeChild(valueTag);
      keyElement.removeChild(typeTag);
      addNewProperty(keyElement,key,value,arrayTag);
    }
  }

  public void setProperty(String key, Object value) {
    setProperty(key,value.toString(),value.getClass().getName());
  }

  /**
   * Throws IWNotPropertyListException if this IWProperty has a Single Property not a PropertyList
   */
  public IWPropertyList getPropertyList() throws IWNotPropertyListException{
    return getPropertyList(getKeyElementAndCreateIfNotExists());
  }


  /**
   * Throws IWNotPropertyListException if this IWProperty has a Single Property not a PropertyList
   */
  static IWPropertyList getPropertyList(Element keyElement)throws IWNotPropertyListException{
    Element valueElement = getValueElement(keyElement);
    String type = getPropertyType(keyElement);
    if(type!=null){
      if(type.equals(mapTag)){
        if(valueElement!=null){
          return new IWPropertyList(valueElement);
        }
      }
      else{
        throw new IWNotPropertyListException(getPropertyName(keyElement));
      }
    }
    return null;
  }

  public IWPropertyList getNewPropertyList(String key){
    setName(key);
    return getNewPropertyList();
  }


  public IWPropertyList getNewPropertyList(){
    return getNewPropertyList(getKeyElementAndCreateIfNotExists(),getParentList());
  }

  static IWPropertyList getNewPropertyList(Element keyElement,IWPropertyList plist){
    Element valueElement = getValueElement(keyElement);
    if(valueElement!=null){
      valueElement.removeChildren();
      IWPropertyList list = new IWPropertyList(valueElement);
      setType(keyElement,plist,mapTag);
      return list;
    }
    return null;
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

  private void addProperty(String key, Object value,String type) {
    addProperty(key,value,type,getParentList());
  }

  static void addProperty(String key, Object value,String type,IWPropertyList plist) {
    Element keyElement = createKeyElement(plist);
    addNewProperty(keyElement,key,value,type);
  }

  private Element getKeyElementAndCreateIfNotExists(){
    Element key = getKeyElement();
    if(key==null){
      key = createKeyElement();
    }
    return key;
  }

  private Element createKeyElement(){
    this.propertyElement = createKeyElement(getParentList());
    return propertyElement;
  }

  static Element createKeyElement(IWPropertyList list){
       Element keyElement = new Element(keyTag);
       if(list!=null){
        list.getMapElement().addContent(keyElement);
       }
       return keyElement;
  }

  static Element createKeyElement(IWPropertyList list,String keyName){
       Element keyElement = new Element(keyTag);
       Element nameElement = new Element(nameTag);
       nameElement.addContent(keyName);
       keyElement.addContent(nameElement);
       if(list!=null){
        list.getMapElement().addContent(keyElement);
       }
       return keyElement;
  }

  private Element getKeyElement(){
    return this.propertyElement;
  }

  static String getPropertyName(Element keyElement) {
    if(keyElement!=null)
      return keyElement.getChild(nameTag).getText();
    return null;
  }

  static String getPropertyType(Element keyElement) {
    if(keyElement!=null){
      Element child = keyElement.getChild(typeTag);
      if(child!=null)
        return child.getText();
    }
    return null;
  }

  static String getPropertyValue(Element keyElement) {
    if(keyElement!=null)
      return keyElement.getChild(valueTag).getText();
    return null;
  }

  static Element createArrayElement(Element valueElement){
    Element arrayElement = new Element(arrayTag);
    valueElement.addContent(arrayElement);
    return arrayElement;
  }

  private Element getValueElement(){
    return  getValueElement(getKeyElement());
  }

  static Element getValueElement(Element keyElement){
    Element value = keyElement.getChild(valueTag);
    if(value==null){
      value = createValueElement(keyElement);
    }
    return value;
  }

  private Element createValueElement(){
    return createValueElement(getKeyElement());
  }

  static Element createValueElement(Element parent){
    Element valueElement = new Element(valueTag);
    parent.addContent(valueElement);
    return valueElement;
  }

  static boolean valueContains(Element valueElement,Object value){
    return valueElement.getText().equals(value.toString());
  }

  static void setValue(Element valueElement,Object value){
    valueElement.addContent(value.toString());
  }

  static void addNewProperty(Element key, String keyName,Object value,String type) {
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

/*

  private Document xmlDocument;
  private File xmlFile;
  private Element rootElement;
  //private static String rootElementTag = "plist";
  private static String propertyString = "property";
  private static String keyString = "key";
  private static String valueString = "value";
  private static String typeString = "type";
  private static String stringString = "java.lang.String";

  public IWProperty(){
  }

   public IWProperty(IWPropertyList list){
//     load(path);
   }

   public void setProperty(String key, Object value){
      setProperty(key,value.toString(),value.getClass().getName());
   }

   public void setProperty(String key, String value){
      setProperty(key,value,stringString);
   }

   public void setProperty(String key, int value){
      setProperty(key, new Integer(value));
   }

   public void setProperty(String key, String value,String type){
      Element property = findPropertyElement(key);
      if(property==null){
        addProperty(key,value,type);
      }
      else{
        property.removeChild(valueString);
        property.removeChild(typeString);
        setProperty(property,value,type);
      }
   }

   public void addProperty(String key, String value){
      addProperty(key,value,stringString);
   }

   public void addProperty(String key, Object value){
      addProperty(key,value.toString(),value.getClass().getName());
   }

  public void addProperty(String key, int value){
      addProperty(key,new Integer(value));
  }

   public void addProperty(String key, String value,String type){
      Element property = new Element(propertyString);
      Element keyElement = new Element(keyString);
      keyElement.addContent(key);
      property.addContent(keyElement);
      setProperty(property,value,type);
      rootElement.addContent(property);
   }

   private void setProperty(Element property, String value,String type){
      Element valueElement = new Element(valueString);
      valueElement.addContent(value);
      Element typeElement = new Element(typeString);
      typeElement.addContent(type);
      property.addContent(valueElement);
      property.addContent(typeElement);
   }

   public String getPropertyType(String key){
      return findPropertyElement(key).getChild(typeString).getText();
   }

   public String getProperty(String key){
      try{
        return findPropertyElement(key).getChild(valueString).getText();
      }
      catch(NullPointerException ex){
        return null;
      }
   }

    //Returns null if no match
   private Element findPropertyElement(String key){
        List list = rootElement.getChildren();
        Iterator iter = list.iterator();

        while(iter.hasNext()){
          Element property = (Element)iter.next();
          Element keyElement = property.getChild(keyString);
          if(keyElement.getText().equalsIgnoreCase(key)){
              return property;
          }
        }
        return null;
   }

*/


}
