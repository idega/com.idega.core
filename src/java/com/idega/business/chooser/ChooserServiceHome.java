package com.idega.business.chooser;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface ChooserServiceHome extends IBOHome {
	public ChooserService create() throws CreateException, RemoteException;
}