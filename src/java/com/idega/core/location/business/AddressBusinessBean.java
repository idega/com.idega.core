package com.idega.core.location.business;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.business.IBOServiceBean;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.AddressHome;
import com.idega.core.location.data.Commune;
import com.idega.core.location.data.CommuneHome;
import com.idega.core.location.data.Country;
import com.idega.core.location.data.CountryHome;
import com.idega.core.location.data.PostalCode;
import com.idega.core.location.data.PostalCodeHome;
import com.idega.util.text.TextSoap;

 /**
  * <p>Title: com.idega.core.business.AddressBusinessBean</p>
  * <p>Description: Common business class for handling all Address related IDO</p>
  * <p>Copyright: (c) 2002</p> 
  * <p>Company: Idega Software</p>
  * @author <a href="eiki@idega.is">Eirikur S. Hrafnsson</a>
  * @version 1.0
  */

public class AddressBusinessBean extends IBOServiceBean implements AddressBusiness{

  public AddressBusinessBean() {
  }

  /**
   * @return The Country Beans' home
   */
  public CountryHome getCountryHome() throws RemoteException{
    return (CountryHome)this.getIDOHome(Country.class);
  }
  
  /**
   * @return The Commune Beans' home
   */
  public CommuneHome getCommuneHome() throws RemoteException{
    return (CommuneHome)this.getIDOHome(Commune.class);
  }
  
    /**
   * @return The PostalCode Beans' home
   */
  public PostalCodeHome getPostalCodeHome() throws RemoteException{
    return (PostalCodeHome)this.getIDOHome(PostalCode.class);
  }
  
    /**
   * @return The Email Beans' home
   */
  public EmailHome getEmailHome() throws RemoteException{
    return (EmailHome)this.getIDOHome(Email.class);
  }
  
    /**
   * @return The Address Beans' home
   */
  public AddressHome getAddressHome() throws RemoteException{
    return (AddressHome)this.getIDOHome(Address.class);
  }


  /**
   * Finds and updates or Creates a new postal code
   * @return A new or updates PostalCode
   */
  public PostalCode getPostalCodeAndCreateIfDoesNotExist(String postCode, String name, Country country) throws CreateException,RemoteException{
    PostalCode code;
    PostalCodeHome home = (PostalCodeHome)this.getIDOHome(PostalCode.class);

    try{
      code = home.findByPostalCodeAndCountryId(postCode,((Integer)country.getPrimaryKey()).intValue() );
    }
    catch(FinderException ex){
      code = home.create();
      code.setPostalCode(postCode);
      code.setName(name);
      code.setCountry(country);
      code.store();
    }

    return code;
  }

	/**
	 * Connects the postal code with the given commune
	 * The commune code is set to the commune name if it is not already set.
	 * This is a simplification since we didn't have that data for Iceland
	 */
	public void connectPostalCodeToCommune(PostalCode postalCode, String Commune) throws RemoteException, CreateException {
		CommuneHome communeHome = getCommuneHome();
		Commune commune;
		try {
			commune = communeHome.findByCommuneCode(Commune);
		}
		catch (FinderException e) {
			commune = communeHome.create();
			commune.setCommuneCode(Commune);
			commune.setCommuneName(Commune);
			commune.setIsValid(true);
			commune.store();
		}
		postalCode.setCommune(commune);
		postalCode.store();
	}
	
	
  /**
   * Change postal code name when only one address is related to the postalcode
   */
  public PostalCode changePostalCodeNameWhenOnlyOneAddressRelated(PostalCode postalCode,String newName){
  	java.util.Collection addresses = postalCode.getAddresses();
  	if(addresses!=null && addresses.size()==1){
  		postalCode.setName(newName);
  		postalCode.store();
  	}
  	return postalCode;
  		
  }


  /**
   * Gets the streetname from a string with the format.<br>
   * "Streetname Number ..." e.g. "My Street 24 982 NY" would return "My Street".<br>
   * not very flexibel but handles "my street 24, 982 NY" the same way.
   * @return Finds the first number in the string and return a sbustring to that point or the whole string if no number is present
   */
  public String getStreetNameFromAddressString(String addressString){
   int index = TextSoap.getIndexOfFirstNumberInString(addressString);
    if( index==-1 ){
      return addressString;
    }
    else{
      return addressString.substring(0,index);
    }
  }

  /**
   * Gets the streetnumber from a string with the format.<br>
   * "Streetname Number ..." e.g. "My Street 24" would return "24".<br>
   * @return Finds the first number in the string and returns a substring from that point or null if no number found
   */
  public String getStreetNumberFromAddressString(String addressString){
    int index = TextSoap.getIndexOfFirstNumberInString(addressString);
    if( index!=-1 ){
      return addressString.substring(index,addressString.length());
    }
    return null;
  }
  
  /**
   * Creates the fully qualifying address string with postal and country info from the address bean<br>
   * seperated by a ","
   * @return The full address string with postal info and country
   */
  public String getFullAddressString(Address address){
  	StringBuffer fullAddress = new StringBuffer();
    String streetNameAndNumber = address.getStreetAddress();
    String postalCode = address.getPostalAddress();
    Country country = address.getCountry();
    /*String countryName = "";
    if(country!=null) {
    		countryName = country.getName();
    }*/
    
    fullAddress.append(streetNameAndNumber).append(", ")
	.append(postalCode).append(", ")
	.append(country);
  	
  	return fullAddress.toString();
  }


} // Class AddressBusinessBean