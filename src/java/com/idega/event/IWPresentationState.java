package com.idega.event;

import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWUserContext;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface IWPresentationState extends Cloneable {
  public void reset();
//  public void setStateValue(String stateName,Object value);
//  public Object getStateValue(String stateName);

  public IWLocation getLocation();
  public void setLocation(IWLocation location);
  
  public String getCompoundId();
  public void setCompoundId(String compoundId);
  public void setUserContext(IWUserContext iuwc);
  public void setArtificialCompoundId(String artificialCompoundId);
  public String getArtificialCompoundId();
  /**
   * @see javax.faces.component.UIComponent#findComponent(java.lang.String
   */
  public IWPresentationState findComponent(String expr);

  public boolean stateHasChanged();
  public void fireStateChanged();
  public ChangeListener[] getChangeListener();
  public void addChangeListener(ChangeListener listener);
  public void removeChangeListener(ChangeListener listener);

  public Object clone();

}