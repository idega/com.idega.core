package com.idega.core.business;



import com.idega.core.data.ICCategory;

import com.idega.core.data.ICObjectInstance;

import com.idega.data.CategoryEntity;

import com.idega.data.EntityControl;



import com.idega.data.EntityFinder;

import com.idega.data.GenericEntity;

import com.idega.data.IDOFinderException;



import java.sql.SQLException;

import java.util.Collection;

import java.util.HashMap;

import java.util.HashSet;

import java.util.Hashtable;

import java.util.List;

import java.util.Locale;

import java.util.Map;

import java.util.Vector;



/**

 *  Title: Description: Copyright: Copyright (c) 2000-2001 idega.is All Rights

 *  Reserved Company: idega

 *

 * @author     <a href="mailto:aron@idega.is">Aron Birkir</a>

 * @created    11. mars 2002

 * @version    1.1

 */



public class CategoryFinder {



  private static CategoryFinder categoryFinder;





  /**

   *  Gets the category of the CategoryFinder object

   *

   * @param  iCategoryId  Description of the Parameter

   * @return              The category value

   */

  public ICCategory getCategory(int iCategoryId) {

    if (iCategoryId > 0) {

      return (ICCategory) ICCategory.getEntityInstance(ICCategory.class, iCategoryId);

    }

    return null;

  }





  /**

   *  @todo Description of the Method

   *

   * @param  type  Description of the Parameter

   * @return       Description of the Return Value

   */

  public List listOfCategories(String type) {

    try {

      return EntityFinder.findAllByColumn(ICCategory.getStaticInstance(ICCategory.class), ICCategory.getColumnType(), type);

    } catch (SQLException ex) {

      return null;

    }

  }





  /**

   *  @todo Description of the Method

   *

   * @return    Description of the Return Value

   */

  public List listOfValidCategories() {

    try {

      return EntityFinder.findAllByColumn(ICCategory.getStaticInstance(ICCategory.class), ICCategory.getColumnValid(), "Y");

    } catch (SQLException ex) {

      return null;

    }

  }





  /**

   *  @todo Description of the Method

   *

   * @param  type  Description of the Parameter

   * @return       Description of the Return Value

   */

  public List listOfValidCategories(String type) {

    try {

      return EntityFinder.findAllByColumn(ICCategory.getStaticInstance(ICCategory.class), ICCategory.getColumnValid(), "Y", ICCategory.getColumnType(), type);

    } catch (SQLException ex) {

      return null;

    }

  }





  /**

   *  @todo Description of the Method

   *

   * @return    Description of the Return Value

   */

  public List listOfInValidCategories() {

    try {

      return EntityFinder.findAllByColumn(ICCategory.getStaticInstance(ICCategory.class), ICCategory.getColumnValid(), "N");

    } catch (SQLException ex) {

      return null;

    }

  }





  /**

   *  @todo Description of the Method

   *

   * @param  type  Description of the Parameter

   * @return       Description of the Return Value

   */

  public List listOfInValidCategories(String type) {

    try {

      return EntityFinder.findAllByColumn(ICCategory.getStaticInstance(ICCategory.class), ICCategory.getColumnValid(), "N", ICCategory.getColumnType(), type);

    } catch (SQLException ex) {

      return null;

    }

  }







  /**

   *  Gets the objectInstanceIdFromCategoryId of the CategoryFinder object

   *

   * @param  iCategoryId  Description of the Parameter

   * @return              The object instance id from category id value

   */

  public int getObjectInstanceIdFromCategoryId(int iCategoryId) {

    try {

      ICCategory nw = (ICCategory) getCategory(iCategoryId);

      List L = EntityFinder.findRelated(nw, new ICObjectInstance());

      if (L != null) {

        return ((ICObjectInstance) L.get(0)).getID();

      } else {

        return -1;

      }

    } catch (SQLException ex) {

      ex.printStackTrace();

      return -2;

    }

  }





  /**

   *  Gets the objectInstanceCategoryId of the CategoryFinder object

   *

   * @param  iObjectInstanceId  Description of the Parameter

   * @param  CreateNew          Description of the Parameter

   * @param  type               Description of the Parameter

   * @return                    The object instance category id value

   */

  public int getObjectInstanceCategoryId(int iObjectInstanceId, boolean CreateNew, String type) {

    int id = -1;

    try {

      ICObjectInstance obj = new ICObjectInstance(iObjectInstanceId);

      id = getObjectInstanceCategoryId(obj);

      if (id <= 0 && CreateNew) {

        id = CategoryBusiness.getInstance().createCategory(iObjectInstanceId, type);

      }

    } catch (Exception ex) {

      ex.printStackTrace();

    }

    return id;

  }





