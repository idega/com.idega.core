package com.idega.versioncontrol.business;


public interface UpdateService extends com.idega.business.IBOService
{
 public boolean updateBundleToMostRecentVersion(String bundleIdentifier) throws java.rmi.RemoteException;
 public boolean updateBundleToMostRecentVersion(com.idega.idegaweb.IWBundle p0) throws java.rmi.RemoteException;
}
