/*
 * $Id: KeyboardShortcut.java,v 1.2.2.1 2007/01/12 19:32:12 idegaweb Exp $
 * Created on 13.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:12 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2.2.1 $
 */
public class KeyboardShortcut {
	private String actionKey;

	
	public KeyboardShortcut(){}
	
	/**
	 * @param actionKey is a single key modifier
	 */
	public KeyboardShortcut(String actionKey){
		this.actionKey = actionKey;
	}
	/**
	 * @return Returns the actionKey.
	 */
	public String getActionKey() {
		return this.actionKey;
	}

	
	/**
	 * @param actionKey The actionKey to set.
	 */
	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}
	
}
