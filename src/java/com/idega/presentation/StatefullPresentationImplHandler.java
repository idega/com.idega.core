package com.idega.presentation;
import java.sql.SQLException;
import java.rmi.RemoteException;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWUserContext;
import com.idega.event.IWStateMachine;
import com.idega.event.IWPresentationState;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class StatefullPresentationImplHandler {

  private Class _class = null;

  IWPresentationState _presentationState = null;


  public StatefullPresentationImplHandler() {
  }


  public Class getPresentationStateClass(){
    return _class;
  }

  public void setPresentationStateClass(Class stateClass){
    _class = stateClass;
  }

  public IWPresentationState getPresentationState(PresentationObject obj, IWUserContext iwuc){
    if(_presentationState == null){
      try {
        IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
        if(obj.getICObjectInstanceID() == 0){
          _presentationState = stateMachine.getStateFor(obj.getLocation(),_class);
        } else {
          _presentationState = stateMachine.getStateFor(obj.getICObjectInstance());
        }
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
      catch (SQLException sql) {
        throw new RuntimeException(sql.getMessage());
      }
    }
    return _presentationState;
  }



}