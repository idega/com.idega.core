/*
 * $Id: CommuneHome.java,v 1.4 2005/11/02 16:40:05 eiki Exp $
 * Created on Nov 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2005/11/02 16:40:05 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public interface CommuneHome extends IDOHome {

	public Commune create() throws javax.ejb.CreateException;

	public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#ejbFindDefaultCommune
	 */
	public Commune findDefaultCommune() throws FinderException;

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#ejbFindAllCommunes
	 */
	public Collection<Commune> findAllCommunes() throws FinderException;

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#ejbFindByCommuneName
	 */
	public Commune findByCommuneName(String name) throws FinderException;

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#ejbFindByCommuneNameAndProvince
	 */
	public Commune findByCommuneNameAndProvince(String name, Object provinceID) throws FinderException;

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#ejbFindByCommuneCode
	 */
	public Commune findByCommuneCode(String communeCode) throws FinderException;

	/**
	 * @see com.idega.core.location.data.CommuneBMPBean#ejbFindOtherCommmuneCreateIfNotExist
	 */
	public Commune findOtherCommmuneCreateIfNotExist() throws IDOLookupException, CreateException, FinderException;
}