  /**

   *  Gets the objectInstanceCategoryIds of the CategoryFinder object

   *

   * @param  iObjectInstanceId  Description of the Parameter

   * @param  CreateNew          Description of the Parameter

   * @param  type               Description of the Parameter

   * @return                    The object instance category ids value

   */

  public int[] getObjectInstanceCategoryIds(int iObjectInstanceId, boolean CreateNew, String type) {

    int[] ids = new int[0];

    try {

      ids = getObjectInstanceCategoryIds(iObjectInstanceId);

      if (ids.length == 0 && CreateNew) {

        ids = new int[1];

        ids[0] = CategoryBusiness.getInstance().createCategory(iObjectInstanceId, type);

      }

    } catch (Exception ex) {

      ex.printStackTrace();

    }

    return ids;

  }





  /**

   *  Gets the objectInstanceCategoryId of the CategoryFinder object

   *

   * @param  iObjectInstanceId  Description of the Parameter

   * @return                    The object instance category id value

   */

  public int getObjectInstanceCategoryId(int iObjectInstanceId) {

    try {

      ICObjectInstance obj = new ICObjectInstance(iObjectInstanceId);

      return getObjectInstanceCategoryId(obj);

    } catch (Exception ex) {



    }

    return -1;

  }





  /**

   *  Gets the objectInstanceCategoryId of the CategoryFinder object

   *

   * @param  eObjectInstance  Description of the Parameter

   * @return                  The object instance category id value

   */

  public int getObjectInstanceCategoryId(ICObjectInstance eObjectInstance) {

    try {

      List L = EntityFinder.findRelated(eObjectInstance, (GenericEntity) ICCategory.getStaticInstance(ICCategory.class));

      if (L != null) {

        return ((GenericEntity) L.get(0)).getID();

      } else {

        return -1;

      }

    } catch (SQLException ex) {

      ex.printStackTrace();

      return -2;

    }

  }





  /**

   *  Gets the objectInstanceCategoryIds of the CategoryFinder object

   *

   * @param  ICObjectInstanceId  Description of the Parameter

   * @return                     The object instance category ids value

   */

  public int[] getObjectInstanceCategoryIds(int ICObjectInstanceId) {

    //select ic_category_id from IC_CATEGORY_ic_object_instance where ic_object_instance_ID=51

    StringBuffer sql = new StringBuffer("select ");

    ICCategory cat = (ICCategory) ICCategory.getStaticInstance(ICCategory.class);

    ICObjectInstance obj = (ICObjectInstance) ICObjectInstance.getStaticInstance(ICObjectInstance.class);

    sql.append(cat.getIDColumnName()).append(" from ");

    sql.append(EntityControl.getManyToManyRelationShipTableName(ICCategory.class, ICObjectInstance.class));

    sql.append(" where ").append(obj.getIDColumnName()).append(" = ").append(ICObjectInstanceId);

    try {

      String[] sids = com.idega.data.SimpleQuerier.executeStringQuery(sql.toString());

      if (sids != null && sids.length > 0) {

        int[] ids = new int[sids.length];

        for (int i = 0; i < sids.length; i++) {

          ids[i] = Integer.parseInt(sids[i]);

        }

        return ids;

      }

    } catch (Exception ex) {

      ex.printStackTrace();

    }

    return new int[0];

    /*

     *  try {

     *  EntityFinder.debug = true;

     *  List L = EntityFinder.findRelated(eObjectInstance ,(GenericEntity)ICCategory.getStaticInstance(ICCategory.class));

     *  EntityFinder.debug = false;

     *  if(L!= null){

     *  java.util.Iterator iter = L.iterator();

     *  int[] ids = new int[L.size()];

     *  for (int i = 0; i < ids.length; i++) {

     *  ids[i] = ((GenericEntity) L.get(0)).getID();

     *  }

     *  return ids;

     *  }

     *  else

     *  return new int[0];

     *  }

     *  catch (SQLException ex) {

     *  ex.printStackTrace();

     *  return new int[0];

     *  }

     */

  }





  /**

   *  @todo Description of the Method

   *

   * @param  instanceid  Description of the Parameter

   * @return             Description of the Return Value

   */

  public List listOfCategoryForObjectInstanceId(int instanceid) {

    try {

      ICObjectInstance obj = ICObjectBusiness.getInstance().getICObjectInstance(instanceid);

      return listOfCategoryForObjectInstanceId(obj);

    } catch (Exception ex) {

      return null;

    }

  }





  /**

   *  @todo Description of the Method

   *

   * @param  obj  Description of the Parameter

   * @return      Description of the Return Value

   */

