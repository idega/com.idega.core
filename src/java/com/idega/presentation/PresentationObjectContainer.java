/*
 * $Id: PresentationObjectContainer.java,v 1.8 2002/02/25 15:51:24 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.presentation;

import com.idega.presentation.text.*;
import java.util.*;
import java.io.*;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class PresentationObjectContainer extends PresentationObject {
  protected Vector theObjects;
  protected List allObjects = null;
  protected boolean goneThroughMain = false;
  protected boolean _locked = true;
  protected String _label = null;

  public PresentationObjectContainer() {
  }

  /**
   * Add an object inside this container
   */
  protected void add(int index,PresentationObject modObject) {
    try {
      if (theObjects == null) {
        this.theObjects = new Vector();
      }
      if (modObject != null) {
        modObject.setParentObject(this);
        this.theObjects.add(index,modObject);
      }
    }
    catch(Exception ex) {
      ExceptionWrapper exep = new ExceptionWrapper(ex,this);
    }
  }

  /**
   * Add an object inside this container
   */
  public void add(PresentationObject modObject) {
    try {
      if (theObjects == null) {
        this.theObjects = new Vector();
      }
      if (modObject != null) {
        modObject.setParentObject(this);
        this.theObjects.addElement(modObject);
      }
    }
    catch(Exception ex) {
      ExceptionWrapper exep = new ExceptionWrapper(ex,this);
    }
  }

  public void add(Object moduleObject) {
    if (moduleObject instanceof PresentationObject) {
      add((PresentationObject)moduleObject);
    }
    else {
      System.err.println("Not instance of PresentationObject and therefore cannot be added to PresentationObjectContainer: " + moduleObject);
    }
  }

  public void addAtBeginning(PresentationObject modObject) {
    if (theObjects == null) {
      theObjects = new Vector();
    }
    modObject.setParentObject(this);
    theObjects.insertElementAt(modObject,0);
  }

  /**
   * Add an object inside this container - same as the add() function
   * @deprecated replaced by the add function
   */
  public void addObject(PresentationObject modObject) {
    add(modObject);
  }

  /**
   * Adds an simple string (Creates a Text object around it)
   */
  public void add(String theText) {
    add(new Text(theText));
  }

  /**
   * Adds an array of strings and creates an end of line character after each element
   */
  public void add(String[] theTextArray) {
    for (int i = 0; i < theTextArray.length; i++) {
      add(theTextArray[i]);
      addBreak();
    }
  }

  public void addBreak() {
    Text text = Text.getBreak();
    add(text);
  }

  public void addText(String theText) {
    add(new Text(theText));
  }

  public void addText(String theText, String format) {
    Text text = new Text();
    if (format != null) {
      if (format.equals("bold")) {
        text.setBold();
      }
      else if (format.equals("italic")) {
        text.setItalic();
      }
      else if (format.equals("underline")) {
        text.setUnderline();
      }
    }
    add(text);
  }

  public void addText(int integerToInsert) {
    addText(Integer.toString(integerToInsert));
  }

  public List getAllContainingObjects() {
    return theObjects;
  }

  public List getAllContainedObjectsRecursive(){
    if(allObjects == null){
      List toReturn = null;
      if(theObjects != null){
        toReturn = new Vector();
        toReturn.containsAll(theObjects);
        Iterator iter = theObjects.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          if(item instanceof PresentationObjectContainer){
            toReturn.add(item);
            //if(!toReturn.contains(item)){
              List tmp = ((PresentationObjectContainer)item).getAllContainedObjectsRecursive();
              if(tmp != null){
                toReturn.addAll(tmp);
              }
            //}
          }else{
            toReturn.add(item);
          }
        }
      }
      allObjects = toReturn;
    }
    return allObjects;
  }

  public void resetAllContainedObjectsRecursive(){
    allObjects = null;
  }

  public boolean isEmpty() {
    if (theObjects != null) {
      return theObjects.isEmpty();
    }
    else {
      return true;
    }
  }

  public void main(IWContext iwc) throws Exception {
  }

  public void _main(IWContext iwc) throws Exception {
    if(!initializedInMain){
      this.initInMain(iwc);
    }
    if (!goneThroughMain) {
      initVariables(iwc);
      try {
        //super.main(iwc);
        main(iwc);
      }
      catch(Exception ex) {
        add(new ExceptionWrapper(ex,this));
      }
      if (!isEmpty()) {
        for (int index = 0; index < numberOfObjects(); index++) {
          PresentationObject tempobj = objectAt(index);

          try {
            if (tempobj != null) {
              if (tempobj != this) {
                tempobj._main(iwc);
              }
            }
          }
          catch(Exception ex) {
            add(new ExceptionWrapper(ex,this));
          }
        }
      }
    }
    goneThroughMain = true;
  }

  /**
  * Empties the container of all PresentationObjects stored inside
  */
  public void empty() {
    if (theObjects != null) {
      theObjects.removeAllElements();
    }
  }

  protected void setObjects(Vector objects) {
    this.theObjects = objects;
  }



  /*protected void prepareClone(PresentationObject newObjToCreate){
      int number = numberOfObjects();
      for (int i = 0; i < number; i++) {
        PresentationObject tempObj = this.objectAt(i);
        ((PresentationObjectContainer)newObjToCreate).add((PresentationObject)tempObj.clone());
      }

     // if (this.theObjects!=null){
    //((PresentationObjectContainer)newObjToCreate).setObjects((Vector)this.theObjects.clone());
     // }
  }*/

  public void _print(IWContext iwc) throws Exception {
    goneThroughMain = false;
    super._print(iwc);
  }

  public void print(IWContext iwc) throws Exception {
    initVariables(iwc);
    //Workaround for JRun - JRun has hardcoded content type text/html in JSP pages
    //if(this.doPrint(iwc)){
    if (iwc.getLanguage().equals("WML")) {
      iwc.setContentType("text/vnd.wap.wml");
    }
    if (!isEmpty()) {
      int numberofObjects = numberOfObjects();
      for (int index = 0; index < numberofObjects; index++) {
        PresentationObject tempobj = objectAt(index);
        try {
          if (tempobj != null) {
            tempobj._print(iwc);
            flush();
          }
        }
        catch(Exception ex) {
          ExceptionWrapper exep = new ExceptionWrapper(ex,this);
          exep._print(iwc);
        }
      }
    }
  }

  /**
   *
   */
  public PresentationObject getContainedObject(int objectInstanceID) {
    List list = this.getAllContainingObjects();
    if (list != null) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        PresentationObject item = (PresentationObject)iter.next();
        if(item.getICObjectInstanceID()==objectInstanceID){
          return item;
        }
        else if(item instanceof PresentationObjectContainer){
          PresentationObject theReturn = ((PresentationObjectContainer)item).getContainedObject(objectInstanceID);
          if(theReturn != null){
            return theReturn;
          }
        }
      }
    }
    return null;
  }

  /**
   *
   */
  public PresentationObject getContainedObject(String objectInstanceID) {
    try {
      try {
        return(getContainedObject(Integer.parseInt(objectInstanceID)));
      }
      catch(NumberFormatException e) {
        int objectInstanceIDInt = Integer.parseInt(objectInstanceID.substring(0,objectInstanceID.indexOf(".")));

        String index = objectInstanceID.substring(objectInstanceID.indexOf(".")+1,objectInstanceID.length());
        if (index.indexOf(".") == -1) {
          return(((PresentationObjectContainer)getContainedObject(objectInstanceIDInt)).objectAt(Integer.parseInt(index)));
        }
        else {
          int xindex = Integer.parseInt(index.substring(0,index.indexOf(".")));
          int yindex = Integer.parseInt(index.substring(index.indexOf(".")+1,index.length()));

          return(((Table)getContainedObject(objectInstanceIDInt)).containerAt(xindex,yindex));
        }
      }
    }
    catch (NullPointerException ex) {
      return(null);
    }
  }

  /**
   *
   */
  public PresentationObject getContainedLabeledObject(String label) {
    List list = getAllContainingObjects();
    if (list != null) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        PresentationObject item = (PresentationObject)iter.next();
        if (item instanceof PresentationObjectContainer) {
          String itemLabel = ((PresentationObjectContainer)item).getLabel();
          if (itemLabel != null)
            if (itemLabel.equals(label))
              return(item);
          PresentationObject theReturn = ((PresentationObjectContainer)item).getContainedLabeledObject(label);
          if (theReturn != null){
            return(theReturn);
          }
        }
      }
    }
    return(null);
  }


  /*public PresentationObject getContainedObject(String objectTreeID) {
    if (objectTreeID.indexOf(".") == -1) {
      return objectAt(Integer.parseInt(objectTreeID));
    }
    else {
      String newString = objectTreeID.substring(objectTreeID.indexOf(".") + 1,objectTreeID.length());
      String index = objectTreeID.substring(0,objectTreeID.indexOf("."));

      PresentationObject obj = objectAt(Integer.parseInt(index));
      if (obj instanceof PresentationObjectContainer){
        return ((PresentationObjectContainer)obj).getContainedObject(newString);
      }
      else {
        return obj;
      }
    }
  }*/

  /*public void updateTreeIDs() {
    if (!isEmpty()) {
      String thisTreeID = this.getTreeID();
      int numberOfObjects = numberOfObjects();
      for(int index = 0; index < numberOfObjects; index++) {
        PresentationObject tempobj = objectAt(index);
        if (tempobj != null) {
          if (tempobj != this) {
            try {
              if (thisTreeID == null) {
                String treeID = Integer.toString(index);
                tempobj.setTreeID(treeID);
              }
              else {
                String treeID = thisTreeID + "." + index;
                tempobj.setTreeID(treeID);
              }
            }
            catch(Exception ex) {
              ExceptionWrapper exep = new ExceptionWrapper(ex,this);
              add(exep);
            }
          }
        }
      }
    }
  }*/

  /*public void setTreeID(String ID) {
    super.setTreeID(ID);
    updateTreeIDs();
  }*/

  public int numberOfObjects() {
    if (theObjects != null) {
      return theObjects.size();
    }
    else {
      return 0;
    }
  }

  public PresentationObject objectAt(int index) {
    if (theObjects != null) {
      return (PresentationObject)theObjects.elementAt(index);
    }
    else {
      return null;
    }
  }

  public int getIndex(PresentationObject ob) {
    if (theObjects == null)
      return(-1);
    else
      return(theObjects.indexOf(ob));
  }

  /**
   * Insert element at specified index
   */
  public void insertAt(PresentationObject modObject, int index) {
    try {
      if (theObjects == null) {
        this.theObjects = new Vector();
      }
      if (modObject != null) {
        modObject.setParentObject(this);
        theObjects.insertElementAt(modObject,index);
      }
    }
    catch(Exception ex) {
      ExceptionWrapper exep = new ExceptionWrapper(ex,this);
    }
  }

  /**
   * Replace element at specified index
   */
