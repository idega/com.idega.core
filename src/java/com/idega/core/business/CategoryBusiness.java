package com.idega.core.business;



import com.idega.data.IDOLookup;
import com.idega.core.data.*;
import java.sql.*;




import com.idega.util.IWTimestamp;



import java.util.List;

import java.util.Vector;

import java.util.Iterator;


import com.idega.core.business.ICObjectBusiness;



public class CategoryBusiness{



  private static CategoryBusiness categoryBusiness;



  public static CategoryBusiness getInstance(){

    if(categoryBusiness==null)

      categoryBusiness = new CategoryBusiness();

    return categoryBusiness;

  }



  public boolean disconnectBlock(int instanceid){

    List L = CategoryFinder.getInstance().listOfCategoryForObjectInstanceId(instanceid);

    if(L!= null){

      Iterator I = L.iterator();

      while(I.hasNext()){

        ICCategory Cat = (ICCategory) I.next();

        disconnectCategory(Cat,instanceid);

      }

      return true;

    }

    else

      return false;



  }



  public boolean disconnectCategory(ICCategory Cat,int iObjectInstanceId){

    try {

      if(iObjectInstanceId > 0  ){

        ICObjectInstance obj = ((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(iObjectInstanceId);

        Cat.removeFrom(obj);

      }

      return true;

    }

    catch (SQLException ex) {



    }

    return false;

  }



  /**

   *  removes all categories bound to specified instance

   */

  public boolean removeInstanceCategories(int iObjectInstanceId){

    try {

      ICObjectInstance obj =  ICObjectBusiness.getInstance().getICObjectInstance(iObjectInstanceId);

      obj.removeFrom(ICCategory.class);

    }

    catch (Exception ex) {



    }

    return false;



  }





  public boolean deleteBlock(int instanceid){

    List L = CategoryFinder.getInstance().listOfCategoryForObjectInstanceId(instanceid);

    if(L!= null){

      Iterator I = L.iterator();

      while(I.hasNext()){

        ICCategory N = (ICCategory) I.next();

        try{

          deleteCategory(N.getID(),instanceid );

        }

        catch(SQLException sql){



        }

      }

      return true;

    }

    else

      return false;

  }



  public void deleteCategory(int iCategoryId) throws SQLException{

    deleteCategory(iCategoryId ,CategoryFinder.getInstance().getObjectInstanceIdFromCategoryId(iCategoryId));

  }



  public void deleteCategory(int iCategoryId ,int iObjectInstanceId) throws SQLException {

    ICCategory nc = (ICCategory) CategoryFinder.getInstance().getCategory( iCategoryId );

    if(iObjectInstanceId > 0  ){

      ICObjectInstance obj = ((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(iObjectInstanceId);
      nc.removeFrom(obj);

    }

    nc.delete();



  }



  public void saveRelatedCategories(int iObjectInstanceId,int[] CategoryIds){

    try {
      ICObjectInstance instance = ((ICObjectInstanceHome) IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(iObjectInstanceId);
      //ICCategoryICObjectInstanceHome catObjInstHome = (ICCategoryICObjectInstanceHome) IDOLookup.getHome(ICCategoryICObjectInstance.class);
      ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHomeLegacy(ICCategory.class);

      Category category;
//      int tree_order = 0;

      int treeOrder[] = new int[CategoryIds.length];
      for (int i = 0; i < CategoryIds.length; i++) {
        category = catHome.findByPrimaryKeyLegacy(CategoryIds[i]);
        treeOrder[i] = catHome.getOrderNumber(category, instance);
//        tree_order = 1;//catObjInstHome.getOrderNumber(category, instance);
      }

      instance.removeFrom(ICCategory.class);
//      com.idega.core.data.ICObjectInstanceBMPBean.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom((ICCategory) category);
      for (int i = 0; i < CategoryIds.length; i++) {
        category = catHome.findByPrimaryKeyLegacy(CategoryIds[i]);
        //com.idega.core.data.ICObjectInstanceBMPBean.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom(ICCategory.class, CategoryIds[i]);
        //com.idega.core.data.ICObjectInstanceBMPBean.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom((ICCategory) category);
        instance.addTo(ICCategory.class,CategoryIds[i]);
        catHome.setOrderNumber(category, instance, treeOrder[i]);

      }

    }

    catch (Exception ex) {

      ex.printStackTrace();

    }

  }

  public ICCategory saveCategory(int iCategoryId,String sName,String sDesc,int iObjectInstanceId,String type,boolean allowMultible){
    return saveCategory(iCategoryId, sName, sDesc, 0,iObjectInstanceId, type, allowMultible);
  }
  public ICCategory saveCategory(int iCategoryId,String sName,String sDesc,int orderNumber, int iObjectInstanceId,String type,boolean allowMultible){

    ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHomeLegacy(ICCategory.class);
    ICCategory Cat = catHome.createLegacy();
    if(iCategoryId > 0)

      Cat = CategoryFinder.getInstance().getCategory(iCategoryId);

    Cat.setName(sName);

    Cat.setDescription(sDesc);

    Cat.setType(type);


    if (orderNumber == 0) {
      return saveCategory(Cat,iObjectInstanceId,allowMultible);
    }else {
      return saveCategory(Cat,iObjectInstanceId,orderNumber, allowMultible);
    }

  }

  public boolean updateCategory(int id,String name,String info){
    return updateCategory(id, name, info, 0, -1);
  }

  public boolean updateCategory(int id,String name,String info, int orderNumber, int objectInstanceId){

    try {
      ICCategoryHome catHome = (ICCategoryHome) IDOLookup.getHome(ICCategory.class);
      ICCategory cat = catHome.findByPrimaryKey(id);
      cat.setName(name);
      cat.setDescription(info);
      cat.update();

      if (objectInstanceId > 0) {
        ICObjectInstanceHome objInsHome = (ICObjectInstanceHome) IDOLookup.getHome(ICObjectInstance.class);
        ICObjectInstance objIns = objInsHome.findByPrimaryKey(objectInstanceId);
        //ICCategoryICObjectInstanceHome catObjInsHome = ( ICCategoryICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICCategoryICObjectInstance.class);
        //catObjInsHome.setOrderNumber(cat, objIns, orderNumber);
        catHome.setOrderNumber(cat, objIns, orderNumber);
      }

      return true;

    }

    catch (Exception ex) {

      ex.printStackTrace();

    }

    return false;

  }



  public ICCategory saveCategory(int iCategoryId,String sName,String sDesc,int iObjectInstanceId,String type){

    return saveCategory(iCategoryId,sName,sDesc,iObjectInstanceId,type,false);

  }

  public ICCategory saveCategory(ICCategory Cat,int iObjectInstanceId,boolean allowMultible){
    return saveCategory(Cat, iObjectInstanceId, 0, allowMultible);
  }

  public ICCategory saveCategory(ICCategory Cat,int iObjectInstanceId,int orderNumber, boolean allowMultible){

    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();

    try{

     t.begin();

      if(Cat.getID()>0){

        Cat.update();

      }

      else{

        Cat.setCreated(IWTimestamp.getTimestampRightNow());

        Cat.setValid(true);

        Cat.insert();

      }

      // Binding category to instanceId

      if(iObjectInstanceId > 0){

        //ICCategoryICObjectInstanceHome catObjInstHome = ((com.idega.core.data.ICCategoryICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICCategoryICObjectInstance.class));
        ICObjectInstance objIns = ((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(iObjectInstanceId);
        //catObjInstHome.setOrderNumber(Cat, objIns, orderNumber);

        // Allows only one category per instanceId

        if(!allowMultible)

          objIns.removeFrom((ICCategory)com.idega.core.data.ICCategoryBMPBean.getEntityInstance(ICCategory.class));

        Cat.addTo(objIns,"TREE_ORDER",String.valueOf(orderNumber));

      }

      t.commit();

      return Cat;

    }

    catch(Exception e) {

      try {

        t.rollback();

      }

      catch(javax.transaction.SystemException ex) {

        ex.printStackTrace();

      }

      e.printStackTrace();

    }

    return null;

  }





  public int createCategory(int iObjectInstanceId,String type){

    return saveCategory(-1,"Category - "+iObjectInstanceId,"Category - "+iObjectInstanceId,iObjectInstanceId ,type,false).getID();

  }



}
