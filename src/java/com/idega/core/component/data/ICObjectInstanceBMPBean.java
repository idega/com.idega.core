//idega 2001 - Tryggvi Larusson
/*

*Copyright 2001 idega.is All Rights Reserved.

*/
package com.idega.core.component.data;
import java.sql.SQLException;

import com.idega.core.component.business.ICObjectBusiness;
import com.idega.data.IDORemoveRelationshipException;
import com.idega.presentation.PresentationObject;
/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class ICObjectInstanceBMPBean extends com.idega.data.GenericEntity implements ICObjectInstance{
	public ICObjectInstanceBMPBean()
	{
		super();
	}
	public ICObjectInstanceBMPBean(int id) throws Exception
	{
		super(id);
	}
	public static final String COLUMN_OBJECT_ID = "IC_OBJECT_ID";
	public static final String IBPAGEID = "IB_PAGE_ID";
	public static final String COLUMNNAME_PARENTID = "DPT_PARENT_ID";
	public void initializeAttributes()
	{
		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addManyToOneRelationship(COLUMN_OBJECT_ID, "Object", ICObject.class);
		addManyToOneRelationship(IBPAGEID, "Page", com.idega.core.builder.data.ICPage.class);
		addManyToOneRelationship(COLUMNNAME_PARENTID, "ParentId", ICObjectInstance.class);
	}
	public String getEntityName()
	{
		return "IC_OBJECT_INSTANCE";
	}
	public void setDefaultValues()
	{
		//setColumn("image_id",1);
	}
	public String getName()
	{
		return getObject().getName() + " nr. " + this.getID();
	}
	public void setICObjectID(int id)
	{
		this.setColumn(COLUMN_OBJECT_ID, id);
	}
	public void setICObject(ICObject object)
	{
		this.setColumn(COLUMN_OBJECT_ID, object);
	}
	public int getIBPageID()
	{
		return getIntColumnValue(IBPAGEID);
	}
	public void setIBPageID(int id)
	{
		this.setColumn(IBPAGEID, id);
	}
	public int getParentInstanceID()
	{
		return getIntColumnValue(COLUMNNAME_PARENTID);
	}
	public void setParentInstanceID(int id)
	{
		this.setColumn(COLUMNNAME_PARENTID, id);
	}
	public ICObject getObject()
	{
		int icObjectID = this.getIntColumnValue(COLUMN_OBJECT_ID);
		return ICObjectBusiness.getInstance().getICObject(icObjectID);
	}
	public PresentationObject getNewInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		return getObject().getNewInstance();
	}

	public void setIBPageByKey(String pageKey)
	{
		try
		{
			int id = Integer.parseInt(pageKey);
			this.setIBPageID(id);
		}
		catch (NumberFormatException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	
	public void ejbHomeRemoveRelation(ICObjectInstance instance,Class relatedEntity )throws IDORemoveRelationshipException{
	    try {
            instance.removeFrom(relatedEntity);
        } catch (SQLException e) {
           throw new IDORemoveRelationshipException(e.getMessage());
        }
	}
}
