package com.idega.core.business;


/**
 * Title:PostalCodeBundleStarter
 * Description: PostalCodeBundleStarter implements the IWBundleStartable interface. The start method of this
 * object is called during the Bundle loading when starting up a idegaWeb applications. It is used to register and update postalcodes from text files
 * within the bundle that it is registered in. Simply registed this starter class in your bundle and create a folder called 'postalcode' and put a text file(s) in
 * it with the postal codes. The text file must be a commaseparated file with the columns as such:<br>
 * code;areaname;countryname
 * Copyright: idega software 2002
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import java.io.File;
import java.rmi.RemoteException;
import java.util.*;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.*;
import com.idega.util.*;
import com.idega.block.media.presentation.MediaToolbarButton;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.core.data.*;
import com.idega.block.importer.data.*;
import com.idega.business.*;

public class PostalCodeBundleStarter implements IWBundleStartable{

private IWApplicationContext iwac;

  public PostalCodeBundleStarter() {
  }

  public void start(IWBundle bundle){
  	iwac = bundle.getApplication().getIWApplicationContext();
  	
  	File postalCodeFolder = new File(bundle.getResourcesRealPath()+FileUtil.getFileSeparator()+"postalcode");
  	
  	if( postalCodeFolder.isDirectory() ){
   		File[] files = postalCodeFolder.listFiles();
   			
  		if(files!=null && files.length>0){
  			for (int i = 0; i < files.length; i++) {
				ColumnSeparatedImportFile postals = new ColumnSeparatedImportFile(files[i]);
				
				String record;
      
      			while ( !(record=(String)postals.getNextRecord()).equals("") ){
      				ArrayList values = postals.getValuesFromRecordString(record);
      				createPostalIfDoesNotExist((String)values.get(0),(String)values.get(1),(String)values.get(2));			      				
      			}
				
			}
  				
  		}
  		
  	}

  }


	private void createPostalIfDoesNotExist(String code, String area, String countryName){
		
		try {
			AddressBusiness biz = getAddressBusiness();
			Country country = ((CountryHome)IDOLookup.getHome(Country.class)).findByCountryName(countryName);
			
			biz.getPostalCodeAndCreateIfDoesNotExist(code,area,country);
			
		} catch (Exception e) {
			System.out.println("PostalCodeBundleStarter: import failed for : "+code+ ", "+area+", "+countryName );
			e.printStackTrace();
		}
	}

	
	private AddressBusiness getAddressBusiness() throws RemoteException{
		return (AddressBusiness) IBOLookup.getServiceInstance(iwac,AddressBusiness.class);
	}
	
	
}
