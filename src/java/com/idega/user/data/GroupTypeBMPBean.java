package com.idega.user.data;

import java.util.Collection;
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
  private static String TYPE_COLUMN="GROUP_TYPE";
  private static String DESCRIPTION_COLUMN="DESCRIPTION";
  private static String COLUMN_HANDLER_CLASS="HANDLER_CLASS_ID";
  private static String COLUMN_IS_VISIBLE = "IS_VISIBLE";

  private static final String TYPE_GENERAL_GROUP = "general";
  private static final String TYPE_USER_REPRESENTATIVE = "ic_user_representative";
  private static final String TYPE_PERMISSION_GROUP = "permission";

  public void initializeAttributes() {
    //this.addAttribute(getIDColumnName());
    this.addAttribute(getIDColumnName(),"Type",String.class,30);
//    this.setUnique(getIDColumnName(),true);
    this.setAsPrimaryKey(getIDColumnName(),true);
    this.addAttribute(DESCRIPTION_COLUMN,"Description",String.class,1000);
//    this.addAttribute(COLUMN_HANDLER_CLASS, "GroupTypeHandler",String.class,500); this is handled with plugins
 //   this.addAttribute(COLUMN_HANDLER_CLASS, "GroupTypeHandler",true,true, Integer.class,"one-to-many",ICObject.class);
    this.addAttribute(COLUMN_IS_VISIBLE,"is Visible",Boolean.class);
  }


  public void setDefaultValues(){
    setVisibility(true);
  }

  public void insertStartData(){
    try {
      GroupTypeHome home = (GroupTypeHome)IDOLookup.getHome(GroupType.class);

      GroupType type = home.create();
      type.setType(TYPE_GENERAL_GROUP);
      type.setDescription("");
      type.setVisibility(true);
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
      type.setVisibility(true);
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
      type.setType(TYPE_USER_REPRESENTATIVE);
      type.setDescription("");
      type.setVisibility(false);
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

  public void setVisibility(boolean visible){
    setColumn(COLUMN_IS_VISIBLE, visible);
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

 /* public Class getHandlerClass() throws ClassNotFoundException {
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
*/

  public String getGeneralGroupTypeString(){
    return TYPE_GENERAL_GROUP;
  }

  public String getPermissionGroupTypeString(){
    return TYPE_PERMISSION_GROUP;
  }

  public void setGroupTypeAsGeneralGroup(){
    setType(this.TYPE_GENERAL_GROUP);
  }

  public void setGroupTypeAsPermissionGroup(){
    setType(this.TYPE_PERMISSION_GROUP);
  }

  public boolean getVisibility(){
    return getBooleanColumnValue(COLUMN_IS_VISIBLE,true);
  }

  public Collection ejbFindAllGroupTypes() throws FinderException{
    return super.idoFindIDsBySQL("select * from "+getEntityName());
  }

  public Collection ejbFindVisibleGroupTypes() throws FinderException{
    IDOQuery query = new IDOQuery();
    query.appendSelectAllFrom();
    query.append(getEntityName());
    query.appendWhere(COLUMN_IS_VISIBLE);
    query.appendNOTEqual();
    query.appendWithinSingleQuotes(super.COLUMN_VALUE_FALSE);
    query.appendOrderBy(this.getIDColumnName());
//    System.out.println("[GroupTypeBMPBean](ejbFindVisibleGroupTypes): "+query.toString());
    return this.idoFindPKsBySQL(query.toString());
//    return super.idoFindIDsBySQL("select * from "+getEntityName()+" where "+ COLUMN_IS_VISIBLE + "!='"+super.COLUMN_VALUE_FALSE+"'");
  }

  public int ejbHomeGetNumberOfVisibleGroupTypes() throws FinderException, IDOException {
    IDOQuery query = new IDOQuery();
    query.appendSelectCountFrom();
    query.append(getEntityName());
    query.appendWhere(COLUMN_IS_VISIBLE);
    query.appendNOTEqual();
    query.appendWithinSingleQuotes(super.COLUMN_VALUE_FALSE);
//    System.out.println("[GroupTypeBMPBean](ejbHomeGetNumberOfVisibleGroupTypes): "+query.toString());
    return this.idoGetNumberOfRecords(query.toString());
//    return super.idoGetNumberOfRecords("select count(*) from "+getEntityName()+" where "+ COLUMN_IS_VISIBLE + "!='"+super.COLUMN_VALUE_FALSE+"'");
  }


}