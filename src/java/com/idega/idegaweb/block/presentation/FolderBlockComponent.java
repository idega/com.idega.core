/*
 * Created on 18.2.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.idegaweb.block.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.ui.Window;

/**
 * Title:		FolderBlock
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */


public class FolderBlockComponent extends FolderBlock {
	
	public static final String prmBlockObjectID = "fb_obj_id";
	public static final String prmBlockInstanceID = "fb_inst_id";
	private static List _parametersToMaintain;
	
	static {
		_parametersToMaintain = new ArrayList();
		_parametersToMaintain.add(prmBlockObjectID);
		_parametersToMaintain.add(prmBlockInstanceID);		
	}
	
	
	public void _main(IWContext iwc) throws Exception {
		maintainBlockObjectAndInstanceID(iwc);
		super._main(iwc);
	}
	
	
	public void quit(IWContext iwc){
		Page page = this.getParentPage();
		if(page instanceof Window){
			page.setParentToReload();
			page.close();
		} else {
			try {
				page.setToRedirect(getBuilderService(iwc).getPageURI(page.getPageID()));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public void maintainBlockObjectAndInstanceID(IWContext iwc){
		try {
			String instID =iwc.getParameter(FolderBlockComponent.prmBlockInstanceID);
			setBlockInstanceID(Integer.parseInt(instID));
			String objID = iwc.getParameter(FolderBlockComponent.prmBlockObjectID);
			setBlockObjectID(Integer.parseInt(objID));
		} catch (NumberFormatException e) {
			System.out.println("["+this.getClassName()+"]: this class depends on the parameters prmBlockObjectID and prmBlockInstanceID");
			e.printStackTrace();
		}
	}
	
	public List getListOfParametersToMaintain(){
		return _parametersToMaintain;
	}
	
}
