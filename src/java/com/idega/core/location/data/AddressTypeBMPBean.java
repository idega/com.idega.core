package com.idega.core.location.data;

import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.core.data.GenericTypeBMPBean;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;

/**
 * 
 * Title: IW Core
 * 
 * Description:
 * 
 * Copyright: Copyright (c) 2001
 * 
 * Company: idega.is
 * 
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st
 *         S�mundsson</a>
 * 
 * @version 1.0
 * 
 */

public class AddressTypeBMPBean extends GenericTypeBMPBean implements
		AddressType {
	protected final static String ENTITY_NAME = "ic_address_type";

	public final static String ADDRESS_1 = "ic_user_address_1";

	public final static String ADDRESS_2 = "ic_user_address_2";

	public String getEntityName() {
		return ENTITY_NAME;
	}

	public void insertStartData() throws Exception {
		AddressType at = ((AddressTypeHome) IDOLookup
				.getHome(AddressType.class)).create();
		at.setName("Home");
		at.setDescription("Home");
		at.setUniqueName(ADDRESS_1);
		at.store();

		at = ((AddressTypeHome) IDOLookup.getHome(AddressType.class)).create();
		at.setName("Work");
		at.setDescription("Work");
		at.setUniqueName(ADDRESS_2);
		at.store();

	}

	public static int getId(String uniqueKey) throws SQLException {
		int returner = 0;

		try {
			AddressTypeHome addrHome = (AddressTypeHome) IDOLookup
					.getHome(AddressType.class);
			AddressType addrType;
			try {
				addrType = addrHome.findByUniqueName(uniqueKey);
				returner = ((Integer) addrType.getPrimaryKey()).intValue();
			} catch (FinderException e) {
				try {
					addrType = addrHome.create();
					addrType.setName("");
					addrType.setDescription("");
					addrType.setUniqueName(uniqueKey);
					addrType.store();
					returner = ((Integer) addrType.getPrimaryKey()).intValue();
				} catch (CreateException e1) {
					throw new SQLException(e1.getMessage());
				}
			}
		} catch (IDOLookupException e) {
			throw new SQLException(e.getMessage());
		}

		return returner;
	}

	public Integer ejbFindAddressType1() throws FinderException {
		Collection coll = super.idoFindAllIDsByColumnBySQL(
				getColumnNameUniqueName(), ADDRESS_1);
		if (!coll.isEmpty()) {
			return (Integer) coll.iterator().next();
		}
		else {
			throw new FinderException("AddressType1 does not exist");
		}
	}

	public Integer ejbFindAddressType2() throws FinderException {
		Collection coll = super.idoFindAllIDsByColumnBySQL(
				getColumnNameUniqueName(), ADDRESS_2);
		if (!coll.isEmpty()) {
			return (Integer) coll.iterator().next();
		}
		else {
			throw new FinderException("AddressType2 does not exist");
		}
	}

	public Object ejbFindByUniqueName(String name) throws FinderException {
		IDOQuery query = this.idoQueryGetSelect();
		query.appendWhereEqualsQuoted(getColumnNameUniqueName(), name);

		return idoFindOnePKByQuery(query);
	}
}