package com.idega.core.business;

import com.idega.core.data.Address;
import com.idega.core.data.AddressHome;
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

 // public getPostalCodeBy


  //public Address createAddress(String


} // Class AddressBusinessBean