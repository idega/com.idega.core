/*
 * $Id: IDOEntity.java,v 1.17 2005/09/06 16:39:24 tryggvil Exp $ Created on
 * 2.9.2004
 * 
 * Copyright (C) 2002-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.data;

import java.util.Collection;

import javax.ejb.EJBLocalObject;

/**
 * <p>
 * This is the main super-interface for Entities for the IDO (Idega Data
 * Objects) Persistence framework. The main implementation of this interface is
 * currently GenericEntity but this interface was created to abstract make the
 * framework more EJB compatible and further abstract users of the entity
 * subclasses from GenericEntity, as that type contains many legacy and older
 * style methods.<br/> There is also an older style IDOLegacyEntity that
 * contains many methods that are in GenericEntity and used by legacy code.
 * </p>
 * Last modified: $Date: 2005/09/06 16:39:24 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.17 $
 */
public interface IDOEntity extends EJBLocalObject, Comparable<IDOEntity> {

	/**
	 * <p>
	 * This is an addition by IDO to the EJB standard.<br/>
	 * This makes the entity bean do either an insert or update to the datastore.
	 * </p>
	 * @throws IDOStoreException
	 */
	public void store() throws IDOStoreException;

	public IDOEntityDefinition getEntityDefinition();

	public Integer decode(String pkString);

	public Collection<Integer> decode(String[] pkString);

	public String getDatasource();

	public void setDatasource(String datasource);
}
