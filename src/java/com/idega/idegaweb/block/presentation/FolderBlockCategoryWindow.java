package com.idega.idegaweb.block.presentation;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.core.category.data.ICInformationCategory;
import com.idega.core.category.data.ICInformationCategoryTranslation;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.presentation.ICLocalePresentation;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.block.business.FolderBlockBusiness;
import com.idega.idegaweb.block.business.FolderBlockBusinessBean;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
	*@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */
public class FolderBlockCategoryWindow extends IWAdminWindow {
	private int iCategoryId = -1;
	protected int iObjectInstanceId = -1;
	protected int iObjectId = -1;
	protected int iWorkingFolder = -1;
	protected ICObjectInstance objectInstance;
	protected String sType = "no_type";
	protected String sCacheKey = null;
	protected boolean multi = false;
	protected boolean allowOrdering = false;
	public static final String prmCategoryId = "icinfcat_categoryid";
	public static final String prmObjInstId = "icinfcat_obinstid";
	public static final String prmObjId = "icinfcat_objid";
	public static final String prmWorkingFolder = "icinfcat_workf";
	public final static String prmCategoryType = "icinfcat_type";
	public final static String prmMulti = "icinfcat_multi";
	public final static String prmOrder = "icinfcat_order";
	public static final String prmCacheClearKey = "icinfcat_cache_clear";
	public static final String prmParentID = "icinfcat_parent";
	public final static String prmLocale = "icinfcat_localedrp";
	protected static final String actDelete = "icinfcat_del";
	protected static final String actSave = "icinfcat_save";
	protected static final String actClose = "icinfcat_close";
	protected static final String actForm = "icinfcat_form";
	protected Image tree_image_M, tree_image_L, tree_image_T;
	protected IWResourceBundle iwrb;
	protected IWBundle iwb, core;
	private int iObjInsId = -1;
	private int iUserId = -1;
	protected boolean formAdded = false;
	protected int row = 1;
	protected FolderBlockBusiness _folderblockBusiness = null;
	protected int iLocaleId = -1;

	public FolderBlockCategoryWindow() {
		setWidth(700);
		setHeight(400);
		setResizable(true);
		setUnMerged();
		setScrollbar(true);
	}

//	protected void clearCache(IWContext iwc) {
//		if (getCacheKey(iwc) != null) {
//			if (iwc.getApplication().getIWCacheManager().isCacheValid(getCacheKey(iwc))) {
//				iwc.getApplication().getIWCacheManager().invalidateCache(getCacheKey(iwc));
//			}
//		}
//	}
//	protected String getCacheKey(IWContext iwc) {
//		if (sCacheKey == null) {
//			sCacheKey = iwc.getParameter(prmCacheClearKey);
//		}
//		return sCacheKey;
//	}
//	protected void maintainClearCacheKeyInForm(IWContext iwc) {
//		if (getCacheKey(iwc) != null) {
//			this.addHiddenInput(new HiddenInput(prmCacheClearKey, getCacheKey(iwc)));
//		} else {
//		}
//	}
	protected void control(IWContext iwc) throws Exception {

		if (iwc.isParameterSet(prmLocale)) {
			iLocaleId = Integer.parseInt(iwc.getParameter(prmLocale));
		} else {
			iLocaleId = ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale());
		}

		Table T = new Table();
		T.setCellpadding(0);
		T.setCellspacing(0);
		if (iCategoryId <= 0 && iwc.isParameterSet(prmCategoryId)) {
			iCategoryId = Integer.parseInt(iwc.getParameter(prmCategoryId));
		}
		if (iObjectInstanceId <= 0 && iwc.isParameterSet(prmObjInstId)) {
			iObjectInstanceId = Integer.parseInt(iwc.getParameter(prmObjInstId));
			objectInstance = ((ICObjectInstanceHome)IDOLookup.getHome(ICObjectInstance.class)).findByPrimaryKey(iObjectInstanceId);
		}
		if (iObjectId <= 0 && iwc.isParameterSet(prmObjId)) {
			iObjectId = Integer.parseInt(iwc.getParameter(prmObjId));
		}

