/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.core.component.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * <p>
 * TODO thomas Describe Type ICObjectHome
 * </p>
 *  Last modified: $Date: 2005/06/03 15:18:29 $ by $Author: thomas $
 * 
 * @author <a href="mailto:thomas@idega.com">thomas</a>
 * @version $Revision: 1.2 $
 */
public interface ICObjectHome extends IDOHome {
	
	public ICObject findByPrimaryKey(int id) throws javax.ejb.FinderException;
	public ICObject findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
	public ICObject createLegacy();

	public ICObject create() throws javax.ejb.CreateException;

	public ICObject findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllByObjectType
	 */
	public Collection findAllByObjectType(String type) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllByObjectTypeAndBundle
	 */
	public Collection findAllByObjectTypeAndBundle(String type, String bundle) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllByBundle
	 */
	public Collection findAllByBundle(String bundle) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindByClassName
	 */
	public ICObject findByClassName(String className) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllBlocksByBundle
	 */
	public Collection findAllBlocksByBundle(String bundle) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllBlocks
	 */
	public Collection findAllBlocks() throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllElementsByBundle
	 */
	public Collection findAllElementsByBundle(String bundle) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllElements
	 */
	public Collection findAllElements() throws FinderException;
}
