package com.idega.core.location.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface RealEstateHome extends IDOHome {

	public RealEstate create() throws CreateException;

	public RealEstate findByPrimaryKey(Object pk) throws FinderException;

	public RealEstate findRealEstateByRealEstateIdentifier(String landRegisterMapNumber, String number, String unit,
			String code) throws FinderException;
}