/*
 * $Id: AddressCoordinateImportHandlerBean.java,v 1.1 2005/02/04 00:09:24 gimmi Exp $
 * Created on 3.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOServiceBean;
import com.idega.core.location.data.AddressCoordinate;
import com.idega.core.location.data.AddressCoordinateHome;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2005/02/04 00:09:24 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.1 $
 */
public class AddressCoordinateImportHandlerBean extends IBOServiceBean implements AddressCoordinateImportHandler{
	
	/*
	 insert into im_handler values (null,'AddressCoordinate Handler', 'com.idega.core.location.business.AddressCoordinateImportHandler', 'AddressCoordinate Handler', null, null)
	 */
	
	private HashMap communeMap;
	private HashMap coordMap;
	private CommuneHome commHome;
	private AddressCoordinateHome coordHome;
	
	ImportFile importFile = null;
	
	public boolean handleRecords() throws RemoteException {
		
		try {
			communeMap = new HashMap();
			coordMap = new HashMap();
			try {
				commHome = (CommuneHome) IDOLookup.getHome(Commune.class);
				coordHome = (AddressCoordinateHome) IDOLookup.getHome(AddressCoordinate.class);
			}
			catch (IDOLookupException e1) {
				e1.printStackTrace();
			}
			
			int counter = 0; 
			String record;
			while (!(record = (String) importFile.getNextRecord()).equals("")) {
				counter++;
				ArrayList values = importFile.getValuesFromRecordString(record);
				createCoordinateIfDoesNotExist((String) values.get(0), (String) values.get(1),
						(String) values.get(2), (String) values.get(3), (String) values.get(4), (String) values.get(5));
				if (counter % 50 == 0) {
					System.out.println("AddressCoordinateImportHandler : "+counter+" records imported");
				}
			}
			System.out.println("AddressCoordinateImportHandler : "+counter+" records imported");
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
		
	}
	
	private void createCoordinateIfDoesNotExist(String county, String communeCode, String parish, String coordinate, String coordinateCode, String newCoordinateCode ) {
		try {
			Integer.parseInt(county);		// Just making sure the line is valid
			Integer.parseInt(communeCode);	// Just making sure the line is valid
			Integer.parseInt(parish); 		// Just making sure the line is valid
			
			Commune commune = getCommune(communeCode);
			AddressCoordinate coord = getCoordinate(coordinate.trim());
			if (commune != null) {
				coord.setCommune(commune);
			}
			if (newCoordinateCode != null && !newCoordinateCode.trim().equals("")) {
				coord.setCoordinateCode(newCoordinateCode);
			} else if (coordinateCode != null && !coordinateCode.trim().equals("")) {
				coord.setCoordinateCode(coordinateCode);
			}
			coord.store();
			
		} catch (NumberFormatException n) {
			System.out.println("[IWBundleStarter (core) Not a valid import line");
		}
	}
	
	// If created, then NOT stored
	private AddressCoordinate getCoordinate(String coordinate) {
		AddressCoordinate coord = (AddressCoordinate) coordMap.get(coordinate);
		if (coord == null) {
			try {
				coord = coordHome.findByCoordinate(coordinate);
			} catch (FinderException e) {
				try {
					coord = coordHome.create();
					coord.setCoordinate(coordinate);
				}
				catch (CreateException e1) {
					e1.printStackTrace();
				}
			}
			coordMap.put(coordinate, coord);
		}
		return coord;
	}
	
	private Commune getCommune(String communeCode) {
		Commune comm = (Commune) communeMap.get(communeCode);
		if (comm == null) {
			try {
				comm = commHome.findByCommuneCode(communeCode);
				communeMap.put(communeCode, comm);
			} catch (FinderException e) {
				//e.printStackTrace();
			}
		}
		return comm;
	}
	
	public void setImportFile(ImportFile file) throws RemoteException {
		this.importFile = file;
	}
	
	public void setRootGroup(Group rootGroup) throws RemoteException {
	}
	
	public List getFailedRecords() throws RemoteException {
		return null;
	}
}
