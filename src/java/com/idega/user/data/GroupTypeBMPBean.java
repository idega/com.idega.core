package com.idega.user.data;

import com.idega.core.data.ICObject;
import com.idega.builder.data.IBDomain;
import javax.ejb.*;
import java.rmi.RemoteException;
import com.idega.data.*;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GroupTypeBMPBean extends GenericEntity implements GroupType{

  private static String TABLE_NAME="IC_GROUP_TYPE";
  private static String TYPE_COLUMN="TYPE";
  private static String DESCRIPTION_COLUMN="DESCRIPTION";
  private static String COLUMN_HANDLER_CLASS="HANDLER_CLASS_ID";

  private static final String TYPE_GENERAL_GROUP = "general";
  private static final String TYPE_PERMISSION_GROUP = "permission";

  public void initializeAttributes() {
    //this.addAttribute(getIDColumnName());
    this.addAttribute(getIDColumnName(),"Type",String.class,30);
    this.addAttribute(DESCRIPTION_COLUMN,"Description",String.class,1000);
//    this.addAttribute(COLUMN_HANDLER_CLASS, "GroupTypeHandler",String.class,500);
    this.addAttribute(COLUMN_HANDLER_CLASS, "GroupTypeHandler",true,true, Integer.class,"one-to-many",ICObject.class);
  }


  public void insertStartData(){
    try {
      GroupTypeHome home = (GroupTypeHome)IDOLookup.getHome(GroupType.class);

      GroupType type = home.create();
      type.setType(TYPE_GENERAL_GROUP);
      type.setDescription("");
      type.store();
    }
    catch (RemoteException ex) {
      throw new EJBException(ex);
    }
    catch (CreateException ex) {
      ex.printStackTrace();
    }

    try {
      GroupTypeHome home = (GroupTypeHome)IDOLookup.getHome(GroupType.class);

      GroupType type = home.create();
      type.setType(TYPE_PERMISSION_GROUP);
      type.setDescription("");
      type.store();
    }
    catch (RemoteException ex) {
      throw new EJBException(ex);
    }
    catch (CreateException ex) {
      ex.printStackTrace();
    }
  }

  public String getEntityName() {
    return TABLE_NAME;
  }

  public void setType(String type){
    setColumn(TYPE_COLUMN,type);
  }

  public String getType(){
    return getStringColumnValue(TYPE_COLUMN);
  }

  public void setDescription(String desc){
    setColumn(DESCRIPTION_COLUMN,desc);
  }

  public String getDescription(){
    return getStringColumnValue(DESCRIPTION_COLUMN);
  }

  public String getIDColumnName(){
    return TYPE_COLUMN;
  }

  public Class getPrimaryKeyClass(){
    return String.class;
  }

  public Class getHandlerClass() throws ClassNotFoundException {
    ICObject obj = (ICObject)this.getColumn(COLUMN_HANDLER_CLASS);
    if(obj != null){
      return obj.getObjectClass();
    } else {
      return null;
    }
  }



  public void setHandlerClass(ICObject obj){
    setColumn(COLUMN_HANDLER_CLASS,obj);
  }

}