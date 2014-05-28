package com.idega.repository.data;

import com.idega.data.IDOHome;

public interface RepositoryFileHome extends IDOHome {
	
    public RepositoryFile create() throws javax.ejb.CreateException;

    public RepositoryFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

}