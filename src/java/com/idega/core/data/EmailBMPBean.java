package com.idega.core.data;



import com.idega.core.business.EmailDataView;

import com.idega.data.*;

import java.sql.SQLException;
import javax.ejb.FinderException;
import java.rmi.RemoteException;
import java.util.Collection;

import com.idega.core.user.data.User;





/**

 * Title:        IW Core

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>

 * @version 1.0

 */



public class EmailBMPBean extends com.idega.data.GenericEntity implements com.idega.core.data.Email,com.idega.core.business.EmailDataView {



  public EmailBMPBean(){

    super();

  }



  public EmailBMPBean(int id)throws SQLException{

    super(id);

  }





  public void initializeAttributes() {

    this.addAttribute(this.getIDColumnName());

    this.addAttribute(getColumnNameAddress(),"Email address",true,true,String.class,255);

    addManyToOneRelationship(getColumnNameEmailTypeId(),"Type",EmailType.class);

    this.addManyToManyRelationShip(User.class,"ic_user_email");

  }



  public String getEntityName() {

    return "ic_email";

  }



  public static String getColumnNameAddress(){return "ADDRESS";}

  public static String getColumnNameEmailTypeId(){return "ic_email_type_id";}



  public void setEmailAddress(String address){

    setColumn(getColumnNameAddress(),address);

  }

  public String getEmailAddress(){

    return getStringColumnValue(getColumnNameAddress());

  }

  public void setEmailTypeId(int id){

    setColumn(getColumnNameEmailTypeId(),id);

  }

  public int getEmailTypeId(){

    return getIntColumnValue(getColumnNameEmailTypeId());

  }


  public Collection ejbFindEmailsForUser(com.idega.user.data.User user)throws FinderException,RemoteException{
    int userId = ((Integer)user.getPrimaryKey()).intValue();
    return ejbFindEmailsForUser(userId);
  }


  public Collection ejbFindEmailsForUser(int iUserId)throws FinderException{
    StringBuffer sql = new StringBuffer("select ie.* ");
    sql.append("from ic_email ie,ic_user_email iue ");
    sql.append("where ie.ic_email_id = iue.ic_email_address ");
    sql.append(" and iue.ic_user_id = ");
    sql.append(iUserId);
    return super.idoFindIDsBySQL(sql.toString());
  }


  public Integer ejbFindEmailByAddress(String address)throws FinderException{
    Collection coll = super.idoFindAllIDsByColumnOrderedBySQL(this.getColumnNameAddress(),address);
    if(!coll.isEmpty())
      return (Integer)coll.iterator().next();
    else
      throw new FinderException("No email found");
  }

}
