package com.idega.event;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import java.util.*;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.Parameter;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class IWEventModel extends Object implements Cloneable {

//  public final static String PRM_EVENT_SOURCE = "em_src";
  public final static String PRM_IW_EVENT = "iw_event_type";

  List _parameters = new Vector();

  public IWEventModel(){
    initializeVariables();
    this.addParameter(PRM_IW_EVENT, IWMainApplication.getEncryptedClassName(this.getClass()));
  }

  public IWEventModel(IWContext iwc) throws NoSuchEventException{
    this();
    boolean ok = this.initializeEvent(iwc);
    if(!ok){
      NoSuchEventException ex = new NoSuchEventException("No Event of type: " + this.getClass().getName());
      throw ex;
    }
  }

  public void initializeVariables(){

  }

//  public IWEventModel(PresentationObject source) {
//    setSource(source);
//  }
//
//  public void setSource(PresentationObject source){
//    if(source.getICObjectInstanceID() != 0){
//      this.addParameter(PRM_EVENT_SOURCE, Integer.toString(source.getICObjectInstanceID()));
//    } else {
//
//    }
//  }

  protected void addParameter(String prmName, String value){
    Parameter prm = new Parameter(prmName,value);
    this.addParameter(prm);
  }

  protected void addParameter(String prmName, int value){
    Parameter prm = new Parameter(prmName,Integer.toString(value));
    this.addParameter(prm);
  }


  protected void addParameter(Parameter prm){
    _parameters.add(prm);
  }


  public Iterator getParameters(){
    return _parameters.iterator();
  }

  public abstract boolean initializeEvent(IWContext iwc);

  public Object clone(){
    IWEventModel model = null;

    try {
      model = (IWEventModel)super.clone();
      if(this._parameters != null){
        model._parameters = (List)((Vector)this._parameters).clone();
//        ListIterator iter = this._parameters.listIterator();
//        while (iter.hasNext()) {
//          int index = iter.nextIndex();
//          Object item = iter.next();
//          model._parameters.set(index,((Parameter)item).clone());
//        }
      } else {
        model._parameters = new Vector();
      }

    }
    catch(CloneNotSupportedException ex) {
      ex.printStackTrace(System.err);
    }

    return model;
  }

}