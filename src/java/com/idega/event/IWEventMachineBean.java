package com.idega.event;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import com.idega.business.IBOSessionBean;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWLocation;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 **/

public class IWEventMachineBean extends IBOSessionBean implements IWEventMachine {

	/**
	 * 
	 * @uml.property name="_stateMap"
	 * @uml.associationEnd multiplicity="(0 1)" qualifier="idObj:java.lang.Object list:javax.swing.event.EventListenerList"
	 */
	private Map _stateMap = new Hashtable();

//
//  private int _historyID = 0;
//
//  public void setHistoryID(int historyId ){
//    _historyID = historyId;
//  }
//
//  public void newState(){
//    increaseHistoryID();
//  }
//
//  public void increaseHistoryID(){
//    _historyID++;
//  }

  public EventListenerList getListenersFor(ICObjectInstance instance){
    //try {
      return getListenersFor(instance.getPrimaryKey());
    //}
    //catch (RemoteException ex) {
    //  throw new RuntimeException(ex.getMessage());
    //}
  }

//  private EventListenerList getListenersFor(int idObj){
//    return getListenersFor(new Integer(idObj));
//  }

  public EventListenerList getListenersFor(IWLocation location){
    return getListenersFor((Object)location);
  }

  public EventListenerList getListenersFor(String location){
    return getListenersFor((Object)location);
  }


  public EventListenerList getListenersForCompoundId(String compoundId)  {
    return getListenersFor(compoundId);
  }




  private EventListenerList getListenersFor(Object idObj){
//    System.out.println("getListenersFor(): get -> "+idObj);
    EventListenerList list = (EventListenerList)this.getUserStatesMap().get(idObj);
    if(list==null){
      list = new EventListenerList();
//      System.out.println("getListenersFor(): initialize for -> "+idObj);
      getUserStatesMap().put(idObj,list);
    }

//    Object[] arr = list.getListenerList();
//    if(arr.length == 0){
//      System.out.println("IWEventMachine: arr.length = 0");
//    }
//    for (int i = 0; i < arr.length; i++) {
//      System.out.println("IWEventMachine: arr["+i+"]:"+arr[i]);
//    }


    return list;
  }

  private Map getUserStatesMap(){
//    String mapKey = "iw_user_event_listener_map";
//    Map stateMap = (Map)getUserContext().getSessionAttribute(mapKey);
//    if(stateMap == null){
//      stateMap = new Hashtable();
//      getUserContext().setSessionAttribute(mapKey,stateMap);
//    }
//    System.out.println("getUserStatesMap()._stateMap.isEmpty(): "+_stateMap.isEmpty());



    if(!this._stateMap.isEmpty()){
      Set set = this._stateMap.keySet();
      Iterator iter = set.iterator();
      int counter = 1;
      while (iter.hasNext()) {
        iter.next();
//        System.out.println("_stateMap key"+counter+" contained = "+item);
//        System.out.println("_stateMap key"+counter+".hashCode() = "+item.hashCode());
//        System.out.println("_stateMap key"+counter+".getClass() = "+item.getClass());
        counter++;
      }
    }
    return this._stateMap;
  }



  public void processEvent(Page page, IWContext iwc) {
//    System.out.println("-------------processEvent begins-----------------------");

//    System.out.println("getEventListenerList: machine = "+ this);

    IWPresentationEvent[] events = IWPresentationEvent.getCurrentEvents(iwc);

//    System.out.println("Events: " + events);

    for (int i = 0; i < events.length; i++) {
      events[i].setPage(page);
	  events[i].setIWContext(iwc);
    }

    Object id = IWPresentationEvent.getSource(iwc);

//    System.out.println("ID: " + id);

//    if(id instanceof IWLocation && ((IWLocation)id).isInFrameSet()){
//ChageListener
//    }

    if(id != null){
      EventListenerList list = this.getListenersFor(id);
//      System.out.println("EventListenerList: " + list);
      if(list != null){
        IWActionListener[] listeners = (IWActionListener[])list.getListeners(IWActionListener.class);
//        System.out.println("listeners: " + listeners);
//        System.out.println("listeners.length: " + listeners.length);
        for (int i = 0; i < listeners.length; i++) {
//          System.out.println("listeners["+i+"]: " + listeners[i]);
          for (int j = 0; j < events.length; j++) {
            try {
//              System.out.println("events["+j+"]: " + events[j]);
              listeners[i].actionPerformed(events[j]);
            }
            catch (IWException ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    }

//    System.out.println("---------------processEvent ends---------------------");
  }






}