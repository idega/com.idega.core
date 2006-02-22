package com.idega.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.StyledButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.GenericFormCollector;
import com.idega.util.datastructures.Collectable;

/**
 * Title:        UserModule
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class TabbedPropertyPanel extends Form implements ChangeListener, IWSubmitListener { 
    
    public static final String TAB_FORM_NAME = "tab_form";
    public static final String TAB_STORE_WINDOW = "tab_store_window";
    private boolean applyClicked = false;
    private String attributeString;
    private Table buttonTable;
    private StyledButton cancelButton;
    private SubmitButton cancel;
    private boolean cancelClicked = false;
    private GenericFormCollector collector;
    private boolean first = true;
    private Table frameTable;
    private boolean justConstructed = true;
    private StyledButton okButton;
    private SubmitButton ok;
    private boolean okClicked = false;
    private boolean stateChanged = false;
    private IWTabbedPane tpane;
    private boolean useApplyButton=true;
    private boolean useCancelButton=true;
    private boolean useCollector = false;
    private boolean useOkButton=true;
    
    public static TabbedPropertyPanel getInstanceFromSessionOrAddToSession(String key, IWContext iwc, TabbedPropertyPanel panel){
        boolean reload = false;
        if (iwc.getSessionAttribute(TAB_STORE_WINDOW) != null) {
            iwc.removeSessionAttribute(TAB_STORE_WINDOW);
            reload = new GenericFormCollector().storeAll(iwc);
        }
        
        Object  obj = iwc.getSessionAttribute(key);
        if (obj != null && obj instanceof TabbedPropertyPanel) {
            TabbedPropertyPanel TabPropPanelObj = (TabbedPropertyPanel)obj;
            TabPropPanelObj.justConstructed(false);
            if (reload) {
                TabPropPanelObj.okClicked = true;
            }
            return TabPropPanelObj;
        }
        else {
            TabbedPropertyPanel tempTab;
            if (panel!=null) {
                tempTab = panel;
            }
            else {
                tempTab = new TabbedPropertyPanel(key,iwc);
            }
            
            iwc.setSessionAttribute(key, tempTab);
            tempTab.setAttributeString(key);
            return tempTab;
        }
    }
    
    //added - birna
    public TabbedPropertyPanel(IWContext iwc) {
        this("undefined", iwc,true,true,true);
    }
    
    public TabbedPropertyPanel(IWContext iwc, boolean useOkButton,boolean useCancelButton,boolean useApplyButton) {
        this("undefined", iwc,useOkButton,useCancelButton,useApplyButton);
    }
    
    public TabbedPropertyPanel(String key, IWContext iwc, boolean useOkButton,boolean useCancelButton,boolean useApplyButton) {
        this(key,iwc);
        this.useOkButton = useOkButton;
        this.useCancelButton = useCancelButton;
        this.useApplyButton = useApplyButton;
        lineUpButtons();
    }
    
    private TabbedPropertyPanel(String key, IWContext iwc) {
        
        setName(TAB_FORM_NAME);
        frameTable = new Table();
        frameTable.setCellpadding(0);
        frameTable.setCellspacing(0);
        frameTable.setWidth(Table.HUNDRED_PERCENT);
        frameTable.setHeight(2, 5);

        tpane = IWTabbedPane.getInstance(key,iwc);
        tpane.addChangeListener(this);
        // add all change listeners
        Collection changeListeners;
        try {
            IWStateMachine stateMachine = (IWStateMachine) IBOLookup.getSessionInstance(iwc, IWStateMachine.class);
            changeListeners = stateMachine.getAllChangeListeners();
        }
        catch (RemoteException e) {
            changeListeners = new ArrayList();
        }
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext())  {
            tpane.addChangeListener((ChangeListener) iterator.next());
        }
        tpane.setTabsToFormSubmit(this);
        
        initializeLayout();
        initializeButtons(iwc);
        this.add(frameTable);
        
        ok.addIWSubmitListener(this, this,iwc);
        cancel.addIWSubmitListener(this, this,iwc);
    }
	
	public void setCollector(GenericFormCollector collector){
		this.collector = collector;
	}
	
	public GenericFormCollector getCollector(){
		if(collector==null){
			collector = new GenericFormCollector();
		}
		return collector;
	}
    
    public void actionPerformed(IWSubmitEvent e){
        if(e.getSource() == ok){
		
            boolean success = getCollector().storeAll(e.getIWContext());
            if(success){
                this.okClicked = true;
            }else{
                this.okClicked = false;
            }
            this.cancelClicked = false;
            this.applyClicked = false;
        }
        else if(e.getSource() == cancel){
            this.okClicked = false;
            this.cancelClicked = true;
            this.applyClicked = false;
        }
        else {
            boolean success = getCollector().storeAll(e.getIWContext());
            if(success){
                this.okClicked = true;
            }
            else{
                this.okClicked = false;
            }
            this.cancelClicked = false;
            this.applyClicked = false;
        }
    }
    
    public void addTab(PresentationObject collectable, int index, IWContext iwc){
    		collectable.setParent(this);
    	
        tpane.insertTab( collectable.getName(), collectable, index, iwc);
        if(collectable instanceof Collectable){
			getCollector().addCollectable((Collectable)collectable, index);
            useCollector = true;
        }
    }
    
    public boolean clickedApply(){
        return this.applyClicked;
    }
    
    public boolean clickedCancel(){
        return this.cancelClicked;
    }
    
    public boolean clickedOk(){
        return this.okClicked;
    }
    
    public boolean isApplyButtonDisabled(){
		return !useApplyButton;
    }
    public boolean isOkButtonDisabled(){
		return !useOkButton;
    }
    public boolean isCancelButtonDisabled(){
		return !useCancelButton;
    }

    public void disableApplyButton(boolean value){
        useApplyButton = !value;
    }
    
    public void disableCancelButton(boolean value){
        useCancelButton = !value;
    }
    
    public void disableOkButton(boolean value){
        useOkButton = !value;
    }
    
    public void dispose(IWContext iwc){
        //this method should also be called if someone presses the "x" close button on a window
        //todo implement by adding a frame around the panel and do a javascript onunload to call this method via an url
        iwc.getSession().removeAttribute(attributeString);
        tpane.dispose(iwc);
        ok.endEvent(iwc);
        cancel.endEvent(iwc);
    }
    
    public PresentationObject[] getAddedTabs(){
        return tpane.getAddedTabs();
    }
    
    public SubmitButton getCancelButton(){
        return cancel;
    }
    
    public IWTabbedPane getIWTabbedPane(){
        return tpane;
    }
    
    public SubmitButton getOkButton(){
        return ok;
    }
    
    public void initializeButtons(IWContext iwc){
        //changed for localized buttons - birna
    			IWResourceBundle iwrb = getResourceBundle(iwc);
        ok = new SubmitButton(iwrb.getLocalizedString("save", "Save"), iwrb.getLocalizedString("save", "Save"));
        okButton = new StyledButton(ok);
        //ok.setSubmitConfirm(iwrb.getLocalizedString("change.group.details?", "Do you want to save changes to group details?"));
        cancel = new SubmitButton(iwrb.getLocalizedString("close", "Close"), iwrb.getLocalizedString("close", "Close"));
        cancelButton = new StyledButton(cancel);
    }
    
    public void initializeLayout(){
        frameTable.resize(1,3);
        frameTable.add(tpane,1,1);
        frameTable.setCellpadding(0);
        frameTable.setCellspacing(0);
        frameTable.setBorder(0);
        frameTable.setWidth(Table.HUNDRED_PERCENT);
    }
    
    public boolean justConstructed(){
        return justConstructed;
    }
    
    public void justConstructed(boolean justConstructed){
        this.justConstructed = justConstructed;
        this.tpane.justConstructed(justConstructed);
    }
    
    public void lineUpButtons(){
        buttonTable = new Table(2, 1);
        
        buttonTable.setCellpadding(5);
        buttonTable.setCellspacing(0);
        buttonTable.setStyleClass("main");
        buttonTable.setWidth(Table.HUNDRED_PERCENT);
        buttonTable.setAlignment(2, 1, Table.HORIZONTAL_ALIGN_RIGHT);
        
        Table buttons = new Table();
        buttons.setCellpadding(0);
        buttons.setCellspacing(0);

				if (useOkButton) {
          buttons.add(okButton, 1, 1);
        }
        
        if(useCancelButton) {
          buttons.setWidth(2, 5);
          buttons.add(cancelButton, 3, 1);
        }
        buttonTable.add(buttons, 2, 1);
        
        frameTable.add(buttonTable, 1, 3);
    }
    
    public void main(IWContext iwc) throws Exception{
        if(stateChanged){
            boolean success = getCollector().setSelectedIndex(tpane.getSelectedIndex(),iwc);
            if(!success){
                this.getIWTabbedPane().setSelectedIndex(getCollector().getSelectedIndex());
            }
            stateChanged = false;
        }
        super.main(iwc);
    }
    
    public void setAttributeString(String attributeString){
        this.attributeString = attributeString;
    }
    
    public void stateChanged(ChangeEvent e){
        if(useCollector && !first){
            stateChanged = true;
        }
        first = false;
    }
    
    public void addHelpButton(PresentationObject obj) {
    		buttonTable.emptyCell(1, 1);
    		buttonTable.add(obj, 1, 1);
    }   
}