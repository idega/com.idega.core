package com.idega.event;

import com.idega.business.IBOSessionBean;
import com.idega.business.IBOServiceBean;
import java.util.*;

import com.idega.idegaweb.*;
import com.idega.core.data.ICObjectInstance;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IWStateMachineBean extends IBOSessionBean {

  private int _historyID = 0;

  public void setHistoryID(int historyId ){
    _historyID = historyId;
  }

  public void newState(){
    increaseHistoryID();
  }

  public void increaseHistoryID(){
    _historyID++;
  }

  public IWPresentationState getStateFor(ICObjectInstance instance){
    IWPresentationState state = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(instance);
    if(state==null){
      state = state = (IWPresentationState)this.getGlobalStatesMap(getUserContext().getApplicationContext()).get(instance);
      if(state==null){
        state = initializeState(instance);
        getGlobalStatesMap(getUserContext().getApplicationContext()).put(instance,state);
      }
    }
    return state;
  }





  private IWPresentationState initializeState(ICObjectInstance instance)throws RuntimeException{
    Class stateClass = lookupStateClassForInstance(instance);
    try{
      return (IWPresentationState)stateClass.newInstance();
    }
    catch(IllegalAccessException iae){
      throw new RuntimeException(iae.getMessage());
    }
    catch(InstantiationException ie){
      throw new RuntimeException(ie.getMessage());
    }
  }

  private Class lookupStateClassForInstance(ICObjectInstance instance){
    /**
     * @todo Implement
     */
    return null;
  }

  private Map getUserStatesMap(){
    String mapKey = "iw_user_state_map";
    Map stateMap = (Map)getUserContext().getSessionAttribute(mapKey);
    if(stateMap == null){
      stateMap = new Hashtable();
      getUserContext().setSessionAttribute(mapKey,stateMap);
    }
    return stateMap;
  }

  private Map getGlobalStatesMap(IWApplicationContext iwac){
    String mapKey = "iw_global_state_map";
    Map stateMap = (Map)iwac.getApplicationAttribute(mapKey);
    if(stateMap == null){
      stateMap = new Hashtable();
      iwac.setApplicationAttribute(mapKey,stateMap);
    }
    return stateMap;
  }


//  public PresentationObject[] getIWPOListeners(IWContext iwc){
//    String prm = iwc.getParameter(this.IB_OBJECT_INSTANCE_COORDINATE);
//    String[] coordinates = null;
//    if(prm != null){
//      StringTokenizer tokens = new StringTokenizer(prm,",");
//      coordinates = new String[tokens.countTokens()];
//      int index = 0;
//      while (tokens.hasMoreTokens()) {
//	coordinates[index++] = tokens.nextToken();
//      }
//    }
//
//    if(coordinates != null && coordinates.length > 0){
//      List l = new Vector();
//      for (int i = 0; i < coordinates.length; i++) {
//	String crdnts = coordinates[i];
//	int index = crdnts.indexOf('_');
//	String page = crdnts.substring(0,index);
//	String inst = crdnts.substring(index+1,crdnts.length());
//	if(!"".equals(page) && !"".equals(inst)){
//	  //Page parentPage = BuilderLogic.getInstance().getIBXMLPage(page).getPopulatedPage();
//	  //PresentationObject obj = parentPage.getContainedICObjectInstance(Integer.parseInt(inst));
//	  PresentationObject obj = getPopulatedObjectInstance(inst,iwc);
//	  if(obj != null && !obj.equals(PresentationObject.NULL_CLONE_OBJECT)){
//	    l.add(obj);
//	  }
//	}
//      }
//      PresentationObject[] toReturn = (PresentationObject[])l.toArray(new PresentationObject[0]);
//
//      if(toReturn.length > 0){
//	/*
//	System.err.println("BuilderLogic Listeners");
//	for (int i = 0; i < toReturn.length; i++) {
//	  System.err.println(" - "+toReturn[i].getParentPageID()+"_"+toReturn[i].getICObjectInstanceID());
//	}*/
//
//	return toReturn;
//      }else{
//	//System.err.println("BuilderLogic Listeners are null");
//	return null;
//      }
//    } else{
//      //System.err.println("BuilderLogic Listeners are null");
//      return null;
//    }
//  }



}