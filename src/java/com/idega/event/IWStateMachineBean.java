package com.idega.event;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;
import javax.swing.event.ChangeListener;

import com.idega.business.IBOLookup;
import com.idega.business.IBOSessionBean;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;
import com.idega.presentation.Page;
import com.idega.presentation.StatefullPresentation;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWStateMachineBean extends IBOSessionBean implements IWStateMachine {

  private int _historyID = 0;

	/**
	 * 
	 * @uml.property name="_stateMap"
	 * @uml.associationEnd multiplicity="(0 -1)" ordering="ordered" elementType="com.idega.event.IWPresentationState"
	 * qualifier="location:com.idega.idegaweb.IWLocation state:com.idega.idegaweb.browser.presentation.IWControlFramePresentationState"
	 */
	private Map _stateMap = new Hashtable();

  public void setHistoryID(int historyId ){
    _historyID = historyId;
  }

  public void newState(){
    increaseHistoryID();
  }

  public void increaseHistoryID(){
    _historyID++;
  }

//  public IWPresentationState getStateFor(ICObjectInstance instance){
//    IWPresentationState state = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(instance);
//    if(state==null){
//      state = state = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(instance);
//      if(state==null){
//        state = initializeState(instance);
//        getGlobalStatesMap(getUserContext().getApplicationContext()).put(instance,state);
//      }
//    }
//    return state;
//  }

//  public IWPresentationState getStateFor(ICObjectInstance instance){
//    return getStateFor((Object)instance);
//  }



  private IWPresentationState initializeState(ICObject obj)throws RuntimeException{
    try{
      Class stateClass = lookupStateClassForInstance(obj);
      if(stateClass != null){
        return (IWPresentationState)stateClass.newInstance();
      } else {
        System.err.println("IWStateMachine.initializeState(instance): stateClass == null");
        return null;
      }

    }
    catch(RemoteException re){
      throw new RuntimeException(re.getMessage());
    }
    catch(FinderException fe){
      throw new RuntimeException(fe.getMessage());
    }
    catch(ClassNotFoundException cnfe){
      throw new RuntimeException(cnfe.getMessage());
    }
    catch(IllegalAccessException iae){
      throw new RuntimeException(iae.getMessage());
    }
    catch(InstantiationException ie){
      throw new RuntimeException(ie.getMessage());
    }
  }

  private Class lookupStateClassForInstance(ICObject obj) throws RemoteException, FinderException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    String className = obj.getClassName();
    return ((StatefullPresentation)Class.forName(className).newInstance()).getPresentationStateClass();
  }

//  private Map getUserStatesMap(){
//    String mapKey = "iw_user_state_map";
//    Map stateMap = (Map)getUserContext().getSessionAttribute(mapKey);
//    if(stateMap == null){
//      stateMap = new Hashtable();
//      getUserContext().setSessionAttribute(mapKey,stateMap);
//    }
//    return stateMap;
//  }

  private Map getUserStatesMap(){
//    String mapKey = "iw_user_event_listener_map";
//    Map stateMap = (Map)getUserContext().getSessionAttribute(mapKey);
//    if(stateMap == null){
//      stateMap = new Hashtable();
//      getUserContext().setSessionAttribute(mapKey,stateMap);
//    }
    if(_stateMap == null){
      _stateMap = new Hashtable();
    }

//    if(_stateMap != null){
//      System.out.println("StateMachine: _stateMap is "+_stateMap);
//      Iterator iter = _stateMap.keySet().iterator();
//      int counter = 1;
//      while (iter.hasNext()) {
//        Object item = iter.next();
//        System.out.println("StateMachine item"+(counter++)+": "+item);
//      }
//    } else {
//      System.out.println("StateMachine: _stateMap is null");
//    }

    return _stateMap;
  }

  /**
   * temp
   */
