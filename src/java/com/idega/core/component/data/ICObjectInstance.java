package com.idega.core.component.data;


import com.idega.data.IDOLegacyEntity;
import com.idega.presentation.PresentationObject;

public interface ICObjectInstance extends IDOLegacyEntity {

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#setICObjectID
	 */
	public void setICObjectID(int id);

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#setICObject
	 */
	public void setICObject(ICObject object);

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getIBPageID
	 */
	public int getIBPageID();

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#setIBPageID
	 */
	public void setIBPageID(int id);

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getParentInstanceID
	 */
	public int getParentInstanceID();

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#setParentInstanceID
	 */
	public void setParentInstanceID(int id);

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getObject
	 */
	public ICObject getObject();

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getNewInstance
	 */
	public PresentationObject getNewInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException;

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#setIBPageByKey
	 */
	public void setIBPageByKey(String pageKey);

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getUniqueId
	 */
	public String getUniqueId();

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#setUniqueId
	 */
	public void setUniqueId(String uniqueId);

	/**
	 * @see com.idega.core.component.data.ICObjectInstanceBMPBean#getID
	 */
	public int getID();
}