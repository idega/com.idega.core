package com.idega.idegaweb.block.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.category.data.ICInformationCategory;
import com.idega.core.category.data.ICInformationCategoryHome;
import com.idega.core.category.data.ICInformationCategoryTranslation;
import com.idega.core.category.data.ICInformationCategoryTranslationHome;
import com.idega.core.category.data.ICInformationFolder;
import com.idega.core.category.data.ICInformationFolderHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.EntityFinder;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.util.IWTimestamp;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */
public class FolderBlockBusinessBean extends IBOServiceBean implements FolderBlockBusiness {

	public FolderBlockBusinessBean() {
	}

	public static FolderBlockBusinessBean getInstance(IWApplicationContext iwac) throws IBOLookupException {
		return (FolderBlockBusinessBean) IBOLookup.getServiceInstance(iwac,FolderBlockBusiness.class);
	}

	public ICInformationFolder getInstanceWorkeFolder(int icObjectInstanceId, int icObjectId, int localeId, boolean autocreate) {
		ICInformationFolder parentFolder = null;
		try {
			List l = EntityFinder.findRelated(((com.idega.core.component.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceId), com.idega.core.category.data.ICInformationFolderBMPBean.getStaticInstance(ICInformationFolder.class));
			if (l != null && l.size() > 0) {
				parentFolder = (ICInformationFolder)l.get(0);
			} else if (autocreate) {
				ICInformationFolder folder = createICInformationFolder("folder - " + icObjectInstanceId, null, null, icObjectId, -1, icObjectInstanceId);
				parentFolder = folder;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		if (parentFolder != null) {
			try {
				List l = EntityFinder.findAllByColumn(com.idega.core.category.data.ICInformationFolderBMPBean.getStaticInstance(ICInformationFolder.class), com.idega.core.category.data.ICInformationFolderBMPBean.getColumnParentFolderId(), parentFolder.getID(), com.idega.core.category.data.ICInformationFolderBMPBean.getColumnLocaleId(), localeId);
				if (l != null && l.size() > 0) {
					return (ICInformationFolder)l.get(0);
				} else { // autocreates locleFolders
					ICInformationFolder folder = createICInformationFolderForLocale("localefolder - " + icObjectInstanceId, icObjectId, parentFolder.getID(), localeId);
					return folder;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public List getInstanceViewFolders(int icObjectInstanceId) {
		throw new RuntimeException("getInstanceViewFolders(int icObjectInstanceId) not Implemented");
	}

	public List getInstanceCategories(int icObjectInstanceId) {
		try {
			List l = EntityFinder.findRelated(((com.idega.core.component.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceId), com.idega.core.category.data.ICInformationCategoryBMPBean.getStaticInstance(ICInformationCategory.class));
			return l;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public boolean hasAvailableCategory(int icObjectId) {
		try {
			return this.getCategoryHome().hasAvailableCategory(icObjectId);
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (IDOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public ICInformationFolder createICInformationFolder(String name, String description, String type, int ICObjectId, int ownerGroup, int relatedInstanceId) throws SQLException {
		return createICInformationFolder(name, description, type, ICObjectId, -1, -1, ownerGroup, relatedInstanceId);
	}

	public ICInformationFolder createICInformationFolderForLocale(String name, int ICObjectId, int parentId, int localeId) throws SQLException {
		return createICInformationFolder(name, null, null, ICObjectId, parentId, localeId, -1, -1);
	}

	private ICInformationFolder createICInformationFolder(String name, String description, String type, int ICObjectId, int parentId, int localeId, int ownerGroup, int relatedInstanceId) throws SQLException {
		ICInformationFolder folder = ((com.idega.core.category.data.ICInformationFolderHome)com.idega.data.IDOLookup.getHomeLegacy(ICInformationFolder.class)).createLegacy();
		if (name != null) {
			folder.setName(name);
		}
		if (description != null) {
			folder.setDescription(description);
		}
		if (ownerGroup != -1) {
			folder.setOwnerGroupID(ownerGroup);
		}
		if (type != null) {
			folder.setType(type);
		}
		if (parentId != -1) {
			folder.setParentId(parentId);
		}
		if (localeId != -1) {
			folder.setLocaleId(localeId);
		}
		folder.setICObjectId(ICObjectId);
		folder.setValid(true);
		folder.setCreated(IWTimestamp.getTimestampRightNow());
		folder.insert();
		if (relatedInstanceId != -1) {
			folder.addTo(ICObjectInstance.class, relatedInstanceId);
		}
		return folder;
	}

	public void addCategoryToInstance(ICInformationCategory cat, int instanceId) throws SQLException {
		cat.addCategoryToInstance(instanceId);
	}

	public void removeCategoryFromInstance(ICInformationCategory cat, int instanceId) throws SQLException {
		cat.removeCategoryFromInstance(instanceId);
	}

	public void addCategoryToInstance(ICObjectInstance inst, int catId) throws SQLException {
		inst.addTo(ICInformationCategory.class, catId);
	}

	public void removeCategoryFromInstance(ICObjectInstance inst, int catId) throws SQLException {
		inst.removeFrom(ICInformationCategory.class, catId);
	}

	public ICInformationCategory createICInformationCategory(IWContext iwc, int localeID, String name, String description, String type, int ICObjectId, int ownerFolder) {
		ICInformationCategory cat = null;
		try {
			int saveLocaleID = localeID;

			cat = this.getCategoryHome().create();
			if (name != null) {
				cat.setName(name);
			}
			if (description != null) {
				cat.setDescription(description);
			}
			if (ownerFolder != -1) {
				cat.setFolderSpecific(ownerFolder);
			}
			if (type != null) {
				cat.setType(type);
			}
			cat.setICObjectId(ICObjectId);
			cat.setValid(true);
			cat.setCreated(IWTimestamp.getTimestampRightNow());
			cat.store();
			
			
			// create translation record
			if (saveLocaleID == -1) {
				saveLocaleID = ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale());
			}
			createICInformationCategoryTranslation(saveLocaleID, name, description, localeID);

		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (IDOStoreException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}

		return cat;

	}


	public ICInformationCategoryTranslation createICInformationCategoryTranslation(int categoryID, String name, String description, int localeID){
		try {
			ICInformationCategoryTranslation cat = this.getCategoryTranslationHome().create();
			cat.setLocale(localeID);
			cat.setSuperInformationCategory(categoryID);
			cat.setName(name);
			if (description != null) {
				cat.setDescription(description);
			}
			cat.store();
			return cat;
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (IDOStoreException e) {
			e.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Collection getAvailableCategories(int icObjectId, int workingFolderId) {
		try {
			return this.getCategoryHome().findAvailableCategories(icObjectId, workingFolderId);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public Collection getAvailableTopNodeCategories(int icObjectId, int workingFolderId) {
		try {
			return this.getCategoryHome().findAvailableTopNodeCategories(icObjectId, workingFolderId);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public boolean copyCategoryAttachments(int instanceFrom, int instanceTo) {
		List infoCategories = this.getInstanceCategories(instanceFrom);
		boolean toReturn = true;
		if (infoCategories != null) {
			Iterator iter = infoCategories.iterator();
			while (iter.hasNext()) {
				ICInformationCategory item = (ICInformationCategory)iter.next();
				try {
					item.addCategoryToInstance(instanceTo);
				} catch (SQLException ex) {
					toReturn = false;
				}
			}
		}
		return toReturn;
	}

	public ICInformationCategoryHome getCategoryHome() throws IDOLookupException{
		return (ICInformationCategoryHome)IDOLookup.getHome(ICInformationCategory.class);
	}

	public ICInformationCategoryTranslationHome getCategoryTranslationHome() throws IDOLookupException {
		return (ICInformationCategoryTranslationHome)IDOLookup.getHome(ICInformationCategoryTranslation.class);
	}

	public ICInformationCategory getCategory(int iCategoryId) {
		if (iCategoryId > 0) {
			try {
				return ((ICInformationCategoryHome)IDOLookup.getHome(ICInformationCategory.class)).findByPrimaryKey(new Integer(iCategoryId));
			} catch (Exception sql) {
				sql.printStackTrace(System.err);
				return null;
			}
		}
		return null;
	}

	/**
	 *  //TODO IMPLEMENT - TEMPORARY IMPLEMENTATION
	 *  Returns a Collection of ICCategory-ids that have reference to a
	 *  ICObjectInstance
	 *
	 * @param  iObjectInstanceId  Description of the Parameter
	 * @return                    Description of the Return Value
	 */
	public Collection collectCategoryIntegerIds(int iObjectInstanceId) {
		try {
			Collection cats = getInstanceCategories(iObjectInstanceId);
			if (cats != null) {
				Iterator catsIter = cats.iterator();
				if (catsIter != null) {
					HashSet H = new HashSet();
					Integer I;
					while (catsIter.hasNext()) {
						I = (Integer) ((ICInformationCategory)catsIter.next()).getPrimaryKey();
						if (!H.contains(I)) {
							H.add(I);
						}
					}
					return H;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void storeCategoryToParent(int category, int parent) throws RemoteException {
		try {
			ICInformationCategory cat = getCategoryHome().findByPrimaryKey(new Integer(category));
			ICInformationCategory par = getCategoryHome().findByPrimaryKey(new Integer(parent));
			par.addChild(cat);
		} catch (FinderException fex) {
			throw new RemoteException(fex.getMessage());
		} catch (SQLException sql) {
			throw new RemoteException(sql.getMessage());
		}
	}

	public boolean updateCategory(IWContext iwc, int id, String name, String info, int localeID) throws RemoteException {
		try {
			int defaultLocaleID = ICLocaleBusiness.getLocaleId(iwc.getApplicationSettings().getDefaultLocale());

			ICInformationCategoryHome catHome = this.getCategoryHome();
			ICInformationCategory cat = catHome.findByPrimaryKey(new Integer(id));

			if (defaultLocaleID == localeID || localeID < 0) {
				cat.setName(name);
				cat.setDescription(info);
				cat.store();
			}

			if (!(localeID < 0)) {
				ICInformationCategoryTranslationHome catTranslHome = this.getCategoryTranslationHome();
				ICInformationCategoryTranslation catTransl = null;
				try {
					catTransl = catTranslHome.findByCategoryAndLocale(id, localeID);
					catTransl.setName(name);
					catTransl.setDescription(info);
					catTransl.store();
				} catch (FinderException e1) {
					catTransl = createICInformationCategoryTranslation(id, name, info, localeID);
				}
			}

			return true;
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (IDOStoreException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	
	public boolean removeCategory(IWContext iwc, int categoryID){
		boolean successful = false;
		try {
			ICInformationCategoryHome catHome = this.getCategoryHome();
			ICInformationCategory cat = catHome.findByPrimaryKey(new Integer(categoryID));
			
			int usrID = iwc.getUserId();
			cat.setDeleted(usrID,true);	
			cat.store();
			successful = true;
			
		} catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return successful;
	}

	public void storeInstanceCategories(int iObjectInstanceId, int[] CategoryIds) {
		try {
			ICObjectInstance instance = ((ICObjectInstanceHome)IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(iObjectInstanceId);

			instance.removeFrom(ICInformationCategory.class);
			for (int i = 0; i < CategoryIds.length; i++) {
				instance.addTo(ICInformationCategory.class, CategoryIds[i]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean detachWorkfolderFromObjectInstance(ICObjectInstance instance){
		boolean toReturn = true;
		
		try {
			((ICInformationCategoryHome)IDOLookup.getHome(ICInformationCategory.class)).removeObjectInstanceRelation(instance);
			((ICInformationFolderHome)IDOLookup.getHome(ICInformationFolder.class)).removeObjectInstanceRelation(instance);
		} catch (IDORemoveRelationshipException e) {
			e.printStackTrace();
			toReturn = false;
		} catch (IDOLookupException e) {
			e.printStackTrace();
			toReturn = false;
		}
		
		
		return toReturn;
	}

}