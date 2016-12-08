package com.idega.core.file.data;

import java.util.Collection;
import java.util.UUID;

public interface ICFileHome extends com.idega.data.IDOHome {

	/**
	 * 
	 * @param uuid is {@link UUID} value, not <code>null</code>
	 * @return entity or <code>null</code> on failure
	 */
	ICFile findByUUID(String uuid);

 public ICFile create() throws javax.ejb.CreateException;
 public ICFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Collection<ICFile> findAllDescendingOrdered()throws javax.ejb.FinderException;
 public ICFile findByFileName(java.lang.String p0)throws javax.ejb.FinderException;
 public ICFile findEntityOfSpecificVersion(com.idega.core.version.data.ICVersion p0)throws javax.ejb.FinderException;
 public ICFile findRootFolder()throws javax.ejb.FinderException;
 public Collection<ICFile> findChildren(ICFile parent, Collection<String> visibleMimeTypes, Collection<String> hiddenMimeTypes, String orderBy) throws javax.ejb.FinderException;
 public Collection<ICFile> findChildren(ICFile parent, Collection<String> visibleMimeTypes, Collection<String> hiddenMimeTypes, String orderBy, int starting, int numberOfReturns) throws javax.ejb.FinderException;
 public ICFile findByHash(Integer hash) throws javax.ejb.FinderException;

}