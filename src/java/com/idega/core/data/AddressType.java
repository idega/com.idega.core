package com.idega.core.data;

import java.sql.*;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class AddressType extends GenericType{

  public final static String ADDRESS_1 = "ic_user_address_1";
  public final static String ADDRESS_2 = "ic_user_address_2";

	public AddressType(){
          super();
	}

	public AddressType(int id)throws SQLException{
          super(id);
	}

	public String getEntityName(){
          return "ic_address_type";
	}

        public void insertStartData()throws Exception{
          AddressType at;

          at = new AddressType();
            at.setName("Home");
            at.setDescription("Home");
            at.setUniqueName(ADDRESS_1);
          at.insert();

          at = new AddressType();
            at.setName("Work");
            at.setDescription("Work");
            at.setUniqueName(ADDRESS_2);
          at.insert();
        }

}
