package com.idega.presentation.ui;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.core.location.business.AddressBusiness;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;


/**
 * A UI element that can display postalcodes from the database the parameter it submits is: iw_postal_code_id and is the id for the PostalCode
 * 
 * @author Eirikur Hrafnsson	
 */
public class PostalCodeDropdownMenu extends DropdownMenu {
	
	private Country country = null;
	private String countryName = null;
	private boolean showCountry = false;
	public static final String IW_POSTAL_CODE_MENU_PARAM_NAME="iw_postal_code_id";
	
	public PostalCodeDropdownMenu(){
		super(IW_POSTAL_CODE_MENU_PARAM_NAME); 
	}
	
	public void main(IWContext iwc) throws Exception{
		super.main(iwc);
		clearChildren();
		//TODO extract the following code to a method in CountryDropDownMenu or CountryBMPBean
		if (this.country == null) {
		    try {
				if( this.countryName!=null){
				    this.country = getAddressBusiness(iwc).getCountryHome().findByCountryName(this.countryName);	
				}
				// we must ensure no external selected country is set
				else if(this.country==null){
				    this.country = getAddressBusiness(iwc).getCountryHome().findByIsoAbbreviation(iwc.getCurrentLocale().getCountry());	
				}
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			catch (EJBException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
		}
		if( this.country!=null ){
			if(this.showCountry){
				addMenuElement(-1,this.country.getName());
			}
			addMenuElement(-1,"");
			Collection postals = getAddressBusiness(iwc).getPostalCodeHome().findAllByCountryIdOrderedByPostalCode(((Integer)this.country.getPrimaryKey()).intValue());
			Iterator iter = postals.iterator();
			while (iter.hasNext()) {
				PostalCode element = (PostalCode) iter.next();
				int id = ((Integer)element.getPrimaryKey()).intValue();
				String code = element.getPostalAddress();
				if( code!=null ) {
					addMenuElement(id,code);
				}						
			}
		}
		else {
			addMenuElement("No country selected");
		}
				
	}
	
	public void setShowCountry(boolean flag){
		this.showCountry = flag;
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
