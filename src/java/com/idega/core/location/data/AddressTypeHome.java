/**
 * 
 */
package com.idega.core.location.data;


import javax.ejb.FinderException;

import com.idega.data.IDOHome;

/**
 * @author bluebottle
 *
 */
public interface AddressTypeHome extends IDOHome {
	public AddressType create() throws javax.ejb.CreateException;

	public AddressType findByPrimaryKey(Object pk)
			throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.AddressTypeBMPBean#ejbFindAddressType1
	 */
	public AddressType findAddressType1() throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressTypeBMPBean#ejbFindAddressType2
	 */
	public AddressType findAddressType2() throws FinderException;

	/**
	 * @see com.idega.core.location.data.AddressTypeBMPBean#ejbFindByUniqueName
	 */
	public AddressType findByUniqueName(String name) throws FinderException;

}
