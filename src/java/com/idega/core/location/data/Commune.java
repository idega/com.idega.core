/*
 * $Id: Commune.java,v 1.3 2005/11/02 16:40:05 eiki Exp $
 * Created on Nov 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import com.idega.data.IDOEntity;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2005/11/02 16:40:05 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface Commune extends IDOEntity {

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setCommuneName
	 */
	public void setCommuneName(String name);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getCommuneName
	 */
	public String getCommuneName();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setCommuneWebsiteURL
	 */
	public void setCommuneWebsiteURL(String URL);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getCommuneWebsiteURL
	 */
	public String getCommuneWebsiteURL();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setCommuneCode
	 */
	public void setCommuneCode(String code);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getCommuneCode
	 */
	public String getCommuneCode();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setProvince
	 */
	public void setProvince(Province province);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getProvince
	 */
	public Province getProvince();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setProvinceID
	 */
	public void setProvinceID(int province_id);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getProvinceID
	 */
	public int getProvinceID();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setGroup
	 */
	public void setGroup(Group group);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getGroup
	 */
	public Group getGroup();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setGroupID
	 */
	public void setGroupID(int group_id);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getGroupID
	 */
	public int getGroupID();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getIsValid
	 */
	public boolean getIsValid();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setValid
	 */
	public void setValid(boolean isValid);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setIsValid
	 */
	public void setIsValid(boolean isValid);

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#getIsDefault
	 */
	public boolean getIsDefault();

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#setIsDefault
	 */
	public void setIsDefault(boolean isDefault);
}
