package com.idega.event;

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

public class IWStateMachine {

  private static IWStateMachine instance;

  public IWStateMachine() {
  }

  public static IWStateMachine getInstance(IWMainApplication iwma){
    if(instance==null){
      instance = new IWStateMachine();
    }
    return instance;
  }

  public IWPresentationState getStateFor(ICObjectInstance instance,IWUserContext iwuc){
    IWPresentationState state = (IWPresentationState)this.getGlobalStatesMap(iwuc.getApplicationContext()).get(instance);
    if(state==null){
      state = state = (IWPresentationState)this.getGlobalStatesMap(iwuc.getApplicationContext()).get(instance);
      if(state==null){
        state = initializeState(instance);
        getGlobalStatesMap(iwuc.getApplicationContext()).put(instance,state);
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

  private Map getUserStatesMap(IWUserContext iwuc){
    /**
     * @todo Implement
     */
    return null;
  }

  private Map getGlobalStatesMap(IWApplicationContext iwac){
    /**
     * @todo Implement
     */
    return null;
  }
}