//  private Map getUserStatesMap(Object obj){
//    if(_stateMap == null){
//      _stateMap = new Hashtable();
//    }
//
//    if(_stateMap != null){
//      //System.out.println("StateMachine: _stateMap is "+_stateMap);
//      Iterator iter = _stateMap.keySet().iterator();
//      int counter = 1;
//      while (iter.hasNext()) {
//        Object item = iter.next();
//        System.out.println("StateMachine item"+(counter)+": "+item);
//        System.out.println(item.equals(obj)+" StateMachine item"+(counter)+" =  "+obj);
//        System.out.println("StateMachine item"+(counter)+": item.hashCode()="+item.hashCode()+" obj.hashCode()="+obj.hashCode());
//        counter++;
//      }
//    } else {
//      System.out.println("StateMachine: _stateMap is null");
//    }
//
//    return _stateMap;
//  }

  private Map getGlobalStatesMap(IWApplicationContext iwac){
    String mapKey = "iw_global_state_map";
    Map stateMap = (Map)iwac.getApplicationAttribute(mapKey);
    if(stateMap == null){
      stateMap = new Hashtable();
      iwac.setApplicationAttribute(mapKey,stateMap);
    }
    return stateMap;
  }




  public IWPresentationState getStateFor(IWLocation location){
//    System.out.println("IWPresentationState getStateFor(IWLocation location)");
    Object idObj = location;
    IWPresentationState state = (IWPresentationState)this.getUserStatesMap().get(idObj);
//    System.out.println("IWPresentationState - STATE = "+state);
    if(state==null){
      IWPresentationState globalState = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(idObj);
      if(globalState==null){
        throw new RuntimeException("IWPresentationState class not initialized for this location: "+location);
      }
      try{
        state = (IWPresentationState)((IWPresentationState)globalState).getClass().newInstance();
      }
      catch(IllegalAccessException iae){
        throw new RuntimeException(iae.getMessage());
      }
      catch(InstantiationException ie){
        throw new RuntimeException(ie.getMessage());
      }

      this.getUserStatesMap().put(idObj,state);
    }
    return state;
  }
  
  /** Returns all existing change listeners */
  public Collection getAllChangeListeners() {
    Collection changeListeners = new ArrayList();
    Map map = this.getUserStatesMap();
    Collection coll = map.values();
    Iterator iterator = coll.iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (object instanceof ChangeListener)
        changeListeners.add(object);
    }
    return changeListeners;
  }

  /** Returns all existing controllers */  
  public Collection getAllControllers() {
    Collection controllers = new ArrayList();
    Map map = this.getUserStatesMap();
    Collection coll = map.values();
    Iterator iterator = coll.iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (object instanceof IWControlFramePresentationState)
        controllers.add(object);
    }
    return controllers;
  }


  
  public IWPresentationState getStateFor(String compoundId, Class stateClassType) {
    IWPresentationState state = (IWPresentationState)this.getUserStatesMap().get(compoundId);
    // is the following  part of code ever used?
    if (state == null)  {
       Map map = this.getUserStatesMap();
       Collection coll = map.values();
       Iterator iterator = coll.iterator();
       while (iterator.hasNext()) {
         Object object = iterator.next();
         if (stateClassType.isAssignableFrom(object.getClass()) &&
            ((IWPresentationState) object).getCompoundId().equals(compoundId))
          return (IWPresentationState) object;
       }
    }
    
    
    
//    System.out.println("IWPresentationState - STATE = "+state);
    if(state==null){
      IWPresentationState globalState = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(compoundId);
      if(globalState==null){
//        System.out.println("IWPresentationState location2: "+location);
        globalState = initializeState(stateClassType);
        if(globalState != null){
          globalState.setCompoundId(compoundId);
          IWUserContext iwuc = getUserContext();
          globalState.setUserContext(iwuc);
//          System.out.println("IWPresentationState location3: "+location);
          getGlobalStatesMap(getUserContext().getApplicationContext()).put(compoundId,globalState);
        } else {
          return null;
        }
      }

      try{
        state = (IWPresentationState)((IWPresentationState)globalState).getClass().newInstance();
        state.setCompoundId(compoundId);
        IWUserContext iwuc = getUserContext();
        state.setUserContext(iwuc);
      }
      catch(IllegalAccessException iae){
        throw new RuntimeException(iae.getMessage());
      }
      catch(InstantiationException ie){
        throw new RuntimeException(ie.getMessage());
      }
//      System.out.println("IWPresentationState location4: "+location);
      this.getUserStatesMap().put(compoundId,state);
    }
    return state;
  }
  
  
  

  public IWPresentationState getStateFor(IWLocation location, Class stateClassType){
//    System.out.println("IWPresentationState getStateFor(IWLocation location, Class stateClassType)");
//    System.out.println("IWPresentationState location: "+location);
    IWPresentationState state = (IWPresentationState)this.getUserStatesMap().get(location);
    //
    if (state == null)  {
       Map map = this.getUserStatesMap();
       Collection coll = map.values();
       Iterator iterator = coll.iterator();
       while (iterator.hasNext()) {
         Object object = iterator.next();
         if (stateClassType.isAssignableFrom(object.getClass()))
          return (IWPresentationState) object;
       }
    }
    
    
    
//    System.out.println("IWPresentationState - STATE = "+state);
    if(state==null){
      IWPresentationState globalState = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(location);
      if(globalState==null){
//        System.out.println("IWPresentationState location2: "+location);
        globalState = initializeState(stateClassType);
        if(globalState != null){
//          System.out.println("IWPresentationState location3: "+location);
          getGlobalStatesMap(getUserContext().getApplicationContext()).put(location,globalState);
        } else {
          return null;
        }
      }

      try{
        state = (IWPresentationState)((IWPresentationState)globalState).getClass().newInstance();
      }
      catch(IllegalAccessException iae){
        throw new RuntimeException(iae.getMessage());
      }
      catch(InstantiationException ie){
        throw new RuntimeException(ie.getMessage());
      }
//      System.out.println("IWPresentationState location4: "+location);
      this.getUserStatesMap().put(location,state);
    }
    return state;
  }

