package com.idega.versioncontrol.business;

import java.rmi.RemoteException;
import java.util.Locale;

import com.idega.idegaweb.IWBundle;


public interface UpdateService extends com.idega.business.IBOService
{
 public boolean updateBundleToMostRecentVersion(String bundleIdentifier) throws java.rmi.RemoteException;
 public boolean updateBundleToMostRecentVersion(String bundleIdentifier,boolean overWriteLocalChanges) throws java.rmi.RemoteException;
 public boolean updateBundleToMostRecentVersion(com.idega.idegaweb.IWBundle bundle) throws java.rmi.RemoteException;
 public boolean updateBundleToMostRecentVersion(com.idega.idegaweb.IWBundle bundle,boolean overWriteLocalChanges) throws java.rmi.RemoteException;
 public boolean commitLocalizationFile(String bundleIdentifier,String localeString) throws RemoteException;
 public boolean commitLocalizationFile(IWBundle bundle,Locale locale) throws RemoteException;
}
