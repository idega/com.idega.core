package com.idega.core.location.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class RealEstateHomeImpl extends IDOFactory implements RealEstateHome {

	public Class getEntityInterfaceClass() {
		return RealEstate.class;
	}

	public RealEstate create() throws CreateException {
		return (RealEstate) super.createIDO();
	}

	public RealEstate findByPrimaryKey(Object pk) throws FinderException {
		return (RealEstate) super.findByPrimaryKeyIDO(pk);
	}

	public RealEstate findRealEstateByNumber(String number) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RealEstateBMPBean) entity).ejbFindRealEstateByNumber(number);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}