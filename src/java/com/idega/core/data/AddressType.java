//idega 2000 - eiki

package com.idega.core.data;
//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class AddressType extends GenericEntity{

	public AddressType(){
		super();
	}

	public AddressType(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("name", "Nafn", true, true, String.class);
		addAttribute("description", "Lýsing", true, true, String.class);
	}

	public String getEntityName(){
            return "ic_address_type";
	}

        public void insertStartData()throws Exception{
            AddressType at;

            at = new AddressType();
              at.setName("Home");
              at.setDescription("Home");
            at.insert();

            at = new AddressType();
              at.setName("Work");
              at.setDescription("Work");
            at.insert();
        }


	public String getName(){
            return getStringColumnValue("name");
	}

	public void setName(String name){
            setColumn("name",name);
	}

	public void setDescription(String description){
            setColumn("description",description);
	}
	public String getDescription(){
            return getStringColumnValue("description");
	}

}
