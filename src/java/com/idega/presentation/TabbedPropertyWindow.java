package com.idega.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;
import com.idega.presentation.ui.Window;
import com.idega.presentation.text.Text;


/**
 * Title:        IW
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class TabbedPropertyWindow extends Window {

  protected TabbedPropertyPanel panel = null;

  public TabbedPropertyWindow(){
    this(410,512);
  }

  public TabbedPropertyWindow(int width, int height){
    super(width,height);
    super.setScrollbar(false);
    super.setAllMargins(0);
    super.setTopMargin(3);
  }

  public void _main(IWContext iwc) throws Exception {
    this.empty();
		if(disposeOfPanel(iwc)){
			//temp solution
			panel = TabbedPropertyPanel.getInstance(getSessionAddressString(), iwc );
			panel.dispose(iwc);
		}
		
		panel = TabbedPropertyPanel.getInstance(getSessionAddressString(), iwc );
    
    if(panel.justConstructed()){
      panel.setAlignment("center");
      panel.setVerticalAlignment("middle");
      initializePanel(iwc, panel);
    }

    if(panel.clickedCancel() || panel.clickedOk()){
      if(panel.clickedOk() || panel.clickedApply()){
        iwc.getApplicationContext().removeApplicationAttribute("domain_group_tree");
        iwc.getApplicationContext().removeApplicationAttribute("group_tree");    
        setOnLoad(iwc);
        //this.setParentToReload();
      }
      else {
        // cancel was clicked
        clearOnLoad(iwc);
      }
      this.close();
      panel.dispose(iwc);
    }else{
      this.add(panel);
    }
    super._main(iwc);

  }

  public PresentationObject[] getAddedTabs(){
    if(panel != null){
      return panel.getAddedTabs();
    } else {
      throw new RuntimeException("TabbedPropertyPanel not set. TabbedPropertyPanel is set in main(IWContext iwc)");
    }
  }



  public abstract String getSessionAddressString();

  public abstract void initializePanel( IWContext iwc, TabbedPropertyPanel panel);
  
  public boolean disposeOfPanel(IWContext iwc){
  	return false;
  }

  private void setOnLoad(IWContext iwc)  {
		Iterator iterator = getControllerIterator(iwc);
    while (iterator.hasNext())  {
      IWControlFramePresentationState state = (IWControlFramePresentationState) iterator.next();
      Set onLoadSet = state.getOnLoadSet();
      Iterator iter = onLoadSet.iterator();
      while (iter.hasNext()) {
        // this is a pop up window therefore add window.opener as prefix
        StringBuffer buffer = new StringBuffer("window.opener.");
        buffer.append((String) iter.next());
        this.setOnLoad(buffer.toString());
      }
      state.clearOnLoad();
    }
  }

	private Iterator getControllerIterator(IWContext iwc) {
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
		return iterator;
	}

  private void clearOnLoad(IWContext iwc)  {
    Iterator iterator = getControllerIterator(iwc);
    while (iterator.hasNext())  {
      IWControlFramePresentationState state = (IWControlFramePresentationState) iterator.next();
      state.clearOnLoad();
    }
  }

}
