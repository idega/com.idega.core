package com.idega.presentation.ui;

import java.rmi.RemoteException;

import com.idega.business.IBOLookup;
import com.idega.core.business.AddressBusiness;
import com.idega.core.data.*;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import java.util.*;


/**
 * A UI element that can display postalcodes from the database the parameter it submits is: iw_postal_code_id and is the id for the PostalCode
 * 
 * @author Eirikur Hrafnsson	
 */
public class PostalCodeDropdownMenu extends DropdownMenu {
	
	private Country country = null;
	private String countryName = null;
	public static final String IW_POSTAL_CODE_MENU_PARAM_NAME="iw_postal_code_id";
	
	public PostalCodeDropdownMenu(){
		super(IW_POSTAL_CODE_MENU_PARAM_NAME); 
	}
	
	public void main(IWContext iwc) throws Exception{
		if( countryName!=null && country == null) {
			country = getAddressBusiness(iwc).getCountryHome().findByCountryName(countryName);			
		}
		
		if( country!=null ){
			Collection postals = getAddressBusiness(iwc).getPostalCodeHome().findAllByCountryIdOrderedByPostalCode(((Integer)country.getPrimaryKey()).intValue());
			Iterator iter = postals.iterator();
			while (iter.hasNext()) {
				PostalCode element = (PostalCode) iter.next();
				addMenuElement(((Integer)element.getPrimaryKey()).intValue(),element.getPostalCode());						
			}
		}
		else addMenuElement("No country selected");
				
	}
	
	
	public void setCountry(Country country){
		this.country = country;
	}

	public void setCountry(String countryName){
		this.countryName = countryName;
	}


	private AddressBusiness getAddressBusiness(IWApplicationContext iwc) throws RemoteException{
		return (AddressBusiness) IBOLookup.getServiceInstance(iwc,AddressBusiness.class);
	}
	
}
