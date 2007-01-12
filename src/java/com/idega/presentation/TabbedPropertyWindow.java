package com.idega.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.idegaweb.browser.presentation.IWControlFramePresentationState;
import com.idega.idegaweb.presentation.StyledIWAdminWindow;

/**
 * Title:        IW
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public abstract class TabbedPropertyWindow extends StyledIWAdminWindow {
    
    protected TabbedPropertyPanel panel = null;
    
    public TabbedPropertyWindow(){
        this(410,512);
    }
    
    public TabbedPropertyWindow(int width, int height){
        super(width,height);
        super.setScrollbar(false);
        super.setAllMargins(0);
        super.setTopMargin(0);//changed from 3
    }
    
    public void _main(IWContext iwc) throws Exception {
        
        this.empty();
       
        if(disposeOfPanel(iwc)){
            //temp solution
            this.panel = TabbedPropertyPanel.getInstanceFromSessionOrAddToSession(getSessionAddressString(), iwc, null );
            this.panel.dispose(iwc);  
            this.panel = null;
        }
        
        //the getPanelInstance is needed because we need to override the method from this classes extenders and not the TabbedProprtyPanel classes
        this.panel = TabbedPropertyPanel.getInstanceFromSessionOrAddToSession(getSessionAddressString(), iwc , getPanelInstance(iwc));
        
        // do not close the window if the cancel button was pressed
        if (this.panel.clickedCancel()) {
            this.panel.dispose(iwc);
            close();
        }
        else {
            this.add(this.panel,iwc);
        }
        //WE MUST HAVE ADDED THE PANEL FIRST, OTHERWISE THE PARENT PAGE WILL BE NULL!
        if(this.panel.justConstructed()){
            initializePanel(iwc, this.panel);
        }else{
        		//we must set the page parents to this window so getParentPage() works
        		//their parent is lost because it was added to the session and then the window was destroyed
        		resetParentOfTabs(this.panel);
        }
        
        if(this.panel.clickedCancel() || this.panel.clickedOk() || this.panel.clickedApply()){
            if(this.panel.clickedOk() || this.panel.clickedApply()){
                iwc.getApplicationContext().removeApplicationAttribute("domain_group_tree");
                iwc.getApplicationContext().removeApplicationAttribute("group_tree");
                // clear on load only if okay was clicked    
                setOnLoad(iwc, this.panel.clickedOk());
            }
            else {
                // cancel was clicked
                clearOnLoad(iwc);
            }
        }

        super._main(iwc);
        
    }
    
    /**
	 * 
	 */
	private void resetParentOfTabs(TabbedPropertyPanel panel) {
		PresentationObject[] obj = panel.getAddedTabs();
		
		for (int i = 0; i < obj.length; i++) {
			PresentationObject tab = obj[i];
			tab.setParentObject(panel);
		}
		
		
	}

	/**
     * This is overridden else where to add permission checks
     * @param iwc
     * @return
     */
    protected TabbedPropertyPanel getPanelInstance(IWContext iwc) {
        //added -birna
        return new TabbedPropertyPanel(iwc);
    }
    
    public PresentationObject[] getAddedTabs(){
        if(this.panel != null){
            return this.panel.getAddedTabs();
        } else {
            throw new RuntimeException("TabbedPropertyPanel not set. TabbedPropertyPanel is set in main(IWContext iwc)");
        }
    }
    
    
    /**
     * This method must be overridden, for example the user properties window could just return something like "UserPropertiesTabbedWindow"
     * @return
     */
    public abstract String getSessionAddressString();
    
    public abstract void initializePanel( IWContext iwc, TabbedPropertyPanel panel);
    
    public boolean disposeOfPanel(IWContext iwc){
        return false;
    }
    
    private void setOnLoad(IWContext iwc, boolean clearOnLoad)  {
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
            if (clearOnLoad)  {
                state.clearOnLoad();
            }
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
