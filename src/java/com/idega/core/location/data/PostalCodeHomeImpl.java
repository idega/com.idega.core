package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;


/**
 * @author gimmi
 */
public class PostalCodeHomeImpl extends IDOFactory implements PostalCodeHome {

	protected Class getEntityInterfaceClass() {
		return PostalCode.class;
	}

	public PostalCode create() throws javax.ejb.CreateException {
		return (PostalCode) super.createIDO();
	}

	public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (PostalCode) super.findByPrimaryKeyIDO(pk);
	}

	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeAndCountryId(code, countryId);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllByCountryIdOrderedByPostalCode(countryId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbHomeGetUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(countryId);
		this.idoCheckInPooledEntity(entity);
		return theReturn;
	}

	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindByNameAndCountry(name, countryPK);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByCountry(Object countryPK) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindByCountry(countryPK);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllUniqueNames() throws RemoteException, FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllUniqueNames();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllOrdererByCode() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((PostalCodeBMPBean) entity).ejbFindAllOrdererByCode();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection theReturn = ((PostalCodeBMPBean) entity).ejbFindByPostalCodeFromTo(codeFrom, codeTo);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(theReturn);
	}
}
