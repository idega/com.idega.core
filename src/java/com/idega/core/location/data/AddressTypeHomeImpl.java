/**
 * 
 */
package com.idega.core.location.data;


import javax.ejb.FinderException;

import com.idega.data.IDOFactory;

/**
 * @author bluebottle
 *
 */
public class AddressTypeHomeImpl extends IDOFactory implements AddressTypeHome {
	protected Class getEntityInterfaceClass() {
		return AddressType.class;
	}

	public AddressType create() throws javax.ejb.CreateException {
		return (AddressType) super.createIDO();
	}

	public AddressType findByPrimaryKey(Object pk)
			throws javax.ejb.FinderException {
		return (AddressType) super.findByPrimaryKeyIDO(pk);
	}

	public AddressType findAddressType1() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressTypeBMPBean) entity).ejbFindAddressType1();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public AddressType findAddressType2() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressTypeBMPBean) entity).ejbFindAddressType2();
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public AddressType findByUniqueName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AddressTypeBMPBean) entity).ejbFindByUniqueName(name);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

}
