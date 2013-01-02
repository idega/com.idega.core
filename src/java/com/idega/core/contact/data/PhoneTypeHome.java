package com.idega.core.contact.data;

import java.util.Collection;


public interface PhoneTypeHome extends com.idega.data.IDOHome
{
 public PhoneType create() throws javax.ejb.CreateException;
 public PhoneType createLegacy();
 public PhoneType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public PhoneType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public PhoneType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;
 public Collection<PhoneType> getPhoneTypes(int maxAmount);

}