		if (iWorkingFolder <= 0 && iwc.isParameterSet(prmWorkingFolder)) {
			iWorkingFolder = Integer.parseInt(iwc.getParameter(prmWorkingFolder));
		}

		if (iwc.isParameterSet(prmCategoryType)) {
			sType = iwc.getParameter(prmCategoryType);
		}
//		clearCache(iwc);
		multi = iwc.isParameterSet(prmMulti);
		allowOrdering = iwc.isParameterSet(prmOrder);
		/**
		 * @todo We need some authication here ,
		 *  permissions from underlying window ???
		 */
		if (true) {
			if (iwc.isParameterSet(actForm)) {
				processCategoryForm(iwc);
			}
			//addCategoryFields(CategoryFinder.getCategory(iCategoryId));
			getCategoryFields(iwc, iCategoryId);
		} else {
			add(formatText(iwrb.getLocalizedString("access_denied", "Access denied")));
		}
	}
	protected void processCategoryForm(IWContext iwc) throws RemoteException {
		// saving :
		if (iwc.isParameterSet(actSave) || iwc.isParameterSet(actSave + ".x")) {
			String sName = iwc.getParameter("name");
			String sDesc = iwc.getParameter("info");
			String sOrder = iwc.getParameter("order");
			int parent = iwc.isParameterSet(prmParentID) ? Integer.parseInt(iwc.getParameter(prmParentID)) : -1;
			if (sOrder == null || sOrder.equals("")) {
				sOrder = "0";
			}
			String sType = iwc.getParameter(prmCategoryType);
			if (sName != null && sType != null) {
				if (iCategoryId <= 0 && sName.length() > 0) {
					try {
						ICInformationCategory newInfoCat = _folderblockBusiness.createICInformationCategory(iwc, iLocaleId, sName, sDesc, sType, iObjectId, -1);
						iCategoryId = newInfoCat.getID();
						_folderblockBusiness.createICInformationCategoryTranslation(iCategoryId, sName, sDesc, iLocaleId);

						if (parent > 0 && iCategoryId > 0) {
							_folderblockBusiness.storeCategoryToParent(iCategoryId, parent);
						}
						postSave(iwc, iCategoryId);
					} catch (java.rmi.RemoteException ex) {
						ex.printStackTrace();
					}
				} else {
					String[] sids = iwc.getParameterValues("id_box");
					int[] savedids = new int[0];
					if (sids != null)
						savedids = new int[sids.length];
					for (int i = 0; i < savedids.length; i++) {
						savedids[i] = Integer.parseInt(sids[i]);
						//  System.err.println("save id "+savedids[i]);
					}

					if (iCategoryId > 0) {
						_folderblockBusiness.updateCategory(iwc, iCategoryId, sName, sDesc, iLocaleId);
					}

					_folderblockBusiness.storeInstanceCategories(iObjectInstanceId, savedids);
					postSave(iwc, iCategoryId);
				}
			}
		}
		if (iwc.isParameterSet(actClose) || iwc.isParameterSet(actClose + ".x")) {
			setParentToReload();
			close();
		}
		// deleting :
		else if (iwc.isParameterSet(actDelete) || iwc.isParameterSet(actDelete + ".x")) {
			try {
				_folderblockBusiness.removeCategory(iwc, iCategoryId);
				System.out.println(this.getClass().getName() + ": should delete category");
				iCategoryId = -1;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	protected void postSave(IWContext iwc, int iCategoryId) throws RemoteException {

	}

	protected void getCategoryFields(IWContext iwc, int iCategoryId) throws RemoteException {
		int parent = iwc.isParameterSet(prmParentID) ? Integer.parseInt(iwc.getParameter(prmParentID)) : -1;

		int objID = iwc.isParameterSet(prmObjId) ? Integer.parseInt(iwc.getParameter(prmObjId)) : -1;
		int workFolderID = iwc.isParameterSet(prmWorkingFolder) ? Integer.parseInt(iwc.getParameter(prmWorkingFolder)) : -1;

		Link newLink = new Link(core.getImage("/shared/create.gif"));
		newLink.addParameter(prmCategoryId, -1);
		newLink.addParameter(prmObjInstId, iObjectInstanceId);
		newLink.addParameter(FolderBlockCategoryWindow.prmObjId, iObjectId);
		newLink.addParameter(FolderBlockCategoryWindow.prmWorkingFolder, iWorkingFolder);

		newLink.addParameter(actForm, "true");

		Collection L = null;
		try {
			/** @todo  permission handling */
			L = _folderblockBusiness.getAvailableTopNodeCategories(objID, workFolderID);
		} catch (Exception ex) {

		}
		if (L != null) { // Gimmi 17.08.2002
			/** @todo laga comparatorinn */
			//Collections.sort(L, new CategoryComparator());
		}
		Collection coll = _folderblockBusiness.collectCategoryIntegerIds(iObjectInstanceId);
		int chosenId = iCategoryId;

		Table T = new Table();
		T.setCellpadding(0);
		T.setCellspacing(0);
		row = 1;
		DropdownMenu LocaleDrop = ICLocalePresentation.getLocaleDropdownIdKeyed(prmLocale);
		LocaleDrop.setToSubmit();
		LocaleDrop.setSelectedElement(Integer.toString(iLocaleId));
		T.add(LocaleDrop, 1, row);
		T.mergeCells(1, row, 3, row);
		row++;
		T.add(Text.getBreak(), 1, row);
		T.add(formatText(iwrb.getLocalizedString("use", "Use")), 1, row);
		T.add(formatText(iwrb.getLocalizedString("name", "Name")), 2, row);
		T.add(formatText(iwrb.getLocalizedString("info", "Info")), 3, row);
		if (allowOrdering) {
			T.add(formatText("  " + iwrb.getLocalizedString("order", "Order")), 4, row);
		}
		T.add(formatText("  " + iwrb.getLocalizedString("add_child", "Add child") + "  "), 5, row);
		T.add(formatText("  " + iwrb.getLocalizedString("delete", "Delete") + "  "), 6, row);
		row++;
		TextInput name = new TextInput("name");
		TextInput info = new TextInput("info");
		TextInput order = new TextInput("order");
		order.setSize(3);
		setStyle(name);
		setStyle(info);
		setStyle(order);
		formAdded = false;
		if (L != null)
			fillTable(L.iterator(), T, chosenId, coll, name, info, order, 0);
		if (!formAdded) {
			T.add(Text.getBreak(), 1, row++);
			T.mergeCells(2, row, 6, row);
			if (parent > 0) {
				ICInformationCategory cat = _folderblockBusiness.getCategory(parent);
				T.add(formatText(iwrb.getLocalizedString("create_child_category_under", "Create child under") + " " + cat.getName()), 2, row);
				;

			} else {
				T.add(formatText(iwrb.getLocalizedString("create_root_category", "Create new root category")), 2, row);
			}
			row++;
			T.add(name, 2, row);
			T.add(info, 3, row);
		} else {
			Link li = new Link(iwrb.getLocalizedImageButton("new", "New"));
			addParametersToLink(li);
			T.add(Text.getBreak(), 2, row);
			T.add(li, 2, row);
		}
		addLeft(iwrb.getLocalizedString("categories", "Categories"), T, true, false);
		addBreak();

		SubmitButton save = new SubmitButton(iwrb.getLocalizedImageButton("save", "Save"), actSave);
		SubmitButton close = new SubmitButton(iwrb.getLocalizedImageButton("close", "Close"), actClose);
		addSubmitButton(save);
		addSubmitButton(close);
		addHiddenInput(new HiddenInput(prmCategoryType, sType));
		addHiddenInput(new HiddenInput(prmObjInstId, String.valueOf(iObjectInstanceId)));
		addHiddenInput(new HiddenInput(prmParentID, String.valueOf(parent)));
		addHiddenInput(new HiddenInput(FolderBlockCategoryWindow.prmObjId, String.valueOf(iObjectId)));
		addHiddenInput(new HiddenInput(FolderBlockCategoryWindow.prmWorkingFolder, String.valueOf(iWorkingFolder)));

		addHiddenInput(new HiddenInput(actForm, "true"));
		if (allowOrdering) {
			addHiddenInput(new HiddenInput(prmOrder, "true"));
		}
		if (multi) {
			addHiddenInput(new HiddenInput(prmMulti, "true"));
		}
//		this.maintainClearCacheKeyInForm(iwc);

		T.setColumnAlignment(4, T.HORIZONTAL_ALIGN_CENTER);
		T.setAlignment(4, 1, T.HORIZONTAL_ALIGN_LEFT);
		T.setColumnAlignment(5, T.HORIZONTAL_ALIGN_CENTER);
		T.setAlignment(5, 1, T.HORIZONTAL_ALIGN_LEFT);
		T.setColumnAlignment(6, T.HORIZONTAL_ALIGN_CENTER);
		T.setAlignment(6, 1, T.HORIZONTAL_ALIGN_LEFT);

	}

	protected void fillTable(Iterator iter, Table T, int chosenId, Collection coll, TextInput name, TextInput info, TextInput order, int level) throws RemoteException {
		if (iter != null) {

			ICInformationCategory cat;
			ICInformationCategoryTranslation trans = null;
			String catName, catInfo;
			CheckBox box;
			RadioButton rad;
			Link deleteLink;
			int id;
			int iOrder = 0;
			while (iter.hasNext()) {
				cat = (ICInformationCategory)iter.next();
				id = ((Integer)cat.getPrimaryKey()).intValue();
				try {
					trans = _folderblockBusiness.getCategoryTranslationHome().findByCategoryAndLocale(id, iLocaleId);
					catName = trans.getName();
					catInfo = trans.getDescription();
				} catch (FinderException ex) {
					catName = cat.getName();
					catInfo = cat.getDescription();
				}

				if (allowOrdering) {
					try {
						//TEMP iOrder = CategoryFinder.getInstance().getCategoryOrderNumber(cat, this.objectInstance);
						iOrder = 0;
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
				if (level > 0) {
					for (int i = 0; i < level; i++) {
						T.add(tree_image_T, 2, row);
					}
					if (iter.hasNext())
						T.add(tree_image_M, 2, row);
					else
						T.add(tree_image_L, 2, row);
				}
				if (id == chosenId) {

					name.setContent(catName);
					if (catInfo != null)
						info.setContent(catInfo);
					T.add(name, 2, row);
					T.add(info, 3, row);
					if (allowOrdering) {
						T.add(order, 4, row);
						order.setContent(Integer.toString(iOrder));
					}
					T.add(new HiddenInput(prmCategoryId, String.valueOf(id)));
					formAdded = true;
				} else {
					Link Li = new Link(formatText(catName));
					Li.addParameter(prmCategoryId, id);
					Li.addParameter("edit", "true");
					T.add(Li, 2, row);
					T.add(formatText(catInfo), 3, row);
					Link childLink = new Link(core.getImage("/shared/create.gif"));
					childLink.addParameter(prmParentID, id);
					deleteLink = new Link(core.getImage("/shared/delete.gif"));
					deleteLink.addParameter(actDelete, "true");
					deleteLink.addParameter(prmCategoryId, id);
					deleteLink.addParameter(actForm, "true");
					addParametersToLink(childLink);
					addParametersToLink(deleteLink);
					addParametersToLink(Li);
					if (allowOrdering) {
						T.add(formatText(Integer.toString(iOrder)), 4, row);
					}
					T.add(childLink, 5, row);
					T.add(deleteLink, 6, row);

				}
				if (multi) {
					box = new CheckBox("id_box", String.valueOf(cat.getID()));
					box.setChecked(coll != null && coll.contains(new Integer(cat.getID())));
					//setStyle(box);
					T.add(box, 1, row);
				} else {
					rad = new RadioButton("id_box", String.valueOf(cat.getID()));
					if (coll != null && coll.contains(new Integer(cat.getID())))
						rad.setSelected();
					//setStyle(rad);
					T.add(rad, 1, row);
				}
				row++;
				if (cat.getChildCount() > 0)
					fillTable(cat.getChildren(), T, chosenId, coll, name, info, order, level + 1);
			}
			trans = null;
		}
	}

	protected void addParametersToLink(Link L) {
		if (this.sCacheKey != null)
			L.addParameter(this.prmCacheClearKey, this.sCacheKey);
		if (allowOrdering)
			L.addParameter(prmOrder, "true");
		if (multi)
			L.addParameter(prmMulti, "true");
		L.addParameter(prmCategoryType, sType);
		L.addParameter(prmObjInstId, String.valueOf(iObjectInstanceId));
		L.addParameter(prmLocale, String.valueOf(iLocaleId));
		L.addParameter(FolderBlockCategoryWindow.prmObjId, iObjectId);
		L.addParameter(FolderBlockCategoryWindow.prmWorkingFolder, iWorkingFolder);

	}
	/**
	 * @deprecated
	 */
	public static Link getWindowLink(int iCategoryId, int iInstanceId, String type, boolean multible) {
		return getWindowLink(iCategoryId, iInstanceId, type, multible, false);
	}

	public static Link getWindowLink(int iCategoryId, int iInstanceId, String type, boolean multible, boolean allowOrdering) {
		return getWindowLink(iCategoryId, iInstanceId, type, multible, allowOrdering, null);
	}
	public static Link getWindowLink(int iCategoryId, int iInstanceId, String type, boolean multible, boolean allowOrdering, String cacheKey) {
		Link L = new Link();
		L.addParameter(FolderBlockCategoryWindow.prmCategoryId, iCategoryId);
		L.addParameter(FolderBlockCategoryWindow.prmObjInstId, iInstanceId);
		L.addParameter(FolderBlockCategoryWindow.prmCategoryType, type);
		if (multible) {
			L.addParameter(FolderBlockCategoryWindow.prmMulti, "true");
		}
		if (allowOrdering) {
			L.addParameter(FolderBlockCategoryWindow.prmOrder, "true");
		}
		if (cacheKey != null) {
			L.addParameter(prmCacheClearKey, cacheKey);
		}
		L.setWindowToOpen(FolderBlockCategoryWindow.class);
		return L;
	}
	public PresentationObject getNameInput(ICInformationCategory node) {
		TextInput name = new TextInput("name");
		if (node != null) {
			name.setContent(node.getName());
		}
		return name;
	}
	public void main(IWContext iwc) throws Exception {
		iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);
		core = iwc.getApplication().getCoreBundle();
		_folderblockBusiness = (FolderBlockBusiness)IBOLookup.getServiceInstance(iwc, FolderBlockBusiness.class);
		String title = iwrb.getLocalizedString("ic_category_editor", "Category Editor");
		tree_image_M = core.getImage("/treeviewer/ui/win/treeviewer_M_line.gif");
		tree_image_L = core.getImage("/treeviewer/ui/win/treeviewer_L_line.gif");
		tree_image_T = core.getImage("treeviewer/ui/win/treeviewer_trancparent.gif");
		setTitle(title);
		addTitle(title);
		control(iwc);
	}
}
