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

	public RealEstate findRealEstateByRealEstateIdentifier(String landRegisterMapNumber, String number, String unit,
			String code) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((RealEstateBMPBean) entity).ejbFindRealEstateByRealEstateIdentifier(landRegisterMapNumber, number,
				unit, code);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}