/*  public void setAt(PresentationObject modObject, int index) {
    try {
      if (theObjects == null) {
        this.theObjects = new Vector();
      }
      if (modObject != null) {
        modObject.setParentObject(this);
        theObjects.setElementAt(modObject,index);
      }
    }
    catch(Exception ex) {
      ExceptionWrapper exep = new ExceptionWrapper(ex,this);
    }
  }*/

  public void removeAll(java.util.Collection c) {
    if (theObjects != null)
      theObjects.removeAll(c);
  }

  public void _setIWContext(IWContext iwc){
    setIWContext(iwc);
    if ( ! isEmpty() ){
      for(int index=0;index<numberOfObjects();index++){
        PresentationObject tempobj = objectAt(index);
        if(tempobj!=null){
          if(tempobj!=this){
            tempobj._setIWContext(iwc);
          }
        }
      }
    }
  }


  public Object _clone(IWContext iwc, boolean askForPermission){
    if(askForPermission){
      if(iwc!=null && iwc.hasViewPermission(this)){
        return this.clone(iwc,askForPermission);
      } else {
        return NULL_CLONE_OBJECT;
      }
    } else {
      return this.clone();
    }
  }

 public Object clone(IWContext iwc, boolean askForPermission) {
    PresentationObjectContainer obj = null;
    try {
      obj = (PresentationObjectContainer)super.clone();
      obj._locked = this._locked;
      //if(!(this instanceof Table)){
        if (this.theObjects != null) {
            //obj.setObjects((Vector)this.theObjects.clone());
            obj.theObjects=(Vector)this.theObjects.clone();
            ListIterator iter = obj.theObjects.listIterator();
            while (iter.hasNext()) {
              int index = iter.nextIndex();
              Object item = iter.next();
              //Object item = obj.theObjects.elementAt(index);
              if(item instanceof PresentationObject){
                PresentationObject newObject = (PresentationObject) ((PresentationObject)item)._clone(iwc,askForPermission);
                newObject.setParentObject(obj);
                obj.theObjects.set(index,newObject);
              }
            }
        //}
      }
    }
    catch(Exception ex) {
      obj.theObjects = new Vector();
      ex.printStackTrace(System.err);
    }
    return obj;
  }



  public boolean remove(PresentationObject obj){
    if(theObjects!=null){
      if(theObjects.remove(obj)){
        return true;
      }
    }
    return false;
  }




  /**
   * index lies from 0,length-1
   */
  public Object set(int index,PresentationObject o){
    if(theObjects==null){
     theObjects = new Vector();
    }
    o.setParentObject(this);
    return theObjects.set(index,o);
  }

  /**
   *
   */
  public void lock() {
    _locked = true;
  }

  /**
   *
   */
  public void unlock() {
    _locked = false;
  }

  /**
   *
   */
  public boolean isLocked() {
    return(_locked);
  }

  /**
   *
   */
  public void setLabel(String label) {
    _label = label;
  }

  /**
   *
   */
  public String getLabel() {
    return(_label);
  }

  public PresentationObject getContainedICObjectInstance(int id){
/*    System.err.println("-------------------------------------");
    System.err.println("getContainedICObjectInstance("+id+")");
    if(this instanceof Page){
      System.err.println("ibpageid = "+ ((Page)this).getPageID());
    }else{
      System.err.println("this.instanceId = "+this.getICObjectInstanceID());
    }
*/
    List l = this.getAllContainedObjectsRecursive();
    if(l!=null){
      Iterator iter = l.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
//        System.err.println("ObjectinstanceID = " +((PresentationObject)item).getICObjectInstanceID());
        if(item instanceof PresentationObject && (((PresentationObject)item).getICObjectInstanceID()==id)){
          return ((PresentationObject)item);
        }
      }
    }
    return null;
  }

}
