package com.idega.core.location.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class StreetHomeImpl extends IDOFactory implements StreetHome {

	public Class getEntityInterfaceClass() {
		return Street.class;
	}

	public Street create() throws CreateException {
		return (Street) super.createIDO();
	}

	public Street findByPrimaryKey(Object pk) throws FinderException {
		return (Street) super.findByPrimaryKeyIDO(pk);
	}

	public Street findStreetByPostalCodeAndNameOrNameDativ(PostalCode postalCode, String name, String nameDativ) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((StreetBMPBean) entity).ejbFindStreetByPostalCodeAndNameOrNameDativ(postalCode, name, nameDativ);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}