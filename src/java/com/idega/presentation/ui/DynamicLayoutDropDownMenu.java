/*
 * $Id: DynamicLayoutDropDownMenu.java,v 1.2 2007/01/26 06:36:36 idegaweb Exp $
 * Created on 26.1.2007
 *
 * Copyright (C) 2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.CoreConstants;

/**
 * <p>
 * DropDownMenu so it users can have a range of JasperReportLayouts to select from.
 * </p>
 *  Last modified: $Date: 2007/01/26 06:36:36 $ by $Author: idegaweb $
 *
 * @author <a href="mailto:sigtryggur@idega.com">sigtryggur</a>
 * @version $Revision: 1.2 $
 */
public class DynamicLayoutDropDownMenu extends DropDownMenuInputHandler {


	protected static String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	protected static final String LAYOUT_FOLDER_NAME = "layout";
	private ICFileHome fileHome;

	public DynamicLayoutDropDownMenu() {
		super();
	}

    @Override
	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    	IWResourceBundle resourceBundle = getResourceBundle(iwc);
    	this.setName(name);
    	String defaultLayout = resourceBundle.getLocalizedString("DynamicLayoutDropDownMenu.default_layout","Default layout");
		this.addMenuElement("-1", defaultLayout);
		ICFile layoutFolder = getLayoutFolder(value);
		for (AdvancedProperty layout: getLayouts(layoutFolder)) {
			this.addMenuElement(layout.getId(), layout.getValue());
		}
		return this;
    }

    public List<AdvancedProperty> getLayouts(ICFile layoutFolder) {
    	if (layoutFolder == null) {
    		return Collections.emptyList();
    	}

    	List<AdvancedProperty> layouts = new ArrayList<AdvancedProperty>();
    	Iterator<ICFile> iterator = layoutFolder.getChildrenIterator();
		if (iterator != null) {
			while (iterator.hasNext())	{
				ICFile file = iterator.next();
				if (!file.isFolder()) {
					String display = file.getName();
					if (display != null && display.lastIndexOf(CoreConstants.DOT) != -1 ) {
						display = display.substring(0, display.lastIndexOf(CoreConstants.DOT));
					}
					int fileID = Integer.valueOf(file.getId());
					String fileIDValue = Integer.toString(fileID);
					layouts.add(new AdvancedProperty(fileIDValue, display));
				}
			}
		}
		return layouts;
    }

	@Override
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		if (value != null) {
			if (value.equals("-1")) {
				IWResourceBundle resourceBundle = getResourceBundle(iwc);
				return resourceBundle.getLocalizedString("DynamicLayoutDropDownMenu.default_layout","Default layout");
			}
			try {
	    		ICFile file = getFileHome().findByPrimaryKey(value);
	    		return file.getName();
	    	}
	    	catch (FinderException e) {
	    		return value.toString();
	    	}
		} else {
			return CoreConstants.EMPTY;
		}
	}

	public ICFile getLayoutFolder(String userDefinedLayoutFolderName)  {
		ICFile layoutFolder = null;
		try{
			try {
	    		layoutFolder = getFileHome().findByFileName(LAYOUT_FOLDER_NAME);
			}
			catch (FinderException findEx) {
	    		layoutFolder = createAndReturnLayoutFolder(LAYOUT_FOLDER_NAME);
	    		try {
	    			ICFile rootFolder = getFileHome().findRootFolder();
	    			rootFolder.addChild(layoutFolder);
	    		}
	    		catch (FinderException findEx2) {
	    			logError("[DynamicLayoutDropDownMenu] Could not find Root-folder");
	    		}
	    	}
			if (userDefinedLayoutFolderName != null && !userDefinedLayoutFolderName.equals(CoreConstants.EMPTY)) {
				ICFile userDefinedLayoutFolder = null;
				try {
					userDefinedLayoutFolder = getFileHome().findByFileName(userDefinedLayoutFolderName);
				}
				catch (FinderException findEx) {
					userDefinedLayoutFolder = createAndReturnLayoutFolder(userDefinedLayoutFolderName);
					layoutFolder.addChild(userDefinedLayoutFolder);
		    	}
				return userDefinedLayoutFolder;
			}
			return layoutFolder;
		}
		catch (CreateException createEx) {
			logError("[DynamicLayoutDropDownMenu] Could create file");
			log(createEx);
		}
		catch (SQLException sqlEx) {
			logError("[DynamicLayoutDropDownMenu] Could create child file");
			log(sqlEx);
		}
    	return null;
	}

	private ICFile createAndReturnLayoutFolder(String name) throws CreateException {
		ICFile layoutFolder = getFileHome().create();
		layoutFolder.setName(name);
		layoutFolder.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		layoutFolder.store();
		return layoutFolder;
	}

	private ICFileHome getFileHome(){
	    if (fileHome==null) {
	    	try{
	    		fileHome = (ICFileHome)IDOLookup.getHome(ICFile.class);
	    	}
		    catch (IDOLookupException lookupEx) {
		    	logError("[DynamicLayoutDropDownMenu] Could not look up home of ICFile");
		    	log(lookupEx);
		    }
	    }
	    return fileHome;
	  }

	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}