package com.idega.core.location.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * @author gimmi
 */
public class CountryHomeImpl extends IDOFactory implements CountryHome {

	protected Class getEntityInterfaceClass() {
		return Country.class;
	}

	public Country create() throws javax.ejb.CreateException {
		return (Country) super.createIDO();
	}

	public Country findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (Country) super.findByPrimaryKeyIDO(pk);
	}

	public Country findByIsoAbbreviation(String abbreviation) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CountryBMPBean) entity).ejbFindByIsoAbbreviation(abbreviation);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Country findByCountryName(String name) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((CountryBMPBean) entity).ejbFindByCountryName(name);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((CountryBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllFromPostalCodes(Collection postalCodes) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((CountryBMPBean) entity).ejbFindAllFromPostalCodes(postalCodes);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
