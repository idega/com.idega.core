/*
 * $Id: CommuneBusiness.java,v 1.3 2004/09/14 15:04:06 joakim Exp $
 * Created on 14.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.business.IBOService;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.PostalCode;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2004/09/14 15:04:06 $ by $Author: joakim $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim</a>
 * @version $Revision: 1.3 $
 */
public interface CommuneBusiness extends IBOService {

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getCommunes
	 */
	public Collection getCommunes() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getCommune
	 */
	public Commune getCommune(int communeId) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getCommuneByCode
	 */
	public Commune getCommuneByCode(String code) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getCommuneByName
	 */
	public Commune getCommuneByName(String name) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getDefaultCommune
	 */
	public Commune getDefaultCommune() throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getOtherCommuneCreateIfNotExist
	 */
	public Commune getOtherCommuneCreateIfNotExist() throws CreateException, FinderException, RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getGroup
	 */
	public Group getGroup(Commune commune) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getCommuneByPostalCode
	 */
	public Commune getCommuneByPostalCode(PostalCode postalCode) throws java.rmi.RemoteException;

	/**
	 * @see com.idega.core.location.business.CommuneBusinessBean#getCommuneHome
	 */
	public CommuneHome getCommuneHome() throws IDOLookupException, java.rmi.RemoteException;
}
