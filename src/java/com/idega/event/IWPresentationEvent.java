package com.idega.event;

import java.rmi.RemoteException;
import javax.ejb.FinderException;
import com.idega.data.IDOLookup;
import com.idega.core.data.ICObjectInstance;
import com.idega.idegaweb.*;
import com.idega.presentation.*;
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
  public final static String PRM_IW_EVENT_SOURCE = "iw_ev_src";
//  private final static String SEPARATOR = "|";
  private List _parameters = new Vector();
  private Page _page = null;


  public IWPresentationEvent(){
    this(PresentationObject.NULL_CLONE_OBJECT);
  }

  public Page getPage(){
    return _page;
  }

  public void setPage(Page page){
    _page = page;
  }

  public boolean containsParameter(Parameter prm){
    return _parameters.contains(prm);
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
    if(source.getICObjectInstanceID() != 0){
      setSource(source.getICObjectInstanceID());
    } else if(source.getLocation() != null){
      setSource(source.getLocation());
    }
    //this.source = source;
  }

  public void setSource(IWLocation source){
    this.addParameter(PRM_IW_EVENT_SOURCE, source.getLocationString());

  }

  public void setSource(int instanceId){
    this.addParameter(PRM_IW_EVENT_SOURCE,instanceId);
  }

  public void setSource(ICObjectInstance instance) throws RemoteException {
    this.addParameter(PRM_IW_EVENT_SOURCE,((Integer)instance.getPrimaryKey()).toString());
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


  public static IWPresentationEvent[] getCurrentEvents(IWContext iwc){
    String[] classNames = iwc.getParameterValues(PRM_IW_EVENT);
    if(classNames != null){
      IWPresentationEvent[] events = new IWPresentationEvent[classNames.length];
      int index = 0;
      for (int i = 0; i < classNames.length; i++) {
        String className = IWMainApplication.decryptClassName(classNames[i]);

        boolean ok = false;
        IWPresentationEvent event = null;
        try {
          event = (IWPresentationEvent)Class.forName(className).newInstance();
          ok = event.initializeEvent(iwc);
        }
        catch(ClassCastException cce){
          ok = false;
          System.err.println("IWPresentationEvent ClassCastException for :"+className);
          System.err.println(cce.getMessage());
        }
        catch(ClassNotFoundException cnfe){
          ok = false;
          System.err.println("IWPresentationEvent ClassCastException for :"+className);
          System.err.println(cnfe.getMessage());
        }
        catch(IllegalAccessException iae){
          ok = false;
          System.err.println("IWPresentationEvent IllegalAccessException for :"+className);
          System.err.println(iae.getMessage());
        }
        catch(InstantiationException ie){
          ok = false;
          System.err.println("IWPresentationEvent InstantiationException for :"+className);
          System.err.println(ie.getMessage());
        }

        if(ok){
          events[index++] = event;
        }
      }
      if(index < classNames.length){
        IWPresentationEvent[] newEvents = new IWPresentationEvent[index];
        System.arraycopy(events,0,newEvents,0,index);
        return newEvents;
      } else {
        return events;
      }

    } else {
      return new IWPresentationEvent[0];
    }
  }

  public static boolean anyEvents(IWContext iwc){
    return (iwc.getParameter(PRM_IW_EVENT) != null);
  }

  public static Object getSource(IWContext iwc){
    String sourceString = iwc.getParameter(PRM_IW_EVENT_SOURCE);
    try {
      Integer primaryKey = new Integer(sourceString);
//      ICObjectInstance instance = (ICObjectInstance)IDOLookup.findByPrimaryKey(ICObjectInstance.class,primaryKey);
//      return instance;
      return primaryKey;
    }
    catch (NumberFormatException ex) {
      // Source is location
    }
//    catch (RemoteException rex) {
//      throw new RuntimeException(rex.getMessage());
//    }
//    catch (FinderException fe) {
//      throw new RuntimeException(fe.getMessage());
//    }

    return IWPresentationLocation.getLocationObject(sourceString);
  }

}