package com.idega.presentation.plaf;

import com.idega.presentation.Table;
import java.util.Vector;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.EventListener;
import javax.swing.SingleSelectionModel;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.IWColor;
import com.idega.presentation.ui.Form;

/**
 * Title:        idegaWeb project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author
 * @version 1.0
 */

public abstract class GenericTabbedPaneUI implements IWTabbedPaneUI {


  private TabPresentation tab;
  private TabPagePresentation tabpage;
  private IWColor MainColor;


  public GenericTabbedPaneUI(){
    setMainColor(new IWColor(212,208,200));
//    initFrame();
    initTab();
    initTabPage();
  }


//  public abstract void initFrame();
  public abstract void initTab();
  public abstract void initTabPage();

//  public void setFrame(TabbedPaneFrame frame){
//    this.frame = frame;
//  }

  public void setTab(TabPresentation tab){
    this.tab = tab;
  }

  public void setTabPage(TabPagePresentation page){
    this.tabpage = page;
  }


//  public TabbedPaneFrame getFrame(){
//    if (frame == null)
//      initFrame();
//    return frame;
//  }

  public TabPresentation getTabPresentation(){
    if (tab == null)
      initTab();
    return tab;
  }

  public TabPagePresentation getTabPagePresentation(){
    if (tabpage == null)
      initTabPage();
    return tabpage;
  }

  public void setMainColor(IWColor color){
    this.MainColor = color;
  }

  public IWColor getMainColor(){
    return MainColor;
  }







  public abstract class GenericTabPresentation extends Table implements TabPresentation {

    private IWColor tabPageColor;
    protected Vector tabs;
    protected ChangeEvent changeEvent = null;
    protected EventListenerList listenerList = new EventListenerList();
    protected Form linkForm = null;

    private int index = -1;

    public GenericTabPresentation(){
      super();
      tabs = new Vector();
    }


    public void setForm(Form form){
      linkForm = form;
    }

    public Form getForm(){
      return linkForm;
    }


    public void add(PresentationObject obj, int index){
      this.tabs.insertElementAt(obj, index);
    }

    public void empty(int index){
      this.tabs.remove(index);
    }

    public abstract Link getTabLink(PresentationObject obj);

//  public void empty(PresentationObject obj){}

    public abstract void setWidth(String width);
    public abstract void SetHeight(String height);

    public Vector getAddedTabs(){
      return tabs;
    }

    public void setAddedTabs(Vector tabs){
      tabs = tabs;
    }

    public IWColor getColor(){
      return this.tabPageColor;
    }

    public void setColor(IWColor color){
//      super.setColor(color);
      this.tabPageColor = color;
    }


    // SingleSelectionModel methods

    public int getSelectedIndex() {
        return index;
    }

    public void setSelectedIndex(int index) {
        if (this.index != index) {
            this.index = index;
	    fireStateChanged();
        }
    }

    public void clearSelection() {
        setSelectedIndex(-1);
    }

    public boolean isSelected() {
	boolean ret = false;
	if (getSelectedIndex() != -1) {
	    ret = true;
	}
	return ret;
    }

    public void addChangeListener(ChangeListener l) {
	listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
	listenerList.remove(ChangeListener.class, l);
    }

    public void fireStateChanged() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==ChangeListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
	    }
	}
    }

    public EventListener[] getListeners(Class listenerType) {
	return listenerList.getListeners(listenerType);
    }





  } // InnerClass GenericTabPresentation

  public abstract class GenericTabPagePresentation  extends Table implements TabPagePresentation {

    private IWColor tabPageColor;

    public GenericTabPagePresentation(){
      super();
    }

//  public void add(PresentationObject obj){}
    public void setColor(IWColor color){
//      super.setColor(color);
      this.tabPageColor = color;
    }

    public IWColor getColor(){
      return this.tabPageColor;
    }

    public void setWidth(String width){
      super.setWidth(width);
    }
    public void setHeight(String height){
      super.setHeight(height);
    }
    public void fireContentChange(){}

  } // InnerClass GenericTabPagePresentation

} // Class GenericTabbedPaneUI