package com.idega.core.data;

import java.sql.*;
import com.idega.data.*;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */


public class Phone extends GenericEntity{

    public Phone(){
      super();
    }

    public Phone(int id)throws SQLException{
      super(id);
    }

    public void initializeAttributes(){
      addAttribute(getIDColumnName());
      addAttribute("phone_number","Númer",true,true,"java.lang.String");
      addAttribute("ic_country_code_id","Landsnúmer",true,true,"java.lang.Integer");
      addAttribute("ic_area_code_id","Svæðisnúmer",true,true,"java.lang.Integer");
      addAttribute("phone_attribute","Tegund",true,true,"java.lang.String");
      addAttribute("phone_attribute_value","Gildi",true,true,"java.lang.String");
      this.addManyToManyRelationShip(PhoneType.class,"ic_phone_phone_type");
    }

    public String getEntityName(){
      return "ic_phone";
    }

    public void setDefaultValues() {
      setColumn("ic_country_code_id",-1);
      setColumn("ic_area_code_id",-1);
    }

    public String getNumber(){
      return (String)getColumnValue("phone_number");
    }

    public void setNumber(String number){
      setColumn("phone_number", number);
    }


    public String getPhoneType(){
      return (String) getColumnValue("phone_type");
    }

    public void setPhoneType(String phone_type){
      setColumn("phone_type", phone_type);
    }

}
