package com.idega.event;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.Parameter;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class IWPresentationEvent extends EventObject implements Cloneable {

  public final static String PRM_IW_EVENT = "iw_event_type";
  private List _parameters = new Vector();


  public IWPresentationEvent(){
    this(PresentationObject.NULL_CLONE_OBJECT);
  }

  public IWPresentationEvent(PresentationObject source){
    super(source);
    initializeVariables();
    this.addParameter(PRM_IW_EVENT, IWMainApplication.getEncryptedClassName(this.getClass()));
  }

  public IWPresentationEvent(IWContext iwc) throws NoSuchEventException{
    this();
    boolean ok = this.initializeEvent(iwc);
    if(!ok){
      NoSuchEventException ex = new NoSuchEventException("No Event of type: " + this.getClass().getName());
      throw ex;
    }
  }

  public void initializeVariables(){

  }

  protected void setSource(PresentationObject source){
    this.source = source;
  }



//  public IWPresentationEvent(PresentationObject source) {
//    this();
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
    IWPresentationEvent model = null;

    try {
      model = (IWPresentationEvent)super.clone();
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