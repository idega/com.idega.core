package com.idega.core.builder.data;

import java.util.Collection;
import java.util.List;

import javax.ejb.FinderException;

public interface ICPageHome extends com.idega.data.IDOHome {
	
	public ICPage create() throws javax.ejb.CreateException;

	public ICPage createLegacy();

	public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException;

	public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

	public Collection<ICPage> findByTemplate(Integer integer) throws javax.ejb.FinderException;

	public ICPage findByUri(String pageUri, int domainId) throws javax.ejb.FinderException;

	public ICPage findExistingByUri(String pageUri, int domainId) throws FinderException;
	
	public ICPage findByWebDavUri(String webDavUri) throws FinderException;

	public Collection<ICPage> findAllPagesWithoutUri() throws FinderException;

	public Collection<ICPage> findAllSimpleTemplates() throws FinderException;

	public Collection<ICPage> findBySubType(String subType, boolean deleted) throws FinderException;
	
	public Collection<ICPage> findAllByPhrase(String phrase, List<String> idsToAvoid) throws FinderException;
	
	public Collection<ICPage> findAllByPrimaryKeys(List<String> primaryKeys) throws FinderException;
	
	public Collection<ICPage> findAllByName(String name, boolean findOnlyNotDeleted) throws FinderException;

	public Collection<ICPage> findAllPagesAndTemplates() throws FinderException;

	public Collection<ICPage> findAllTemplatesWithWebDavUri() throws FinderException;
	
}