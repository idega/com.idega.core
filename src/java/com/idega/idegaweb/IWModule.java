/*
 * $Id: IWModule.java,v 1.1 2006/06/15 17:53:23 tryggvil Exp $
 * Created on 27.10.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb;


/**
 * <p>
 * This is a more general replacement for the IWBundle interface.
 * </p>
 *  Last modified: $Date: 2006/06/15 17:53:23 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public interface IWModule {
	
	public String getModuleName();
	public String getModuleVersion();	
	/**
	 * <p>
	 * Returns the identifier of this module (such as com.idega.core)
	 * </p>
	 * @return
	 */
	public String getModuleIdentifier();
	public String getModuleVendor();
	
	public boolean canLoadLazily();
	
	public void load();
	public void unload();
	public void reload();
	
}