package com.idega.core.category.business;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import com.idega.core.category.data.*;
import com.idega.core.category.data.ICCategory;
import com.idega.core.category.data.ICCategoryHome;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.util.IWTimestamp;

public class CategoryBusiness {
    
	private static String TREE_ORDER_COLUMN_NAME = "TREE_ORDER";
	
	private static CategoryBusiness categoryBusiness;
	
	public static CategoryBusiness getInstance() {
		if (categoryBusiness == null)
			categoryBusiness = new CategoryBusiness();
		return categoryBusiness;
	}
	
	public boolean disconnectBlock(int instanceid) {
		List L = CategoryFinder.getInstance().listOfCategoryForObjectInstanceId(instanceid);
		if (L != null) {
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
						(com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(
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
	
	public boolean deleteBlock(int instanceid) {
		List L = CategoryFinder.getInstance().listOfCategoryForObjectInstanceId(instanceid);
		if (L != null) {
			Iterator I = L.iterator();
			while (I.hasNext()) {
				ICCategory N = (ICCategory) I.next();
				try {
					deleteCategory(N.getID(), instanceid);
				}
				catch (Exception sql) {
				}
			}
			return true;
		}
		else
			return false;
	}
	
	public void deleteCategory(int iCategoryId) throws Exception {
		deleteCategory(iCategoryId, CategoryFinder.getInstance().getObjectInstanceIdFromCategoryId(iCategoryId));
	}
	
	public void deleteCategory(int iCategoryId, int iObjectInstanceId) throws RemoteException {
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
			nc.remove();
		}
		catch (RemoveException e) {
			throw new RemoteException(e.getMessage());
		}
		catch (SQLException sql) {
			throw new RemoteException(sql.getMessage());
		}
	}
	
	public void saveRelatedCategories(int iObjectInstanceId, int[] CategoryIds) {
		try {
			ICObjectInstance instance =
				((ICObjectInstanceHome) IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(iObjectInstanceId);
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
	
	public ICCategory saveCategory(int iCategoryId, String sName, String sDesc, int iObjectInstanceId, String type, boolean allowMultible)
		throws RemoteException {
		return saveCategory(iCategoryId, sName, sDesc, 0, iObjectInstanceId, type, allowMultible);
	}
	
	public ICCategory saveCategory(
		int iCategoryId,
		String sName,
		String sDesc,
		int orderNumber,
		int iObjectInstanceId,
		String type,
		boolean allowMultible)
		throws RemoteException {
		try {
			ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHomeLegacy(ICCategory.class);
			ICCategory Cat = catHome.create();
			if (iCategoryId > 0)
				Cat = CategoryFinder.getInstance().getCategory(iCategoryId);
			Cat.setName(sName);
			Cat.setDescription(sDesc);
			Cat.setType(type);
			if (orderNumber == 0) {
				return saveCategory(Cat, iObjectInstanceId, allowMultible);
			}
			else {
				return saveCategory(Cat, iObjectInstanceId, orderNumber, allowMultible);
			}
		}
		catch (CreateException crex) {
			throw new RemoteException(crex.getMessage());
		}
	}
	
	public boolean updateCategory(int id, String name, String info) {
		return updateCategory(id, name, info, 0, -1);
	}
	
	public boolean updateCategory(int id, String name, String info, int orderNumber, int objectInstanceId) {
		try {
			ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHome(ICCategory.class);
			ICCategory cat = catHome.findByPrimaryKey(new Integer(id));
			cat.setName(name);
			cat.setDescription(info);
			cat.update();
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
	
	public ICCategory saveCategory(int iCategoryId, String sName, String sDesc, int iObjectInstanceId, String type) throws RemoteException {
		return saveCategory(iCategoryId, sName, sDesc, iObjectInstanceId, type, false);
	}
	
	public ICCategory saveCategory(ICCategory Cat, int iObjectInstanceId, boolean allowMultible) {
		return saveCategory(Cat, iObjectInstanceId, 0, allowMultible);
	}
	
	public boolean createPossiblyMissingTreeOrderColumnInICCategoryICObjectInstanceMiddleTable(ICCategory cat) {
	    
	    try {
            ((GenericEntity)cat).insertStartData();
        } catch (Exception e) {
            
            e.printStackTrace();
            return false;
        }
	    
        return true;
	}
	
	public ICCategory saveCategory(ICCategory Cat, int iObjectInstanceId, int orderNumber, boolean allowMultible) {
		javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			t.begin();
			if (Cat.getID() > 0) {
				Cat.update();
			}
			else {
				Cat.setCreated(IWTimestamp.getTimestampRightNow());
				Cat.setValid(true);
				Cat.insert();
			}
			// Binding category to instanceId
			if (iObjectInstanceId > 0) {
				//ICCategoryICObjectInstanceHome catObjInstHome = ((com.idega.core.data.ICCategoryICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICCategoryICObjectInstance.class));
				ICObjectInstance objIns =
					(
						(com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(
							ICObjectInstance.class)).findByPrimaryKeyLegacy(
						iObjectInstanceId);
				//catObjInstHome.setOrderNumber(Cat, objIns, orderNumber);
				
				//added by Eiki, a fix for databases that do not have the TREE_ORDER COLUMN!
				//We must find a better way!
				createPossiblyMissingTreeOrderColumnInICCategoryICObjectInstanceMiddleTable(Cat);
				////
				
				
				
				// Allows only one category per instanceId
				if (!allowMultible)
					objIns.removeFrom((ICCategory) com.idega.core.category.data.ICCategoryBMPBean.getEntityInstance(ICCategory.class));
				Cat.addTo(objIns, TREE_ORDER_COLUMN_NAME, String.valueOf(orderNumber));
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
		return saveCategory(-1, type + " Category - " + iObjectInstanceId, type + "Category - " + iObjectInstanceId, iObjectInstanceId, type, false)
			.getID();
	}
	
	public ICCategory createCategory(int iObjectInstanceId, String type, String name, String description) throws RemoteException {
		return saveCategory(-1, name, description, iObjectInstanceId, type, false);
	}
	
	public void saveCategoryToParent(int category, int parent) throws RemoteException {
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
	
	public ICCategoryHome getCategoryHome() throws RemoteException {
		return (ICCategoryHome) IDOLookup.getHome(ICCategory.class);
	}
}
