/*
 * $Id: EventViewerPage.java,v 1.1 2004/11/21 16:56:07 tryggvil Exp $
 * Created on 21.11.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;


public class EventViewerPage extends Page {
     private boolean initialized = false;

     private IWControlFramePresentationState _presentationState = null;

     public EventViewerPage(){
       if(this.isChildOfOtherPage()){
         Page parent = this.getParentPage();
         parent.setAllMargins(0);
         //parent.setBackgroundColor("#386CB7");
       } else {
         setAllMargins(0);
         //setBackgroundColor("#386CB7");
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