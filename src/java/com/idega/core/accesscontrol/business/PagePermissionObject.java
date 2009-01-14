package com.idega.core.accesscontrol.business;

/**
 * @author eiki
 * 
 * A helper (flag) class to enable working with Page permissions since hasPermission always needs something to check against (the obj Object in all the methods).
 */
public class PagePermissionObject{
	private String pageKey;
	public PagePermissionObject(String pageKey) {
		this.pageKey=pageKey;
	}
	public String getPageKey(){
		return this.pageKey;
	}

}