package com.idega.core.category.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import com.idega.business.IBOServiceBean;
import com.idega.core.category.data.*;
import com.idega.core.category.data.ICCategory;
import com.idega.core.category.data.ICCategoryHome;
import com.idega.core.category.data.ICCategoryTranslation;
import com.idega.core.category.data.ICCategoryTranslationHome;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.data.IDOLookup;
import com.idega.util.IWTimestamp;


public class CategoryServiceBean extends  IBOServiceBean implements CategoryService {
	
	public boolean disconnectBlock(int instanceid) throws RemoteException {
		Collection L =null;
		try{
			L = getCategoryHome().findAllByObjectInstance(instanceid);
		}
		catch(FinderException ex)
		{throw new RemoteException(ex.getMessage());}
			if (L != null && !L.isEmpty()) {
				Iterator I = L.iterator();
				while (I.hasNext()) {
					ICCategory Cat = (ICCategory) I.next();
					disconnectCategory(Cat, instanceid);
				}
				return true;
			}
			else
				return false;
		
	}
	public boolean disconnectCategory(ICCategory Cat, int iObjectInstanceId) {
		try {
			if (iObjectInstanceId > 0) {
				ICObjectInstance obj =
					(
						(ICObjectInstanceHome) IDOLookup.getHomeLegacy(
							ICObjectInstance.class)).findByPrimaryKeyLegacy(
						iObjectInstanceId);
				Cat.removeFrom(obj);
			}
			return true;
		}
		catch (SQLException ex) {
		}
		return false;
	}
	/**
	
	 *  removes all categories bound to specified instance
	
	 */
	public boolean removeInstanceCategories(int iObjectInstanceId) {
		try {
			ICObjectInstance obj = ICObjectBusiness.getInstance().getICObjectInstance(iObjectInstanceId);
			obj.removeFrom(ICCategory.class);
		}
		catch (Exception ex) {
		}
		return false;
	}
	public boolean deleteBlock(int instanceid) throws RemoteException {
		Collection L =null;
		try{
			L = getCategoryHome().findAllByObjectInstance(instanceid);
		}
		catch(FinderException ex)
		{throw new RemoteException(ex.getMessage());}
			if (L != null && !L.isEmpty()) {
			Iterator I = L.iterator();
			while (I.hasNext()) {
				ICCategory N = (ICCategory) I.next();
				try {
					removeCategory(N.getID(), instanceid);
				}
				catch (Exception sql) {
				}
			}
			return true;
		}
		else
			return false;
	}
	public void removeCategory(int iCategoryId) throws Exception {
		removeCategory(iCategoryId, CategoryFinder.getInstance().getObjectInstanceIdFromCategoryId(iCategoryId));
	}
	public void removeCategory(int iCategoryId, int iObjectInstanceId) throws RemoteException {
		try {
			ICCategory nc = (ICCategory) CategoryFinder.getInstance().getCategory(iCategoryId);
			if (iObjectInstanceId > 0) {
				ICObjectInstance obj =
					(
						(com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(
							ICObjectInstance.class)).findByPrimaryKeyLegacy(
						iObjectInstanceId);
				nc.removeFrom(obj);
			}
			ICCategory parent = (ICCategory) nc.getParentEntity();
			if (parent != null) {
				parent.removeChild(nc);
			}
			Collection transls = getCategoryTranslationHome().findAllByCategory(iCategoryId);
			if(!transls.isEmpty()){
				Iterator iter = transls.iterator();
				while(iter.hasNext()){
					((ICCategoryTranslation) iter.next()).remove();
				}
			}
			
			nc.remove();
		}
		catch (RemoveException e) {
			throw new RemoteException(e.getMessage());
		}
		catch (SQLException sql) {
			throw new RemoteException(sql.getMessage());
		}
		catch (FinderException fex) {
			throw new RemoteException(fex.getMessage());
		}
	}
	public void storeRelatedCategories(int iObjectInstanceId, int[] CategoryIds) {
		try {
			ICObjectInstance instance =
				((ICObjectInstanceHome) IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(
					iObjectInstanceId);
			//ICCategoryICObjectInstanceHome catObjInstHome = (ICCategoryICObjectInstanceHome) IDOLookup.getHome(ICCategoryICObjectInstance.class);
			ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHomeLegacy(ICCategory.class);
			Category category;
			//      int tree_order = 0;
			int treeOrder[] = new int[CategoryIds.length];
			for (int i = 0; i < CategoryIds.length; i++) {
				category = catHome.findByPrimaryKey(new Integer(CategoryIds[i]));
				treeOrder[i] = catHome.getOrderNumber(category, instance);
				//        tree_order = 1;//catObjInstHome.getOrderNumber(category, instance);
			}
			instance.removeFrom(ICCategory.class);
			//      com.idega.core.data.ICObjectInstanceBMPBean.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom((ICCategory) category);
			for (int i = 0; i < CategoryIds.length; i++) {
				category = catHome.findByPrimaryKey(new Integer(CategoryIds[i]));
				//com.idega.core.data.ICObjectInstanceBMPBean.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom(ICCategory.class, CategoryIds[i]);
				//com.idega.core.data.ICObjectInstanceBMPBean.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom((ICCategory) category);
				instance.addTo(ICCategory.class, CategoryIds[i]);
				catHome.setOrderNumber(category, instance, treeOrder[i]);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public ICCategory storeCategory(
		int iCategoryId,
		String sName,
		String sDesc,
		int iObjectInstanceId,
		String type,
		boolean allowMultible)
		throws RemoteException {
		return storeCategory(iCategoryId, sName, sDesc, 0, iObjectInstanceId, type, allowMultible);
	}
	public ICCategory storeCategory(
		int iCategoryId,
		String sName,
		String sDesc,
		int orderNumber,
		int iObjectInstanceId,
		String type,
		boolean allowMultible)
		throws RemoteException {
		try {
			
			ICCategory Cat = getCategoryHome().create();
			if (iCategoryId > 0)
				Cat = getCategoryHome().findByPrimaryKey(new Integer(iCategoryId));
			Cat.setName(sName);
			Cat.setDescription(sDesc);
			Cat.setType(type);
			if (orderNumber == 0) {
				return storeCategory(Cat, iObjectInstanceId, allowMultible);
			}
			else {
				return storeCategory(Cat, iObjectInstanceId, orderNumber, allowMultible);
			}
		}
		catch (CreateException crex) {
			throw new RemoteException(crex.getMessage());
		}
		catch (FinderException fex) {
			throw new RemoteException(fex.getMessage());
		}
	}
	public boolean updateCategory(int id, String name, String info,int localeID)throws RemoteException {
		return updateCategory(id, name, info, 0, -1,localeID);
	}
	public boolean updateCategory(int id, String name, String info, int orderNumber, int objectInstanceId,int localeID) throws RemoteException{
		try {
			ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHome(ICCategory.class);
			ICCategory cat = catHome.findByPrimaryKey(new Integer(id));
			if(localeID<0){
				cat.setName(name);
				cat.setDescription(info);
				cat.store();
			}
			else{
				storeCategoryTranslation(id,name,info,localeID);
			}
			if (objectInstanceId > 0) {
				ICObjectInstanceHome objInsHome = (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
				ICObjectInstance objIns = objInsHome.findByPrimaryKey(objectInstanceId);
				//ICCategoryICObjectInstanceHome catObjInsHome = ( ICCategoryICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICCategoryICObjectInstance.class);
				//catObjInsHome.setOrderNumber(cat, objIns, orderNumber);
				catHome.setOrderNumber(cat, objIns, orderNumber);
			}
			
			return true;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	public ICCategory storeCategory(int iCategoryId, String sName, String sDesc, int iObjectInstanceId, String type)
		throws RemoteException {
		return storeCategory(iCategoryId, sName, sDesc, iObjectInstanceId, type, false);
	}
	public ICCategory storeCategory(ICCategory Cat, int iObjectInstanceId, boolean allowMultible) {
		return storeCategory(Cat, iObjectInstanceId, 0, allowMultible);
	}
		
	public ICCategory storeCategory(ICCategory Cat, int iObjectInstanceId, int orderNumber, boolean allowMultible) {
		javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			t.begin();
			if (Cat.getID() > 0) {
				Cat.store();
			}
			else {
				Cat.setCreated(IWTimestamp.getTimestampRightNow());
				Cat.setValid(true);
				Cat.store();
			}
			// Binding category to instanceId
			if (iObjectInstanceId > 0) {
				
				ICObjectInstance objIns =
					(
						(ICObjectInstanceHome) IDOLookup.getHomeLegacy(
							ICObjectInstance.class)).findByPrimaryKeyLegacy(
						iObjectInstanceId);

				// Allows only one category per instanceId
				if (!allowMultible)
					objIns.removeFrom(
						(ICCategory) com.idega.core.category.data.ICCategoryBMPBean.getEntityInstance(ICCategory.class));
				Cat.addTo(objIns, "TREE_ORDER", String.valueOf(orderNumber));
			}
			
			t.commit();
			return Cat;
		}
		catch (Exception e) {
			try {
				t.rollback();
			}
			catch (javax.transaction.SystemException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		}
		return null;
	}
	public int createCategory(int iObjectInstanceId, String type) throws RemoteException {
		return storeCategory(
			-1,
			type + " Category - " + iObjectInstanceId,
			type + "Category - " + iObjectInstanceId,
			iObjectInstanceId,
			type,
			false)
			.getID();
	}
	public void storeCategoryToParent(int category, int parent) throws RemoteException {
		try {
			ICCategory cat = getCategoryHome().findByPrimaryKey(new Integer(category));
			ICCategory par = getCategoryHome().findByPrimaryKey(new Integer(parent));
			par.addChild(cat);
		}
		catch (FinderException fex) {
			throw new RemoteException(fex.getMessage());
		}
		catch (SQLException sql) {
			throw new RemoteException(sql.getMessage());
		}
	}
	
	public java.util.Map getInheritedMetaData(ICCategory category) {
		return getInheritedMetaData(null, category);
	}
	
	public java.util.Map getInheritedMetaData(java.util.Map table, ICCategory category) {
		if (table == null)
			table = new Hashtable();
			
		ICCategory parent = (ICCategory) category.getParentNode();
		if (parent != null) {
			Map attributes = parent.getMetaDataAttributes();
			if (attributes != null)
				table.putAll(attributes);
			return getInheritedMetaData(table, parent);
		}
			
		return table;
	}
	
	public java.util.Map getInheritedMetaDataTypes(ICCategory category) {
		return getInheritedMetaDataTypes(null, category);
	}
	
	public java.util.Map getInheritedMetaDataTypes(java.util.Map metadata, ICCategory category) {
		if (metadata == null)
			metadata = new Hashtable();
			
		ICCategory parent = (ICCategory) category.getParentNode();
		if (parent != null) {
			Map attributes = parent.getMetaDataTypes();
			if (attributes != null)
				metadata.putAll(attributes);
			return getInheritedMetaDataTypes(metadata, parent);
		}
			
		return metadata;
	}
	
	public void storeCategoryTranslation(int iCategoryId,String name, String info,int localeID)throws RemoteException{
		try{
			if(localeID>0){
				ICCategoryTranslation trans = getTranslation(iCategoryId,localeID);
				trans.setName(name);
				trans.setDescription(info);
				trans.setLocaleID(localeID);
				trans.setCategoryId(iCategoryId);
				trans.store();
			}
		}
		catch(RemoteException cex){
			throw new RemoteException(cex.getMessage());
		}
	}
	
	private ICCategoryTranslation getTranslation(int iCategoryId,int localeID)throws RemoteException{
		try{
			return getCategoryTranslationHome().findByCategoryAndLocale(iCategoryId,localeID);
		}
		catch(FinderException fex){
			
		}
		try {
			return getCategoryTranslationHome().create();
		}
		catch (CreateException e) {
			throw new RemoteException(e.getMessage());
		}
		
		
	}
	
	public ICCategoryHome getCategoryHome() throws RemoteException {
		return (ICCategoryHome) IDOLookup.getHome(ICCategory.class);
	}
	
	public ICCategoryTranslationHome getCategoryTranslationHome() throws RemoteException {
		return (ICCategoryTranslationHome) IDOLookup.getHome(ICCategoryTranslation.class);
	}
}
