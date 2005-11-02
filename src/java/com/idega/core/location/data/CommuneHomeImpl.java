/*
 * $Id: CommuneHomeImpl.java,v 1.4 2005/11/02 16:40:05 eiki Exp $
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
import com.idega.data.IDOFactory;
import com.idega.data.IDOLookupException;


/**
 * 
 *  Last modified: $Date: 2005/11/02 16:40:05 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4 $
 */
public class CommuneHomeImpl extends IDOFactory implements CommuneHome {

	protected Class getEntityInterfaceClass() {
		return Commune.class;
	}

	public Commune create() throws javax.ejb.CreateException {
		return (Commune) super.createIDO();
	}

	public Commune findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Commune) super.findByPrimaryKeyIDO(pk);
	}

	public Commune findDefaultCommune() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CommuneBMPBean) entity).ejbFindDefaultCommune();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllCommunes() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((CommuneBMPBean) entity).ejbFindAllCommunes();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Commune findByCommuneName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CommuneBMPBean) entity).ejbFindByCommuneName(name);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Commune findByCommuneNameAndProvince(String name, Object provinceID) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CommuneBMPBean) entity).ejbFindByCommuneNameAndProvince(name, provinceID);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Commune findByCommuneCode(String communeCode) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CommuneBMPBean) entity).ejbFindByCommuneCode(communeCode);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Commune findOtherCommmuneCreateIfNotExist() throws IDOLookupException, CreateException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CommuneBMPBean) entity).ejbFindOtherCommmuneCreateIfNotExist();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}
