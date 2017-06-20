package com.idega.core.localisation.data;


public interface ICLanguageHome extends com.idega.data.IDOHome
{
 public ICLanguage create() throws javax.ejb.CreateException;
 public ICLanguage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public ICLanguage findByDescription(java.lang.String p0)throws javax.ejb.FinderException;
 public ICLanguage findByISOAbbreviation(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection<ICLanguage> findManyByISOAbbreviation(java.util.Collection<java.lang.String> p0)throws javax.ejb.FinderException;

}