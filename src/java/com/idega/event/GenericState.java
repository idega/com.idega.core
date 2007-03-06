package com.idega.event;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class GenericState extends Object implements Cloneable,Serializable {

  public final static String STATESTRING_VALUE_SEPERATOR = "|";
  public final static String STATESTRING_MULTIVALUE_ARRAY_ELEMENT_SEPERATOR = ";";
  public final static String STATESTRING_NOVALUE = "null";

	/**
	 * 
	 * @uml.property name="state"
	 * @uml.associationEnd multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	//private static Hashtable _theAttributes = new Hashtable();
	List state = null;

//  List defaultStage = null;
  int pageKey;
  int instanceId;


  public synchronized Object clone(){

    GenericState obj = null;
    try {


      obj = (GenericState)super.clone();

      if(this.state != null){
        obj.state = (List)((Vector)this.state).clone();
      }
      obj.pageKey = this.pageKey;
      obj.instanceId = this.instanceId;

    }
    catch (CloneNotSupportedException ex) {
      ex.printStackTrace();
    }

    return obj;
  }

/*
  public GenericState(String pageKey, int instanceId , IWContext iwc) {
    this(pageKey,instanceId,iwc.getCurrentState(pageKey,instanceId));
  }

  public GenericState(String pageKey, PresentationObject obj, IWContext iwc){
    this(pageKey, obj.getICObjectInstanceID(), iwc.getCurrentState(obj));
  }

  public GenericState(String pageKey, int instanceId, String stateString){
    defaultStage = new Vector();
    this.pageKey = pageKey;
    this.instanceId = instanceId;
    parseStateString(stateString);
  }
*/

  public GenericState(PresentationObject obj, IWContext iwc){
    this(obj);
  }

  public GenericState(PresentationObject obj){
    this.state = new Vector();
    this.pageKey = obj.getParentPageID();
    this.instanceId = obj.getICObjectInstanceID();
  }

//
//  private void firstLoadInMemoryCheck() {
//    Object[][] values = (Object[][])_theAttributes.get(this.getClass().getName());
//    if (values == null) {
//      values = new Vector();
//      _theAttributes.put(this.getClass().getName(),values);
//
///*
//      //First store a static instance of this class
//      String className = this.getClass().getName();
//      try {
//        _allStaticClasses.put(className,(IDOLegacyEntity)Class.forName(className).newInstance());
//      }
//      catch(Exception ex) {
//        ex.printStackTrace();
//      }
//*/
//
//      //call the ializeAttributes that stores information about columns and relationships
//      initializeAttributes();
//    }
//  }
//
//  public void initializeAttributes(){}
//
//  public void addAttribute(String name, Class storageClass ){
//
//  }

  public void updateState(String stateString){
    parseStateString(stateString);
  }
/*
  public void setDefaultValue(int index, Object obj){
    defaultStage.set(index,obj);
  }
*/
  public Object getValue(int index){
    if(this.state.size() <= index){
      ((Vector)this.state).setSize(index+1);
    }
    return this.state.get(index);
  }

  public void setValue(int index, Object obj){
    if(this.state.size() <= index){
      ((Vector)this.state).setSize(index+1);
    }
    if(!STATESTRING_NOVALUE.equals(obj)){
      this.state.set(index,obj);
    } else {
      this.state.set(index,null);
    }
  }

  protected void parseStateString(String stateString){
    //System.err.println(this+" string to parse: "+stateString);
    StringTokenizer stoken = new StringTokenizer(stateString,STATESTRING_VALUE_SEPERATOR);
    this.state = new Vector();
    while (stoken.hasMoreTokens()) {
      String token = stoken.nextToken();
      StringTokenizer tmpToken = new StringTokenizer(token,STATESTRING_MULTIVALUE_ARRAY_ELEMENT_SEPERATOR);
      int tokenCount = tmpToken.countTokens();
      if(tokenCount > 1){
        String[] sArray = new String[tokenCount];
        for (int i = 0; i < sArray.length; i++) {
          String t = tmpToken.nextToken();
          if( !STATESTRING_NOVALUE.equals(t)){
            sArray[i] = t;
          }else{
            sArray[i] = null;
          }
        }
        this.state.add(sArray);
      } else {
        if(!STATESTRING_NOVALUE.equals(token)){
          this.state.add(token);
        } else {
          this.state.add(STATESTRING_NOVALUE);
        }
      }
    }
  }

  public String getStateString(){
    String str = "";

    if( this.state != null){
      ListIterator lIter = this.state.listIterator();
      while (lIter.hasNext()) {
        int index = lIter.nextIndex();
        Object lItem = lIter.next();
        if(index != 0){
          str += STATESTRING_VALUE_SEPERATOR;
        }
        if(lItem instanceof String[]){
          String[] obj = (String[])lItem;
          for (int i = 0; i < obj.length; i++) {
            if(i != 0){
              str += STATESTRING_MULTIVALUE_ARRAY_ELEMENT_SEPERATOR;
            }
            if(obj[i] != null){
              str += obj[i];
            } else {
              str += STATESTRING_NOVALUE;
            }
          }

        } else {
          if(lItem != null){
            str += (String)lItem;
          } else {
            str += STATESTRING_NOVALUE;
          }

        }
      }
    }
    //System.err.println(this+" statestring: "+str);
    return str;
  }


}
