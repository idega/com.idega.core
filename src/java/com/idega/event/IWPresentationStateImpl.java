package com.idega.event;

import java.util.EventListener;
import com.idega.idegaweb.IWPresentationLocation;
import com.idega.idegaweb.IWLocation;
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

  protected EventListenerList listenerList = new EventListenerList();

  protected boolean _stateHasChanged = false;

  protected ChangeEvent changeEvent = null;

  protected IWLocation _location = new IWPresentationLocation();

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
    return _stateHasChanged;
  }

  public IWLocation getLocation(){
    return _location;
  }

  public void setLocation(IWLocation location){
    _location = location;
  }

  /**
   * Send a <code>ChangeEvent</code>
   *
   * @see #addChangeListener
   * @see EventListenerList
   */
  public void fireStateChanged() {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
//      System.out.println("ChangeEventListener.listenerList: "+ listenerList);
//      System.out.println("ChangeEventListener: "+ listeners);
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
      _stateHasChanged = false;
  }

  public ChangeListener[] getChangeListener(){
    return (ChangeListener[])listenerList.getListeners(ChangeListener.class);
  }

  public void addChangeListener(ChangeListener listener) {
    listenerList.add(ChangeListener.class,listener);
  }

  public void removeChangeListener(ChangeListener listener){
    listenerList.remove(ChangeListener.class,listener);
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

      if(this._location instanceof Cloneable){
      obj._location = (IWLocation)this._location.clone();
      }

    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }

}