package com.idega.core.component.data;


import javax.faces.component.UIComponent;

import com.idega.data.IDOEntity;
import com.idega.exception.IWBundleDoesNotExist;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.PresentationObject;

public interface ICObject extends IDOEntity {

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#isWidget
	 */
	public Boolean isWidget();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setIsWidget
	 */
	public void setIsWidget(Boolean isWidget);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#isBlock
	 */
	public Boolean isBlock();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setIsBlock
	 */
	public void setIsBlock(Boolean isBlock);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getIconURI
	 */
	public String getIconURI();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setIconURI
	 */
	public void setIconURI(String iconURI);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getDescription
	 */
	public String getDescription();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setDescripton
	 */
	public void setDescripton(String description);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setName
	 */
	public void setName(String object_name);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getClassName
	 */
	public String getClassName();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setClassName
	 */
	public void setClassName(String className);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getObjectClass
	 */
	public Class<? extends UIComponent> getObjectClass() throws ClassNotFoundException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setObjectClass
	 */
	public void setObjectClass(Class<? extends UIComponent> c);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getNewInstance
	 */
	public PresentationObject getNewInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getObjectType
	 */
	public String getObjectType();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setObjectType
	 */
	public void setObjectType(String objectType);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getBundleIdentifier
	 */
	public String getBundleIdentifier();

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setBundleIdentifier
	 */
	public void setBundleIdentifier(String bundleIdentifier);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setBundle
	 */
	public void setBundle(IWBundle bundle);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getBundle
	 */
	public IWBundle getBundle(IWMainApplication iwma) throws IWBundleDoesNotExist;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getID
	 */
	public int getID();
}