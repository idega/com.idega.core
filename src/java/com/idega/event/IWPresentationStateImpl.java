package com.idega.event;

import java.util.EventListener;
import com.idega.idegaweb.IWPresentationLocation;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.StatefullPresentationImplHandler;

import javax.swing.event.*;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public abstract class IWPresentationStateImpl implements IWPresentationState {

	/**
	 * 
	 * @uml.property name="listenerList"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected EventListenerList listenerList = new EventListenerList();


  protected boolean _stateHasChanged = false;

	/**
	 * 
	 * @uml.property name="changeEvent"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	protected ChangeEvent changeEvent = null;

	/**
	 * 
	 * @uml.property name="_location"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	protected IWLocation _location = new IWPresentationLocation();

  
  // compoundId of the corresponding presentation object
  protected String compoundId = null;
  protected String artificialCompoundId = null;

	/**
	 * 
	 * @uml.property name="iwuc"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	protected IWUserContext iwuc = null;

 
  public IWPresentationStateImpl() {
  }

  public void reset(){
//    this.listenerList = new EventListenerList();
//    this._stateHasChanged = false;
    this.changeEvent = null;
//    this._location = new IWPresentationLocation();
  }

//  public abstract void setStateValue(String stateName, Object value);
//  public abstract Object getStateValue(String stateName);


  public boolean stateHasChanged(){
    return this._stateHasChanged;
  }

  public IWLocation getLocation(){
    return this._location;
  }

  public void setLocation(IWLocation location){
    this._location = location;
  }

  /**
   * Send a <code>ChangeEvent</code>
   *
   * @see #addChangeListener
   * @see EventListenerList
   */
  public void fireStateChanged() {
      // Guaranteed to return a non-null array
      Object[] listeners = this.listenerList.getListenerList();
//      System.out.println("ChangeEventListener.listenerList: "+ listenerList);
//      System.out.println("ChangeEventListener: "+ listeners);
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2) {
          if (listeners[i]==ChangeListener.class) {
              // Lazily create the event:
              if (this.changeEvent == null) {
								this.changeEvent = new ChangeEvent(this);
							}
                ((ChangeListener)listeners[i+1]).stateChanged(this.changeEvent);
          }
      }
      this._stateHasChanged = false;
  }

  public ChangeListener[] getChangeListener(){
    return this.listenerList.getListeners(ChangeListener.class);
  }

  /**
   * TEMP temp solution for checking multiple addition of the same listeners
   * @param t
   * @param l
   * @return
   */
  private synchronized boolean listenerListContains(Class t, EventListener l) {
	if (l ==null) {
	    return false;
	}
	
	Object[] tmpList = this.listenerList.getListenerList(); 
	for (int i = tmpList.length-2; i>=0; i-=2) {
	    if ((tmpList[i]==t) && (tmpList[i+1].equals(l) == true)) {
		return true;
	    }
	}
	return false;
  }
  
  public void addChangeListener(ChangeListener listener) {
  	//TEMP solution for checking multiple addition of the same listeners
  	// it might not always be right not to add listener again.
  	if(!listenerListContains(ChangeListener.class,listener)){
  		this.listenerList.add(ChangeListener.class,listener);
  	}
  }

  public void removeChangeListener(ChangeListener listener){
    this.listenerList.remove(ChangeListener.class,listener);
  }

  public Object clone() {
    IWPresentationStateImpl obj = null;
    try {
      obj = (IWPresentationStateImpl)super.clone();

      obj.listenerList = new EventListenerList();
      Object[] thisList = this.listenerList.getListenerList();
      for (int i = 0; i < thisList.length-1; i+= 2) {
        obj.listenerList.add((Class)thisList[i],(EventListener)thisList[i+1]);
      }
      obj._stateHasChanged = false;
      obj.changeEvent = null;

      if(this._location != null){
      obj._location = (IWLocation)this._location.clone();
      }

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

	/**
	 * Returns the compoundId.
	 * @return String
	 */
	public String getCompoundId() {
		return this.compoundId;
	}

	/**
	 * Sets the compoundId.
	 * @param compoundId The compoundId to set
	 */
	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}
  
  public void setUserContext(IWUserContext iwuc) {
    this.iwuc = iwuc;
  }
  
  /**
   * @see javax.faces.component.UIComponent#findComponent(java.lang.String
   */
  public IWPresentationState findComponent(String expr) {
    // to do: check more expressions!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    String anotherCompoundId = "";
    if ("..".equals(expr))  {
      String myCompoundId = getCompoundId();
      int i = myCompoundId.lastIndexOf("/");
      anotherCompoundId = myCompoundId.substring(0, i);
    }
    return StatefullPresentationImplHandler.getPresentationState(anotherCompoundId, this.iwuc);
  }

	/**
	 * Sets the artificialCompoundId.
	 * @param artificialCompoundId The artificialCompoundId to set
	 * 
	 * @uml.property name="artificialCompoundId"
	 */
	public void setArtificialCompoundId(String artificialCompoundId) {
		this.artificialCompoundId = artificialCompoundId;
	}

	/**
	 * Returns the artificialCompoundId.
	 * @return String
	 * 
	 * @uml.property name="artificialCompoundId"
	 */
	public String getArtificialCompoundId() {
		return this.artificialCompoundId;
	}

}