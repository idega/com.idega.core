package com.idega.presentation;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import com.idega.business.IBOLookup;
import com.idega.event.IWPresentationState;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.repository.data.RefactorClassRegistry;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class StatefullPresentationImplHandler {

  private Class<? extends IWPresentationState> _class = null;

  IWPresentationState _presentationState = null;


  public StatefullPresentationImplHandler() {
  }

  public static IWPresentationState getPresentationState(String compoundId, IWUserContext iwuc) {
    IWPresentationState presentationState = null;
    try {
      IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
      StringTokenizer tokenizer = new StringTokenizer(compoundId, "/");
      String lastElement = "";
      while (tokenizer.hasMoreTokens()) {
        lastElement = tokenizer.nextToken();
      }
      int childDelimiterPosition = lastElement.lastIndexOf("_");
      String classCode = lastElement.substring(0, childDelimiterPosition);
      String className = IWMainApplication.decryptClassName(classCode);
      Class presentationClass = RefactorClassRegistry.forName(className);
      presentationState = stateMachine.getStateFor(compoundId, presentationClass);
    }
    catch (ClassNotFoundException ce)  {
      throw new RuntimeException(ce.getMessage());
    }
    catch (RemoteException re) {
      throw new RuntimeException(re.getMessage());
    }
    return presentationState;
  }


  public Class<? extends IWPresentationState> getPresentationStateClass(){
    return this._class;
  }

  public void setPresentationStateClass(Class<? extends IWPresentationState> stateClass){
    this._class = stateClass;
  }

  public IWPresentationState getPresentationState(PresentationObject obj, IWUserContext iwuc){
    if(this._presentationState == null){
      try {
        IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
          this._presentationState = stateMachine.getStateFor(obj.getCompoundId(), this._class);
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
    }
    return this._presentationState;
  }


}