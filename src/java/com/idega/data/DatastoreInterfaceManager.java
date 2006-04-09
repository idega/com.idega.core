/*
 * $Id: DatastoreInterfaceManager.java,v 1.2 2006/04/09 12:13:15 laddi Exp $
 * Created on 6.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data;

import java.util.HashMap;
import java.util.Map;


/**
 *  Class to hold references to Map of the DatastoreInterface instances.
 * 
 *  Last modified: $Date: 2006/04/09 12:13:15 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class DatastoreInterfaceManager {
	
	
	private Map interfacesMap;
	private Map interfacesByDatasourcesMap;
	
	
	/**
	 * 
	 */
	DatastoreInterfaceManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return Returns the interfacesByDatasourcesMap.
	 */
	public Map getInterfacesByDatasourcesMap() {
		if(this.interfacesByDatasourcesMap==null){
			this.interfacesByDatasourcesMap=new HashMap();
		}
		return this.interfacesByDatasourcesMap;
	}
	/**
	 * @param interfacesByDatasourcesMap The interfacesByDatasourcesMap to set.
	 */
	public void setInterfacesByDatasourcesMap(Map interfacesByDatasourcesMap) {
		this.interfacesByDatasourcesMap = interfacesByDatasourcesMap;
	}
	/**
	 * @return Returns the interfacesMap.
	 */
	public Map getInterfacesMap() {
		if(this.interfacesMap==null){
			this.interfacesMap = new HashMap();
		}
		return this.interfacesMap;
	}
	/**
	 * @param interfacesMap The interfacesMap to set.
	 */
	public void setInterfacesMap(Map interfacesMap) {
		this.interfacesMap = interfacesMap;
	}
}
