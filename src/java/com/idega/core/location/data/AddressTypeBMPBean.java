package com.idega.core.location.data;



import java.sql.SQLException;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.data.GenericTypeBMPBean;


/**

 * Title:        IW Core

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>

 * @version 1.0

 */



public class AddressTypeBMPBean extends com.idega.core.data.GenericTypeBMPBean implements com.idega.core.location.data.AddressType {



  public final static String ADDRESS_1 = "ic_user_address_1";

  public final static String ADDRESS_2 = "ic_user_address_2";



	public AddressTypeBMPBean(){

          super();

	}



	public AddressTypeBMPBean(int id)throws SQLException{

          super(id);

	}



	public String getEntityName(){

          return "ic_address_type";

	}



        public void insertStartData()throws Exception{

          AddressType at;



          at = ((com.idega.core.location.data.AddressTypeHome)com.idega.data.IDOLookup.getHomeLegacy(AddressType.class)).createLegacy();

            at.setName("Home");

            at.setDescription("Home");

            at.setUniqueName(ADDRESS_1);

          at.insert();



          at = ((com.idega.core.location.data.AddressTypeHome)com.idega.data.IDOLookup.getHomeLegacy(AddressType.class)).createLegacy();

            at.setName("Work");

            at.setDescription("Work");

            at.setUniqueName(ADDRESS_2);

          at.insert();

        }







        public static int getId(String uniqueKey) throws SQLException {
            int returner;
            AddressType[] addrTypes = (AddressType[]) (((com.idega.core.location.data.AddressTypeHome)com.idega.data.IDOLookup.getHomeLegacy(AddressType.class)).createLegacy()).findAllByColumn(GenericTypeBMPBean.getColumnNameUniqueName(),uniqueKey);
            if (addrTypes.length == 0) {
                AddressType addrType = ((com.idega.core.location.data.AddressTypeHome)com.idega.data.IDOLookup.getHomeLegacy(AddressType.class)).createLegacy();
                  addrType.setUniqueName(uniqueKey);
                  addrType.setDisplayName(uniqueKey);
                addrType.insert();
                returner = addrType.getID();
            }else {
              returner = addrTypes[addrTypes.length-1].getID();
            }
            return returner;
        }


        public Integer ejbFindAddressType1()throws FinderException{
            Collection coll = super.idoFindAllIDsByColumnBySQL(getColumnNameUniqueName(),ADDRESS_1);
            if(!coll.isEmpty())
              return (Integer)coll.iterator().next();
            else
              throw new FinderException("AddressType1 does not exist");
        }

        public Integer ejbFindAddressType2()throws FinderException{
            Collection coll = super.idoFindAllIDsByColumnBySQL(getColumnNameUniqueName(),ADDRESS_2);
            if(!coll.isEmpty())
              return (Integer)coll.iterator().next();
            else
              throw new FinderException("AddressType2 does not exist");
        }



}

