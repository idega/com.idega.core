package com.idega.user.data;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class GroupDomainRelationTypeBMPBean extends GenericEntity implements GroupDomainRelationType{

	private static final String  TABLE_NAME="IC_GROUP_DOMAIN_REL_TYPE";
	private static final String  TYPE_COLUMN="RELATION_TYPE";
	private static final String  DESCRIPTION_COLUMN="DESCRIPTION";

  public static final String RELATION_TYPE_TOP_NODE = "TOP_NODE";
//  private static final String RELATION_TYPE_CREATION_LOCATION = "CREATION_LOCATION";


  public void initializeAttributes() {
    //this.addAttribute(getIDColumnName());
    this.addAttribute(getIDColumnName(),"Type",String.class,30);
    this.setAsPrimaryKey(getIDColumnName(),true);
    this.addAttribute(DESCRIPTION_COLUMN,"Description",String.class,1000);
  }

  public void insertStartData(){
    try {
      GroupDomainRelationTypeHome home = (GroupDomainRelationTypeHome)IDOLookup.getHome(GroupDomainRelationType.class);

      GroupDomainRelationType type1 = home.create();
      type1.setType(RELATION_TYPE_TOP_NODE);
      type1.setDescription("");
      type1.store();

//      GroupDomainRelationType type2 = home.create();
//      type2.setType(RELATION_TYPE_CREATION_LOCATION);
//      type2.setDescription("");
//      type2.store();

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

  public GroupDomainRelationType ejbHomeGetTopNodeRelationType() throws FinderException {
    try {
			return ((GroupDomainRelationTypeHome)IDOLookup.getHome(GroupDomainRelationType.class)).findByPrimaryKey(RELATION_TYPE_TOP_NODE);
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		return null;
  }
  
	public String ejbHomeGetTopNodeRelationTypeString() {
		return RELATION_TYPE_TOP_NODE;
	}

//  public String ejbHomeGetPrimaryKeyForCreationLocationRelation(){
//    return RELATION_TYPE_CREATION_LOCATION;
//  }

}