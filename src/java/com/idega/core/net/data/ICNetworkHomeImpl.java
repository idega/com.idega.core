package com.idega.core.net.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class ICNetworkHomeImpl extends IDOFactory implements ICNetworkHome {

	public Class getEntityInterfaceClass() {
		return ICNetwork.class;
	}

	public ICNetwork create() throws CreateException {
		return (ICNetwork) super.createIDO();
	}

	public ICNetwork findByPrimaryKey(Object pk) throws FinderException {
		return (ICNetwork) super.findByPrimaryKeyIDO(pk);
	}
}