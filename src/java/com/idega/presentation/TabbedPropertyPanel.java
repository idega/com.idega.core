package com.idega.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.idega.block.help.presentation.Help;
import com.idega.business.IBOLookup;
import com.idega.event.IWStateMachine;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.event.CreateGroupEvent;
import com.idega.util.GenericFormCollector;
import com.idega.util.datastructures.Collectable;

/**
 * Title:        UserModule
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class TabbedPropertyPanel extends Form implements ChangeListener, IWSubmitListener { 

  private Table frameTable;
  private Table buttonTable;
  private IWTabbedPane tpane;
  private static String TabbedPropertyPanelAttributeString = "-TabbedPropertyPanel";
  private GenericFormCollector collector;
  private boolean useCollector = false;
  private String attributeString;
  private boolean justConstructed = true;
  private boolean first = true;
  private boolean stateChanged = false;
  private boolean okClicked = false;
  private boolean cancelClicked = false;
  private boolean applyClicked = false;

  private SubmitButton ok;
  private SubmitButton cancel;
	private Help help;
//  private SubmitButton apply;

	private final static String MEMBER_HELP_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";
	private final static String HELP_TEXT_KEY = "tabbed_property_panel";
  
  
	private CreateGroupEvent _createEvent;

  private boolean useOkButton=true;
  private boolean useCancelButton=true;
  private boolean useApplyButton=true;
  
  //added - birna
  public TabbedPropertyPanel(IWContext iwc) {
  	this("undefined", iwc);
  }

  private TabbedPropertyPanel(String key, IWContext iwc) {
    frameTable = new Table();
//    this.setMethod("get");
//    frameTable.setBorder(1);  // temp
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
    collector = new GenericFormCollector();
    initializeButtons(iwc);
    lineUpButtons();
    ok.addIWSubmitListener(this, this,iwc);
 //   apply.addIWSubmitListener(this, this,iwc);
    cancel.addIWSubmitListener(this, this,iwc);
  }
  public Help getHelpButton(IWContext iwc) {
//	added for help-button
			IWBundle iwb = getBundle(iwc);
			help = new Help();
			Image helpImage = iwb.getImage("help.gif");
			help.setHelpTextBundle( MEMBER_HELP_BUNDLE_IDENTIFIER);
			help.setHelpTextKey(HELP_TEXT_KEY);
			help.setImage(helpImage);
			return help;	
  }
  
  public void initializeButtons(IWContext iwc){
  	
  	//changed for localized buttons - birna
  	IWResourceBundle iwrb = getResourceBundle(iwc);
		ok = new SubmitButton(iwrb.getLocalizedImageButton("save", "Save"), iwrb.getLocalizedString("save", "Save"));
		cancel = new SubmitButton(iwrb.getLocalizedImageButton("close", "Close"), iwrb.getLocalizedString("close", "Close"));
//		apply = new SubmitButton(iwrb.getLocalizedImageButton("apply", "Apply"),iwrb.getLocalizedString("commit", "Commit"));

//    ok = new SubmitButton("     OK     ");
//    cancel = new SubmitButton(" Cancel ");
//    apply = new SubmitButton("  Apply  ");
  }


  public static TabbedPropertyPanel getInstance(String key, IWContext iwc){
    Object  obj = iwc.getSessionAttribute(key+TabbedPropertyPanelAttributeString);
    if(obj != null && obj instanceof TabbedPropertyPanel){
      TabbedPropertyPanel TabPropPanelObj = (TabbedPropertyPanel)obj;
      TabPropPanelObj.justConstructed(false);
      return TabPropPanelObj;
    }else{
      TabbedPropertyPanel tempTab = new TabbedPropertyPanel(key,iwc);
      iwc.setSessionAttribute(key+TabbedPropertyPanelAttributeString, tempTab);
      tempTab.setAttributeString(key+TabbedPropertyPanelAttributeString);
      return tempTab;
    }
  }


  public boolean justConstructed(){
    return justConstructed;
  }

  public void justConstructed(boolean justConstructed){
    this.justConstructed = justConstructed;
    this.tpane.justConstructed(justConstructed);
  }

  public void setAttributeString(String attributeString){
    this.attributeString = attributeString;
  }

  public void dispose(IWContext iwc){
  	//this method should also be called if someone presses the "x" close button on a window
  	//todo implement by adding a frame around the panel and do a javascript onunload to call this method via an url
    iwc.getSession().removeAttribute(attributeString);
    tpane.dispose(iwc);
    ok.endEvent(iwc);
    cancel.endEvent(iwc);
//    apply.endEvent(iwc);
  }



  public IWTabbedPane getIWTabbedPane(){
    return tpane;
  }

  public void initializeLayout(){
    frameTable.resize(1,2);
    frameTable.add(tpane,1,1);
    frameTable.setAlignment(1,1,"center");
  }

  public void setAlignment(String align){
    frameTable.setAlignment(align);
  }

  public void setVerticalAlignment(String vAlign){
    frameTable.setVerticalAlignment(vAlign);
  }

  public void stateChanged(ChangeEvent e){
    if(useCollector && !first){
      stateChanged = true;
    }
    first = false;
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
   
//    }else if(e.getSource() == apply){
//      boolean success = collector.storeAll(e.getIWContext());
//      this.okClicked = false;
//      this.cancelClicked = false;
//      if(success){
//        this.applyClicked = true;
//      }else{
//        this.applyClicked = false;
//      }
    }
    else if(e.getSource() == cancel){
      this.okClicked = false;
      this.cancelClicked = true;
      this.applyClicked = false;
    } else {
      this.okClicked = false;
      this.cancelClicked = false;
      this.applyClicked = false;
    }

  }

  public boolean clickedOk(){
    return this.okClicked;
  }

  public boolean clickedCancel(){
    return this.cancelClicked;
  }

  public boolean clickedApply(){
    return this.applyClicked;
  }



  public void addTab(PresentationObject collectable, int index, IWContext iwc){
    tpane.insertTab( collectable.getName(), collectable, index, iwc);
    if(collectable instanceof Collectable){
      collector.addCollectable((Collectable)collectable, index);
      useCollector = true;
    }
  }

  public PresentationObject[] getAddedTabs(){
    return tpane.getAddedTabs();
  }

  public void disableOkButton(boolean value){
    useOkButton = !value;
  }

  public void disableCancelButton(boolean value){
    useCancelButton = !value;
  }

  public void disableApplyButton(boolean value){
    useApplyButton = !value;
  }


  public SubmitButton getOkButton(){
    return ok;
  }

  public SubmitButton getCancelButton(){
    return cancel;
  }

//  public SubmitButton getApplyButton(){
//    return apply;
//  }


  public void lineUpButtons(){
    // assuming all buttons are enabled

    buttonTable = new Table(5,1);

    buttonTable.setCellpadding(0);
    buttonTable.setCellspacing(0);
    buttonTable.setHeight(27);

    buttonTable.setVerticalAlignment(1,1,"bottom");
    buttonTable.setVerticalAlignment(3,1,"bottom");
    buttonTable.setVerticalAlignment(5,1,"bottom");

    buttonTable.setWidth(2,1,"7");
    buttonTable.setWidth(4,1,"7");
    
    IWContext iwc = IWContext.getInstance();

//    buttonTable.add(getHelpButton(iwc),1,1);
    buttonTable.add(ok,3,1);
    buttonTable.add(cancel,5,1);
//    buttonTable.add(apply,5,1);

    frameTable.add(buttonTable,1,2);
    frameTable.setAlignment(1,2,"right");

  }

  public void main(IWContext iwc) {

    if(stateChanged){

      boolean success = collector.setSelectedIndex(tpane.getSelectedIndex(),iwc);
      if(!success){
        this.getIWTabbedPane().setSelectedIndex(collector.getSelectedIndex());
      }
      stateChanged = false;
    }

/*    if(this.justConstructed()){
      lineUpButtons();
      ok.addIWSubmitListener(this, this,iwc);
      apply.addIWSubmitListener(this, this,iwc);
    }
*/
  }


}
