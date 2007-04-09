package com.idega.core.builder.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface ICDomainHome extends IDOHome {
	public ICDomain create() throws CreateException;

	public ICDomain findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAllDomains() throws FinderException;

	public Collection findAllDomainsByServerName(String serverName) throws FinderException;

	public ICDomain findFirstDomain() throws FinderException;

	public ICDomain findDefaultDomain() throws FinderException;

	public ICDomain findDomainByServernameOrDefault(String serverName) throws FinderException;
}