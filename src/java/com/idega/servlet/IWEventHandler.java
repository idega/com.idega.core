package com.idega.servlet;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 */
public class IWEventHandler extends IWPresentationServlet {
  
  
  
  public void initializePage(){
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

     public void main(IWContext iwc) {
      IWStateMachine stateMachine;  
      Collection controllers;   
      try {
        stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwc,IWStateMachine.class);
        controllers = stateMachine.getAllControllers();
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
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