package com.idega.core.localisation.data;


public interface ICLanguageHome extends com.idega.data.IDOHome
{
 public ICLanguage create() throws javax.ejb.CreateException;
 public ICLanguage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;

}