package com.idega.servlet;

import java.rmi.RemoteException;
import java.util.Collection;
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
  


  public class EventViewer extends Page {
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

     public void main(IWContext iwc) throws Exception{
      IWStateMachine stateMachine;     
      try {
        stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwc,IWStateMachine.class);
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
      Collection controllers = stateMachine.getAllControllers();
      Iterator iterator = controllers.iterator();
      while (iterator.hasNext())  {
        IWControlFramePresentationState state = (IWControlFramePresentationState) iterator.next();
        Set onLoadSet = state.getOnLoadSet();
        Iterator iter = onLoadSet.iterator();
        while (iter.hasNext()) {
          String item = (String) iter.next();
          this.setOnLoad(item);
        }
        state.clearOnLoad();
      }
    }
  }
}