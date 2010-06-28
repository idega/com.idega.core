package com.idega.core.net.data;

import javax.ejb.FinderException;

public interface ICProtocolHome extends com.idega.data.IDOHome {

	public ICProtocol create() throws javax.ejb.CreateException;

	public ICProtocol createLegacy();

	public ICProtocol findByPrimaryKey(int id) throws javax.ejb.FinderException;

	public ICProtocol findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	public ICProtocol findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

	public ICProtocol findByName(String name) throws FinderException;
}