//  private IWPresentationState initializeState(Object obj)throws RuntimeException{
//    if(obj instanceof ICObject){
//      return initializeState((ICObject)obj);
//    } else if(obj instanceof IWLocation){
//      return initializeState((IWLocation)obj);
//    } else {
//      return null;
//    }
//  }




  public IWPresentationState getStateFor(ICObjectInstance idObj){
//    System.out.println("---------------getStateFor------------------");
//    System.out.println("StateMachineBean.getStateFor("+idObj+")");
    //try {
      IWPresentationState state = (IWPresentationState)this.getUserStatesMap().get(idObj.getPrimaryKey());
//      System.out.println("IWPresentationState - STATE = "+state);
      if(state==null){
//        System.out.println("StateMachineBean: state is null");
        ICObject icObject = idObj.getObject();
        IWPresentationState globalState = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(icObject.getPrimaryKey());
        if(globalState==null){
//          System.out.println("StateMachineBean: globalState is null");
          globalState = initializeState(icObject);
          if(globalState != null){
            getGlobalStatesMap(getUserContext().getApplicationContext()).put(icObject.getPrimaryKey(),globalState);
          } else {
            throw new RuntimeException("IWPresentationState class not initialized for this object: "+icObject);
          }
        }

        try{
          state = (IWPresentationState)((IWPresentationState)globalState).getClass().newInstance();
        }
        catch(IllegalAccessException iae){
          throw new RuntimeException(iae.getMessage());
        }
        catch(InstantiationException ie){
          throw new RuntimeException(ie.getMessage());
        }

        this.getUserStatesMap().put(idObj.getPrimaryKey(),state);
      }
      return state;
    //}
    //catch (RemoteException ex) {
    //  throw new RuntimeException(ex.getMessage());
    //}
  }





  private IWPresentationState initializeState(IWLocation location)throws RuntimeException{
    try{
      Class stateClass = lookupStateClassForLocation(location);
      if(stateClass != null){
        return (IWPresentationState)stateClass.newInstance();
      } else {
        System.err.println("IWStateMachine.initializeState(location): stateClass == null");
        return null;
      }
    }
    catch(IllegalAccessException iae){
      throw new RuntimeException(iae.getMessage());
    }
    catch(InstantiationException ie){
      throw new RuntimeException(ie.getMessage());
    }
    catch(RemoteException re){
      throw new RuntimeException(re.getMessage());
    }
  }

  private IWPresentationState initializeState(Class stateClassType)throws RuntimeException{
    try{
      Class stateClass = stateClassType;
      if(stateClass != null){
        return (IWPresentationState)stateClass.newInstance();
      } else {
        System.err.println("IWStateMachine.initializeState(location): stateClass == null");
        return null;
      }
    }
    catch(IllegalAccessException iae){
      throw new RuntimeException(iae.getMessage());
    }
    catch(InstantiationException ie){
      throw new RuntimeException(ie.getMessage());
    }
  }

  private Class lookupStateClassForLocation(IWLocation location) throws RemoteException {
    IWFrameBusiness fb = (IWFrameBusiness)IBOLookup.getSessionInstance(this.getUserContext(),IWFrameBusiness.class);

    Page page = fb.getFrame(location);
    if(page != null){
      if(page instanceof StatefullPresentation){
        return ((StatefullPresentation)page).getPresentationStateClass();
      } else {
        List l = page.getChildren();
        if(l != null){
          Iterator iter = l.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            if(item instanceof StatefullPresentation){
              return ((StatefullPresentation)item).getPresentationStateClass();
            }
          }
        }
      }
    }

    return null;
  }




}