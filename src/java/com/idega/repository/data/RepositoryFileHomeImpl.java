package com.idega.repository.data;

import com.idega.data.IDOFactory;

public class RepositoryFileHomeImpl extends IDOFactory implements RepositoryFileHome {
	
	private static final long serialVersionUID = -3362886386156031550L;

	@SuppressWarnings("unchecked")
	protected Class<RepositoryFile> getEntityInterfaceClass() {
        return RepositoryFile.class;
    }

    public RepositoryFile create() throws javax.ejb.CreateException {
        return (RepositoryFile) super.createIDO();
    }

    public RepositoryFile findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
        return (RepositoryFile) super.findByPrimaryKeyIDO(pk);
    }

}