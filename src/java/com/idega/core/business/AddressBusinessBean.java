package com.idega.core.business;

import com.idega.util.text.TextSoap;
import java.util.StringTokenizer;
import javax.ejb.FinderException;
import javax.ejb.CreateException;
import com.idega.data.IDOQuery;
import com.idega.core.data.*;
import com.idega.business.*;
import java.rmi.RemoteException;

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


} // Class AddressBusinessBean