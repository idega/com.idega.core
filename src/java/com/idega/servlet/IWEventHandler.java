package com.idega.servlet;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeListener;

import com.idega.business.IBOLookup;
import com.idega.event.IWPresentationEvent;
import com.idega.event.IWPresentationState;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.browser.presentation.IWBrowseControl;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.StatefullPresentation;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 */
public class IWEventHandler extends IWPresentationServlet {
  
  
  
  public void initializePage(){
    IWContext iwc = getIWContext();
    
    Page page = new EventViewer();
    //page.add("EventHandler");
    setPage(page);
    
    
  }
  


  public class EventViewer extends Page implements IWBrowseControl, StatefullPresentation {
     private boolean initialized = false;

     private IWControlFramePresentationState _presentationState = null;

     public EventViewer(){
       if(this.isChildOfOtherPage()){
         Page parent = this.getParentPage();
         parent.setAllMargins(0);
         parent.setBackgroundColor("#386CB7");
       } else {
         setAllMargins(0);
         setBackgroundColor("#386CB7");
       }
     }

     public String getBundleIdentifier(){
       return "com.idega.user";
     }

     public ChangeListener getChangeControler(){
       return (ChangeListener)this.getPresentationState(this.getIWUserContext());
     }

     public IWPresentationState getPresentationState(IWUserContext iwuc){
       if(_presentationState == null){
         try {
           IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
           // construct compoundId for the control frame
           String id = getIWContext().getParameter(IWPresentationEvent.EVENT_CONTROLLER);
           if (id != null)
            _presentationState = (IWControlFramePresentationState)stateMachine.getStateFor(id,this.getPresentationStateClass());
         }
         catch (RemoteException re) {
           throw new RuntimeException(re.getMessage());
         }
       }
       return _presentationState;
     }

     public Class getPresentationStateClass(){
       return IWControlFramePresentationState.class;
     }



     public void main(IWContext iwc) throws Exception{

       IWControlFramePresentationState state = (IWControlFramePresentationState)this.getPresentationState(iwc);
       if(state != null){
         Set onLoadSet = state.getOnLoadSet();
         Iterator iter = onLoadSet.iterator();
         while (iter.hasNext()) {
           Object item = iter.next();
           this.setOnLoad((String)item);
         }
         state.clearOnLoad();
       }



   }

  }
  
}
