package com.idega.core.contact.data;


public interface EmailTypeHome extends com.idega.data.IDOHome
{
 public EmailType create() throws javax.ejb.CreateException;
 public EmailType createLegacy();
 public EmailType findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public EmailType findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public EmailType findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}