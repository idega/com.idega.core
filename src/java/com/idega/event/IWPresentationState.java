package com.idega.event;

import javax.swing.event.ChangeListener;

import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWUserContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */


public interface IWPresentationState extends Cloneable {
  public void reset();

/**
 * 
 * @uml.property name="location"
 * @uml.associationEnd multiplicity="(0 1)"
 */
//  public void setStateValue(String stateName,Object value);
//  public Object getStateValue(String stateName);
public IWLocation getLocation();

	/**
	 * 
	 * @uml.property name="location"
	 */
	public void setLocation(IWLocation location);

	/**
	 * 
	 * @uml.property name="compoundId"
	 */
	public String getCompoundId();

	/**
	 * 
	 * @uml.property name="compoundId"
	 */
	public void setCompoundId(String compoundId);

  public void setUserContext(IWUserContext iuwc);

	/**
	 * 
	 * @uml.property name="artificialCompoundId"
	 */
	public void setArtificialCompoundId(String artificialCompoundId);

	/**
	 * 
	 * @uml.property name="artificialCompoundId"
	 */
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