package com.idega.core.builder.data;

import java.util.Collection;
import javax.ejb.FinderException;

public interface ICPageHome extends com.idega.data.IDOHome {
	
	public ICPage create() throws javax.ejb.CreateException;

	public ICPage createLegacy();

	public ICPage findByPrimaryKey(int id) throws javax.ejb.FinderException;

	public ICPage findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	public ICPage findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

	/**
	 * @param integer
	 * @return
	 */
	public Collection findByTemplate(Integer integer)
			throws javax.ejb.FinderException;

	public ICPage findByUri(String pageUri, int domainId)
			throws javax.ejb.FinderException;

	public ICPage findExistingByUri(String pageUri, int domainId)
			throws FinderException;

	/**
	 * @return
	 */
	public Collection findAllPagesWithoutUri() throws FinderException;

	public Collection findAllSimpleTemplates() throws FinderException;

	public Collection findBySubType(String subType, boolean deleted)
			throws FinderException;
}