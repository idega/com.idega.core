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
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
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
    
//  private SubmitButton apply;
    
//  private final static String HELP_TEXT_KEY = "tabbed_property_panel";
//  
    
    public static final String TAB_FORM_NAME = "tab_form";
    public static final String TAB_STORE_WINDOW = "tab_store_window";
    private static String TabbedPropertyPanelAttributeString = "-TabbedPropertyPanel";
    private boolean applyClicked = false;
    private String attributeString;
    private Table buttonTable;
    private SubmitButton cancel;
    private boolean cancelClicked = false;
    private GenericFormCollector collector;
    private boolean first = true;
    private Table frameTable;
    private boolean justConstructed = true;
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
        
        Object  obj = iwc.getSessionAttribute(key+TabbedPropertyPanelAttributeString);
        if(obj != null && obj instanceof TabbedPropertyPanel){
            TabbedPropertyPanel TabPropPanelObj = (TabbedPropertyPanel)obj;
            TabPropPanelObj.justConstructed(false);
            if (reload) {
                TabPropPanelObj.okClicked = true;
            }
            return TabPropPanelObj;
        }else{
            TabbedPropertyPanel tempTab;
            if(panel!=null) {
                tempTab = panel;
            }
            else {
                tempTab = new TabbedPropertyPanel(key,iwc);
            }
            
            
            
            iwc.setSessionAttribute(key+TabbedPropertyPanelAttributeString, tempTab);
            tempTab.setAttributeString(key+TabbedPropertyPanelAttributeString);
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
        collector = new GenericFormCollector();
        
        setName(TAB_FORM_NAME);
        frameTable = new Table();
//      frameTable.setStyleClass("main");
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
        this.add(frameTable);
        initializeLayout();
        initializeButtons(iwc);
        
        
        ok.addIWSubmitListener(this, this,iwc);
        //   apply.addIWSubmitListener(this, this,iwc);
        cancel.addIWSubmitListener(this, this,iwc);
    }
    
    public void actionPerformed(IWSubmitEvent e){
        if(e.getSource() == ok){
            boolean success = collector.storeAll(e.getIWContext());
            if(success){
                this.okClicked = true;
            }else{
                this.okClicked = false;
            }
            this.cancelClicked = false;
            this.applyClicked = false;
            
//          }else if(e.getSource() == apply){
//          boolean success = collector.storeAll(e.getIWContext());
//          this.okClicked = false;
//          this.cancelClicked = false;
//          if(success){
//          this.applyClicked = true;
//          }else{
//          this.applyClicked = false;
//          }
        }
        else if(e.getSource() == cancel){
            this.okClicked = false;
            this.cancelClicked = true;
            this.applyClicked = false;
        } else {
            boolean success = collector.storeAll(e.getIWContext());
            if(success){
                this.okClicked = true;
            }else{
                this.okClicked = false;
            }
            this.cancelClicked = false;
            this.applyClicked = false;
        }
        
    }
    
    public void addTab(PresentationObject collectable, int index, IWContext iwc){
        tpane.insertTab( collectable.getName(), collectable, index, iwc);
        if(collectable instanceof Collectable){
            collector.addCollectable((Collectable)collectable, index);
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
//      apply.endEvent(iwc);
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
        ok = new SubmitButton(iwrb.getLocalizedImageButton("save", "Save"), iwrb.getLocalizedString("save", "Save"));
        //ok.setSubmitConfirm(iwrb.getLocalizedString("change.group.details?", "Do you want to save changes to group details?"));
        cancel = new SubmitButton(iwrb.getLocalizedImageButton("close", "Close"), iwrb.getLocalizedString("close", "Close"));
//      apply = new SubmitButton(iwrb.getLocalizedImageButton("apply", "Apply"),iwrb.getLocalizedString("commit", "Commit"));
        
        
    }
    
    public void initializeLayout(){
        frameTable.resize(1,2);
        frameTable.add(tpane,1,1);
        frameTable.setAlignment(1,1,"center");
        frameTable.setWidth(470);
//      frameTable.setHeight(500);
    }
    
    public boolean justConstructed(){
        return justConstructed;
    }
    
    public void justConstructed(boolean justConstructed){
        this.justConstructed = justConstructed;
        this.tpane.justConstructed(justConstructed);
    }
    
//  public SubmitButton getApplyButton(){
//  return apply;
//  }
    
    
    public void lineUpButtons(){
        // assuming all buttons are enabled
        
        buttonTable = new Table(5,1);
        
        buttonTable.setCellpadding(0);
        buttonTable.setCellspacing(0);
        buttonTable.setHeight(37);
        buttonTable.setStyleClass("main");
        buttonTable.setWidth("400");
        
        buttonTable.setVerticalAlignment(1,1,"middle");
        buttonTable.setVerticalAlignment(2,1,"middle");
        buttonTable.setVerticalAlignment(4,1,"middle");
        buttonTable.setAlignment(2,1,"right");
        buttonTable.setAlignment(4,1,"right");
        
        buttonTable.setWidth(3,1,"7");
        buttonTable.setWidth(5,1,"7");
        
//      buttonTable.add(getHelpButton(iwc),1,1);
        
        if(useOkButton) {
            buttonTable.add(ok,4,1);
            buttonTable.add(Text.NON_BREAKING_SPACE,4,1);
            
        }
        
        if(useCancelButton) {
            buttonTable.add(cancel,4,1);
        }
//      buttonTable.add(apply,5,1);
        
        frameTable.add(buttonTable,1,2);
        frameTable.setAlignment(1,2,"center");
        
    }
    
    public void main(IWContext iwc) throws Exception{
        
        if(stateChanged){
            
            boolean success = collector.setSelectedIndex(tpane.getSelectedIndex(),iwc);
            if(!success){
                this.getIWTabbedPane().setSelectedIndex(collector.getSelectedIndex());
            }
            stateChanged = false;
        }
        super.main(iwc);
        /*    if(this.justConstructed()){
         lineUpButtons();
         ok.addIWSubmitListener(this, this,iwc);
         apply.addIWSubmitListener(this, this,iwc);
         }
         */
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
    
    
    
    
}
