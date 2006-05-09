package com.idega.core.component.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface ICObjectHome extends IDOHome {

	public ICObject create() throws CreateException;

	public ICObject findByPrimaryKey(Object pk) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllByObjectType
	 */
	public Collection findAllByObjectType(String type) throws FinderException;

	/**
	 * @see com.idega.core.component.data.ICObjectBMPBean#ejbFindAllByObjectTypeOrdered
	 */
	public Collection findAllByObjectTypeOrdered(String type) throws FinderException;

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