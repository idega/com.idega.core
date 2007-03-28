package com.idega.core.location.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface StreetHome extends IDOHome {

	public Street create() throws CreateException;

	public Street findByPrimaryKey(Object pk) throws FinderException;

	public Street findStreetByPostalCodeAndNameOrNameDativ(PostalCode postalCode, String name, String nameDativ) throws FinderException;
}