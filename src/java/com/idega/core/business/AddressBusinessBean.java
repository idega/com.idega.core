package com.idega.core.business;

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


  public AddressHome getAddressHome() throws RemoteException{
    return (AddressHome)this.getIDOHome(Address.class);
  }


  /**
   * Finds and updates or Creates a new postal code
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


  public String getStreetNameFromAddressString(String addressString){
    StringTokenizer tokens = new StringTokenizer(addressString);
    StringBuffer buf = new StringBuffer();

    while( tokens.hasMoreElements() ){
      String item = (String) tokens.nextElement();
      try{
        Integer.parseInt(item);
      }
      catch(Exception e ){
        buf.append(item);
        buf.append(' ');//behind last one also :(
      }
    }

    return buf.toString();

  }

  public String getStreetNumberFromAddressString(String addressString){
    StringTokenizer tokens = new StringTokenizer(addressString);
    StringBuffer buf = new StringBuffer();

    while( tokens.hasMoreElements() ){
      String item = (String) tokens.nextElement();
      try{
        Integer.parseInt(item);
        buf.append(item);
        buf.append(' ');//behind last one also :(
      }
      catch(Exception e ){
      }
    }

    return buf.toString();

  }

} // Class AddressBusinessBean