package com.idega.idegaweb.block.business;


public interface FolderBlockBusiness extends com.idega.business.IBOService
{
 public void addCategoryToInstance(com.idega.core.category.data.ICInformationCategory p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void addCategoryToInstance(com.idega.core.component.data.ICObjectInstance p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public java.util.Collection collectCategoryIntegerIds(int p0) throws java.rmi.RemoteException;
 public boolean copyCategoryAttachments(int p0,int p1) throws java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationCategory createICInformationCategory(com.idega.presentation.IWContext p0,int p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,int p5,int p6) throws java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationCategoryTranslation createICInformationCategoryTranslation(int p0,java.lang.String p1,java.lang.String p2,int p3) throws java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationFolder createICInformationFolder(java.lang.String p0,java.lang.String p1,java.lang.String p2,int p3,int p4,int p5)throws java.sql.SQLException, java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationFolder createICInformationFolderForLocale(java.lang.String p0,int p1,int p2,int p3)throws java.sql.SQLException, java.rmi.RemoteException;
 public boolean detachWorkfolderFromObjectInstance(com.idega.core.component.data.ICObjectInstance p0) throws java.rmi.RemoteException;
 public java.util.Collection getAvailableCategories(int p0,int p1) throws java.rmi.RemoteException;
 public java.util.Collection getAvailableTopNodeCategories(int p0,int p1) throws java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationCategory getCategory(int p0) throws java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationCategoryHome getCategoryHome()throws com.idega.data.IDOLookupException, java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationCategoryTranslationHome getCategoryTranslationHome()throws com.idega.data.IDOLookupException, java.rmi.RemoteException;
 public java.util.List getInstanceCategories(int p0) throws java.rmi.RemoteException;
 public java.util.List getInstanceViewFolders(int p0) throws java.rmi.RemoteException;
 public com.idega.core.category.data.ICInformationFolder getInstanceWorkeFolder(int p0,int p1,int p2,boolean p3) throws java.rmi.RemoteException;
 public boolean removeCategory(com.idega.presentation.IWContext p0,int p1) throws java.rmi.RemoteException;
 public void removeCategoryFromInstance(com.idega.core.category.data.ICInformationCategory p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void removeCategoryFromInstance(com.idega.core.component.data.ICObjectInstance p0,int p1)throws java.sql.SQLException, java.rmi.RemoteException;
 public void storeCategoryToParent(int p0,int p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public void storeInstanceCategories(int p0,int[] p1) throws java.rmi.RemoteException;
 public boolean updateCategory(com.idega.presentation.IWContext p0,int p1,java.lang.String p2,java.lang.String p3,int p4)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public boolean hasAvailableCategory(int icObjectId);
}