  public List listOfCategoryForObjectInstanceId(ICObjectInstance obj) {

    try {

      List L = EntityFinder.getInstance().findRelated(obj,(ICCategory) ICCategory.getStaticInstance(ICCategory.class));

      return L;

    } catch (SQLException ex) {

      return null;

    }

  }





  /**

   *  Gets the relatedSQL of the CategoryFinder object

   *

   * @param  iObjectInstanceId  Description of the Parameter

   * @return                    The related SQL value

   */

  private String getRelatedSQL(int iObjectInstanceId) {

    StringBuffer sql = new StringBuffer("select ");

    sql.append(((ICCategory) ICCategory.getStaticInstance(ICCategory.class)).getIDColumnName());

    sql.append(" from ").append(com.idega.data.EntityControl.getManyToManyRelationShipTableName(ICCategory.class, ICObjectInstance.class));

    sql.append(" where ").append(((ICObjectInstance) ICObjectInstance.getStaticInstance(ICObjectInstance.class)).getIDColumnName());

    sql.append(" = ").append(iObjectInstanceId);

    return sql.toString();

  }





  /**

   *  Gets the relatedEntitySQL of the CategoryFinder object

   *

   * @param  tablename          Description of the Parameter

   * @param  iObjectInstanceId  Description of the Parameter

   * @return                    The related entity SQL value

   */

  private String getRelatedEntitySQL(String tablename, int iObjectInstanceId) {

    StringBuffer sql = new StringBuffer("select ");

    sql.append(tablename).append(".* from ").append(tablename).append(",");

    String middletable = EntityControl.getManyToManyRelationShipTableName(ICCategory.class, ICObjectInstance.class);

    sql.append(middletable);

    sql.append(" where ").append(((ICObjectInstance) ICObjectInstance.getStaticInstance(ICObjectInstance.class)).getIDColumnName());

    sql.append(" = ").append(iObjectInstanceId);

    String idname = ((ICCategory) ICCategory.getStaticInstance(ICCategory.class)).getIDColumnName();

    sql.append(" and ").append(middletable).append(".").append(idname);

    sql.append(" = ").append(tablename).append(".").append(idname);

    return sql.toString();

  }





  /**

   *  Gets the relatedEntitySql of the CategoryFinder object

   *

   * @param  EntityClass          Description of the Parameter

   * @param  CategoryEntityClass  Description of the Parameter

   * @param  EntityColumn         Description of the Parameter

   * @param  iObjectInstanceId    Description of the Parameter

   * @return                      The related entity sql value

   */

  private String getRelatedEntitySql(Class EntityClass, Class CategoryEntityClass, String EntityColumn, int iObjectInstanceId) {

    GenericEntity entity = GenericEntity.getStaticInstance(EntityClass);

    CategoryEntity catEntity = (CategoryEntity) CategoryEntity.getStaticInstance(CategoryEntityClass);

    ICObjectInstance instanceEntity = (ICObjectInstance) ICObjectInstance.getStaticInstance(ICObjectInstance.class);

    ICCategory icCat = ((ICCategory) ICCategory.getStaticInstance(ICCategory.class));

    String middletable = EntityControl.getManyToManyRelationShipTableName(ICCategory.class, ICObjectInstance.class);

    String tablename = entity.getEntityName();



    StringBuffer sql = new StringBuffer("select ");

    sql.append(entity.getEntityName()).append(".* from ").append(entity.getEntityName()).append(",");

    sql.append(catEntity.getEntityName()).append(",");

    sql.append(middletable);

    sql.append(" where ").append(instanceEntity.getIDColumnName());

    sql.append(" = ").append(iObjectInstanceId);

    sql.append(" and ").append(middletable).append(".").append(icCat.getIDColumnName());

    sql.append(" = ").append(catEntity.getEntityName()).append(".").append(CategoryEntity.getColumnCategoryId());

    sql.append(" and ").append(catEntity.getEntityName()).append(".").append(catEntity.getIDColumnName());

    sql.append(" = ").append(entity.getEntityName()).append(".").append(EntityColumn);



    //System.err.println(sql.toString());

    return sql.toString();

  }





  /**

   *  Returns a Collection of ICCategory entities with specified type

   *

   * @param  ids   Description of the Parameter

   * @param  type  Description of the Parameter

   * @return       The categories value

   */

  public Collection getCategories(int[] ids, String type) {

    StringBuffer sql = new StringBuffer("select * from ");

    ICCategory cat = (ICCategory) ICCategory.getStaticInstance(ICCategory.class);

    sql.append(ICCategory.getEntityTableName());

    sql.append(" where ").append(ICCategory.getColumnType()).append(" = ").append(type);

    sql.append(" and ").append(cat.getIDColumnName()).append(" in (");

    for (int i = 0; i < ids.length; i++) {

      if (i > 0) {

        sql.append(",");

      }

      sql.append(ids[i]);

    }

    sql.append(" )");

    try {

      return EntityFinder.getInstance().findAll(ICCategory.class, sql.toString());

    } catch (IDOFinderException ex) {



    }

    return null;

  }





