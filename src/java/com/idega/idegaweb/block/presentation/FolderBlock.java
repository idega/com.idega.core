package com.idega.idegaweb.block.presentation;

import java.util.Iterator;
import java.util.List;

import com.idega.core.business.InformationCategory;
import com.idega.core.business.InformationFolder;
import com.idega.core.data.ICLocale;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.idegaweb.block.business.FolderBlockBusiness;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;

/**
 *  <p>
 *  Title: idegaWeb</p> <p>
 *  Description: </p> <p>
 *  Copyright: Copyright (c) 2001</p> <p>
 *  Company: idega</p>
 *
 *@author     <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 *@created    15. mars 2002
 *@version    1.0
 */
public class FolderBlock extends Block {

	private InformationFolder _workingFolder = null;
	private InformationFolder[] _viewFolders = null;
	private InformationCategory[] _categoriesForInstance = null;
	private boolean _autocreate = true;
	private String _contentLocaleIdentifier = null;

	/**
	 *  Constructor for the FolderBlock object
	 */
	public FolderBlock() {
	}

	/**
	 *  Gets the workingFolderId attribute of the FolderBlock object
	 *
	 *@return    The workingFolderId value
	 */
	public int getWorkingFolderId() {
		return _workingFolder.getID();
	}

	/**
	 *  Gets the workingFolder attribute of the FolderBlock object
	 *
	 *@return    The workingFolder value
	 */
	public InformationFolder getWorkingFolder() {
		return _workingFolder;
	}

	/*
	 *  public InformationFolder[] getFoldersToView(){
	 *  return _viewFolders;
	 *  }
	 */

	/**
	 *  Gets the categoriesToView attribute of the FolderBlock object
	 *
	 *@return    The categoriesToView value
	 */
	public InformationCategory[] getCategoriesToView() {
		return _categoriesForInstance;
	}

	/**
	 *  Sets the folder attribute of the FolderBlock object
	 *
	 *@param  folder  The new folder value
	 */
	public void setFolder(InformationFolder folder) {
		_workingFolder = folder;
	}

	/**
	 *  Sets the autoCreate attribute of the FolderBlock object
	 *
	 *@param  autocreate  The new autoCreate value
	 */
	public void setAutoCreate(boolean autocreate) {
		_autocreate = autocreate;
	}

	/**
	 *  Sets the contentLocaleIdentifier attribute of the FolderBlock object
	 *
	 *@param  identifier  The new contentLocaleIdentifier value
	 */
	public void setContentLocaleIdentifier(String identifier) {
		_contentLocaleIdentifier = identifier;
	}

	/**
	 *  Gets the contentLocaleIdentifier attribute of the FolderBlock object
	 *
	 *@return    The contentLocaleIdentifier value
	 */
	public String getContentLocaleIdentifier() {
		return _contentLocaleIdentifier;
	}

	/**
	 *  Sets the contentLocaleDependent attribute of the FolderBlock object
	 */
	public void setContentLocaleDependent() {
		_contentLocaleIdentifier = null;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  iwc            Description of the Parameter
	 *@exception  Exception  Description of the Exception
	 */
	public void _main(IWContext iwc) throws Exception {
		if (getICObjectInstanceID() > 0) {
			FolderBlockBusiness business = FolderBlockBusiness.getInstance();
			int localeId = -1;
			if (getContentLocaleIdentifier() != null) {
				ICLocale locale = ICLocaleBusiness.getICLocale(getContentLocaleIdentifier());
				//getContentLocaleIdentifier();
				if (locale != null) {
					localeId = locale.getID();
				}
			}
			if (localeId == -1) {
				localeId = iwc.getCurrentLocaleId();
			}
			InformationFolder folder = business.getInstanceWorkeFolder(getICObjectInstanceID(), getICObjectID(), localeId, _autocreate);
			if (folder != null) {
				setFolder(folder);
			}
			List infoCategories = business.getInstanceCategories(getICObjectInstanceID());
			if (infoCategories != null && infoCategories.size() > 0) {
				_categoriesForInstance = new InformationCategory[infoCategories.size()];
				int pos = 0;
				Iterator iter = infoCategories.iterator();
				while (iter.hasNext()) {
					InformationCategory item = (InformationCategory)iter.next();
					_categoriesForInstance[pos] = item;
					pos++;
				}
			} else {
				_categoriesForInstance = new InformationCategory[0];
			}
		}
		super._main(iwc);
	}

	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public synchronized Object clone() {
		FolderBlock obj = null;
		try {
			obj = (FolderBlock)super.clone();
			obj._workingFolder = this._workingFolder;
			obj._viewFolders = this._viewFolders;
			obj._categoriesForInstance = this._categoriesForInstance;
			obj._autocreate = this._autocreate;
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return obj;
	}

	public boolean copyBlock(int newInstanceID) {
		return FolderBlockBusiness.getInstance().copyCategoryAttachments(this.getICObjectInstanceID(), newInstanceID);
	}


	/**
	 *  Returns a Link to FolderBlockCategoryWindow
	 */
	public Link getCategoryLink(){
		Link L = new Link();
		//L.addParameter(FolderBlockCategoryWindow.prmCategoryId,getCategoryId());
		L.addParameter(FolderBlockCategoryWindow.prmObjInstId,getICObjectInstanceID());
		L.addParameter(FolderBlockCategoryWindow.prmObjId,getICObjectID());
		L.addParameter(FolderBlockCategoryWindow.prmWorkingFolder,this.getWorkingFolderId());
//		if(getMultible()) {
			L.addParameter(FolderBlockCategoryWindow.prmMulti,"true");
//		}
//		if (orderManually) {
//			L.addParameter(CategoryWindow.prmOrder, "true");
//		}

		L.setWindowToOpen(FolderBlockCategoryWindow.class);
		return L;
	}


}