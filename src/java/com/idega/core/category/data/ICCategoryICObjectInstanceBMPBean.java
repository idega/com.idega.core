package com.idega.core.category.data;

import java.util.List;

import javax.ejb.FinderException;

import com.idega.core.component.data.*;
import com.idega.data.EntityFinder;
import com.idega.data.GenericEntity;
import com.idega.data.IDOException;
import com.idega.data.SimpleQuerier;


/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class ICCategoryICObjectInstanceBMPBean extends GenericEntity implements ICCategoryICObjectInstance {

  private String IC_CATEGORY_COLUMN_NAME = ICCategoryBMPBean.getEntityTableName()+"_ID";
  private String IC_OBJECT_INSTANCE_COLUMN_NAME = "IC_OBJECT_INSTANCE_ID";
  private String TREE_ORDER_COLUMN_NAME = "TREE_ORDER";

  public ICCategoryICObjectInstanceBMPBean() {
  }
  public void initializeAttributes() {
    addAttribute(IC_CATEGORY_COLUMN_NAME,"categoryId", true, false, Integer.class,"many-to-one",ICCategory.class);
    addAttribute(IC_OBJECT_INSTANCE_COLUMN_NAME,"objectInstanceId", true, false, Integer.class,"many-to-one",ICObjectInstance.class);
    addAttribute(TREE_ORDER_COLUMN_NAME, "order", true, true, Integer.class);
  }
  public String getEntityName() {
    return "ic_category_ic_object_instance";
  }

  public int ejbHomeGetOrderNumber(Category category, ICObjectInstance instance) throws FinderException{
    try {
      String[] res = SimpleQuerier.executeStringQuery("SELECT TREE_ORDER FROM "+getEntityName()+" WHERE "+IC_OBJECT_INSTANCE_COLUMN_NAME+" = "+instance.getID()+" AND "+IC_CATEGORY_COLUMN_NAME+" = "+category.getID());
      if (res == null || res.length == 0 || res[0] == null) {
        return 0;
      }
      return Integer.parseInt(res[0]);
    }catch (Exception e) {
      throw new FinderException(e.getMessage());
    }
  }

  public boolean ejbHomeSetOrderNumber(Category category, ICObjectInstance instance, int orderNumber) throws IDOException {
    return this.idoExecuteTableUpdate("UPDATE "+getEntityName()+" SET "+TREE_ORDER_COLUMN_NAME+" = "+orderNumber+" WHERE "+IC_OBJECT_INSTANCE_COLUMN_NAME+" = "+instance.getID()+" AND "+IC_CATEGORY_COLUMN_NAME+" = "+category.getID());
  }

  public List ejbHomeGetListOfCategoryForObjectInstance(ICObjectInstance obj) throws FinderException {
    StringBuffer sql = new StringBuffer();
      sql.append("Select c.* from ").append(getEntityName()).append(" mt, ").append(ICCategoryBMPBean.getEntityTableName()).append(" c");
      sql.append(" where mt.").append(IC_OBJECT_INSTANCE_COLUMN_NAME).append(" = ").append(obj.getID());
      sql.append(" and mt.").append(IC_CATEGORY_COLUMN_NAME).append(" = c.").append(IC_CATEGORY_COLUMN_NAME);
      sql.append(" order by mt.").append(TREE_ORDER_COLUMN_NAME);//.append(" desc");
    return EntityFinder.getInstance().findAll(ICCategory.class, sql.toString());
  }

  public String ejbHomeGetRelatedSQL(int iObjectInstanceId) {
    return ejbHomeGetRelatedSQL(iObjectInstanceId, IC_CATEGORY_COLUMN_NAME);
  }

  public String ejbHomeGetRelatedSQL(int iObjectInstanceId, String returnColumnName) {
    StringBuffer sql = new StringBuffer();
      sql.append("Select c.").append(returnColumnName).append(" from ").append(getEntityName()).append(" mt, ").append(ICCategoryBMPBean.getEntityTableName()).append(" c");
      sql.append(" where mt.").append(IC_OBJECT_INSTANCE_COLUMN_NAME).append(" = ").append(iObjectInstanceId);
      sql.append(" and mt.").append(IC_CATEGORY_COLUMN_NAME).append(" = c.").append(IC_CATEGORY_COLUMN_NAME);
      sql.append(" order by mt.").append(TREE_ORDER_COLUMN_NAME);//.append(" desc");
    return sql.toString();  }
}