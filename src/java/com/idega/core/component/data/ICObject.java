/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.core.component.data;

import com.idega.exception.IWBundleDoesNotExist;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.PresentationObject;


/**
 * <p>
 * TODO thomas Describe Type ICObject
 * </p>
 *  Last modified: $Date: 2006/03/28 10:20:10 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.4 $
 */
public interface ICObject extends com.idega.data.IDOLegacyEntity {
	
	public void initializeAttributes();

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
	public Class getObjectClass() throws ClassNotFoundException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#setObjectClass
	 */
	public void setObjectClass(Class c);

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#getNewInstance
	 */
	public PresentationObject getNewInstance() throws ClassNotFoundException, IllegalAccessException,
			InstantiationException;

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
	 * @see com.idega.core.component.data.ICObjectBMPBean#compareTo
	 */
	public int compareTo(Object obj);
	
}
