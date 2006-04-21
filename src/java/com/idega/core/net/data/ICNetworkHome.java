package com.idega.core.net.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface ICNetworkHome extends IDOHome {

	public ICNetwork create() throws CreateException;

	public ICNetwork findByPrimaryKey(Object pk) throws FinderException;
}