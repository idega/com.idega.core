/*
 * $Id: CommuneHome.java,v 1.3 2004/09/14 15:04:06 joakim Exp $
 * Created on 14.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
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
 *  Last modified: $Date: 2004/09/14 15:04:06 $ by $Author: joakim $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim</a>
 * @version $Revision: 1.3 $
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
	public Collection findAllCommunes() throws FinderException;

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