  /**

   *  Returns a Collection of ICCategory-ids that have reference to a

   *  ICObjectInstance

   *

   * @param  iObjectInstanceId  Description of the Parameter

   * @return                    Description of the Return Value

   */

  public Collection collectCategoryIntegerIds(int iObjectInstanceId) {

    String[] ids = null;



    try {

      String sql = getRelatedSQL(iObjectInstanceId);

      ids = com.idega.data.SimpleQuerier.executeStringQuery(sql);

      if (ids != null) {

        HashSet H = new HashSet();

        Integer I;

        for (int i = 0; i < ids.length; i++) {

          I = new Integer(ids[i]);

          if (!H.contains(I)) {

            H.add(I);

          }

        }

        return H;

      }

    } catch (Exception ex) {

      ex.printStackTrace();

    }



    return null;

  }





  /**

   *  @todo Description of the Method

   *

   * @param  categoryEntityClass  Description of the Parameter

   * @param  iCategoryId          Description of the Parameter

   * @return                      Description of the Return Value

   */

  public Collection listOfCategoryEntity(Class categoryEntityClass, int iCategoryId) {

    if (categoryEntityClass.getSuperclass().equals(CategoryEntity.class)) {

      try {

        return EntityFinder.getInstance().findAllByColumn(categoryEntityClass, CategoryEntity.getColumnCategoryId(), iCategoryId);

      } catch (IDOFinderException ex) {



      }

    }

    return null;

  }





  /*

   *  public Collection listOfCategoryEntity(Class categoryEntityClass,int[] iCategoryIds){

   *  if(categoryEntityClass.getSuperclass().equals(CategoryEntity.class)){

   *  return EntityFinder.getInstance().findAllByColumn(categoryEntityClass,CategoryEntity.getColumnCategoryId(),iCategoryId);

   *  }

   *  else return null;

   *  }

   */

  /**

   *  @todo Description of the Method

   *

   * @param  categoryEntityClass  Description of the Parameter

   * @param  ObjectInstanceId     Description of the Parameter

   * @return                      Description of the Return Value

   */

  public List listOfCategoryEntityByInstanceId(Class categoryEntityClass, int ObjectInstanceId) {

    if (categoryEntityClass.getSuperclass().equals(CategoryEntity.class)) {



      try {

        String entityName = ((CategoryEntity) categoryEntityClass.newInstance()).getEntityName();

        return EntityFinder.getInstance().findAll(categoryEntityClass, getRelatedEntitySQL(entityName, ObjectInstanceId));

      } catch (IDOFinderException ex) {



      } catch (Exception ex) {



      }

    }

    return null;

  }





  /**

   *  Gets the categoryRelatedEntityFromInstanceId of the CategoryFinder object

   *

   * @param  CategoryEntityClass  Description of the Parameter

   * @param  EntityClass          Description of the Parameter

   * @param  EntityColumn         Description of the Parameter

   * @param  ObjectInstanceId     Description of the Parameter

   * @return                      The category related entity from instance id

   *      value

   */

  public Collection getCategoryRelatedEntityFromInstanceId(Class CategoryEntityClass, Class EntityClass, String EntityColumn, int ObjectInstanceId) {

    if (CategoryEntityClass.getSuperclass().equals(CategoryEntity.class)) {

      try {

        return EntityFinder.getInstance().findAll(EntityClass, getRelatedEntitySql(EntityClass, CategoryEntityClass, EntityColumn, ObjectInstanceId));

      } catch (IDOFinderException ex) {



      } catch (Exception ex) {



      }

    }

    return null;

  }





  /**

   *  Gets the instance of the CategoryFinder class

   *

   * @return    The instance value

   */

  public static CategoryFinder getInstance() {

    if (categoryFinder == null) {

      categoryFinder = new CategoryFinder();

    }

    return categoryFinder;

  }





  /**

   *  Gets the mapOfCategoriesById of the CategoryFinder object

   *

   * @param  c  Description of the Parameter

   * @return    The map of categories by id value

   */

  public Map getMapOfCategoriesById(int instanceId) {

    return EntityFinder.getInstance().getMapOfEntity(listOfCategoryForObjectInstanceId(instanceId), ICCategory.getStaticInstance(ICCategory.class).getIDColumnName());

  }



  public String getInstanceManyToManyRelationShipName(){

    return EntityControl.getManyToManyRelationShipTableName(ICCategory.class,ICObjectInstance.class);

  }

}

