package com.idega.idegaweb.block.business;



import com.idega.core.data.ICInformationCategory;

import com.idega.core.data.ICInformationFolder;

import com.idega.core.data.ICObjectInstance;

import com.idega.data.EntityFinder;

import com.idega.util.IWTimeStamp;

import java.sql.SQLException;

import java.util.List;

import java.util.Iterator;



/**

 * <p>Title: idegaWeb</p>

 * <p>Description: </p>

 * <p>Copyright: Copyright (c) 2001</p>

 * <p>Company: idega</p>

 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>

 * @version 1.0

 */



public class FolderBlockBusiness {



  public FolderBlockBusiness() {

  }



  public static FolderBlockBusiness getInstance(){

    return new FolderBlockBusiness();

  }





  public ICInformationFolder getInstanceWorkeFolder(int icObjectInstanceId, int icObjectId, int localeId, boolean autocreate){

    ICInformationFolder parentFolder = null;

    try {

      List l =EntityFinder.findRelated(((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceId),com.idega.core.data.ICInformationFolderBMPBean.getStaticInstance(ICInformationFolder.class));

      if(l != null && l.size() > 0){

        parentFolder = (ICInformationFolder)l.get(0);

      } else if(autocreate){

        ICInformationFolder folder = createICInformationFolder("folder - "+icObjectInstanceId,null,null,icObjectId,-1,icObjectInstanceId);

        parentFolder = folder;

      }

    }

    catch (SQLException ex) {

      ex.printStackTrace();

    }



    if(parentFolder != null){

      try {

        List l = EntityFinder.findAllByColumn(com.idega.core.data.ICInformationFolderBMPBean.getStaticInstance(ICInformationFolder.class),com.idega.core.data.ICInformationFolderBMPBean.getColumnParentFolderId(),parentFolder.getID(),com.idega.core.data.ICInformationFolderBMPBean.getColumnLocaleId(),localeId);

        if(l != null && l.size() > 0){

          return (ICInformationFolder)l.get(0);

        } else {  // autocreates locleFolders

          ICInformationFolder folder = createICInformationFolderForLocale("localefolder - "+icObjectInstanceId,icObjectId,parentFolder.getID(),localeId);

          return folder;

        }

      }

      catch (SQLException ex) {

        ex.printStackTrace();

      }

    }

    return null;

  }



  public List getInstanceViewFolders(int icObjectInstanceId){

    throw new RuntimeException("getInstanceViewFolders(int icObjectInstanceId) not Implemented");

  }



  public List getInstanceCategories(int icObjectInstanceId){

     try {

      List l =EntityFinder.findRelated(((com.idega.core.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceId),com.idega.core.data.ICInformationCategoryBMPBean.getStaticInstance(ICInformationCategory.class));

      return l;

    }

    catch (SQLException ex) {

      ex.printStackTrace();

    }

    return null;

  }





  public ICInformationFolder createICInformationFolder(String name, String description, String type, int ICObjectId, int ownerGroup, int relatedInstanceId) throws SQLException{

    return createICInformationFolder(name, description, type, ICObjectId, -1, -1, ownerGroup, relatedInstanceId);

  }



  public ICInformationFolder createICInformationFolderForLocale(String name, int ICObjectId, int parentId, int localeId) throws SQLException{

    return createICInformationFolder(name, null, null, ICObjectId, parentId, localeId, -1, -1);

  }



  private ICInformationFolder createICInformationFolder(String name, String description, String type, int ICObjectId, int parentId, int localeId, int ownerGroup, int relatedInstanceId) throws SQLException{

    ICInformationFolder folder = ((com.idega.core.data.ICInformationFolderHome)com.idega.data.IDOLookup.getHomeLegacy(ICInformationFolder.class)).createLegacy();





    if( name != null){

      folder.setName(name);

    }

    if( description != null){

      folder.setDescription(description);

    }

    if( ownerGroup != -1){

      folder.setOwnerGroupID(ownerGroup);

    }

    if( type != null){

      folder.setType(type);

    }



    if( parentId != -1){

      folder.setParentId(parentId);

    }



    if( localeId != -1){

      folder.setLocaleId(localeId);

    }



    folder.setICObjectId(ICObjectId);



    folder.setValid(true);

    folder.setCreated(IWTimeStamp.getTimestampRightNow());



    folder.insert();



    if(relatedInstanceId != -1){

      folder.addTo(ICObjectInstance.class,relatedInstanceId);

    }



    return folder;



  }



  public void addCategoryToInstance(ICInformationCategory cat, int instanceId) throws SQLException {

    cat.addTo(ICObjectInstance.class,instanceId);

  }



  public void removeCategoryFromInstance(ICInformationCategory cat, int instanceId) throws SQLException {

    cat.removeFrom(ICObjectInstance.class,instanceId);

  }



  public void addCategoryToInstance(ICObjectInstance inst, int catId) throws SQLException {

    inst.addTo(ICInformationCategory.class,catId);

  }



  public void removeCategoryFromInstance(ICObjectInstance inst, int catId) throws SQLException {

    inst.removeFrom(ICInformationCategory.class,catId);

  }







  public ICInformationCategory createICInformationCategory(String name, String description, String type, int ICObjectId, int ownerFolder) throws SQLException{

    ICInformationCategory cat = ((com.idega.core.data.ICInformationCategoryHome)com.idega.data.IDOLookup.getHomeLegacy(ICInformationCategory.class)).createLegacy();





    if( name != null){

      cat.setName(name);

    }

    if( description != null){

      cat.setDescription(description);

    }

    if( ownerFolder != -1){

      cat.setFolderSpecific(ownerFolder);

    }

    if( type != null){

      cat.setType(type);

    }



    cat.setICObjectId(ICObjectId);



    cat.setValid(true);

    cat.setCreated(IWTimeStamp.getTimestampRightNow());



    cat.insert();



    return cat;



  }









  public ICInformationCategory[] getAvailableCategories( int icObjectId, int workingFolderId ){

    return null;

  }



  public boolean copyCategoryAttachments(int instanceFrom, int instanceTo){

    List infoCategories = this.getInstanceCategories(instanceFrom);

    boolean toReturn = true;

    if(infoCategories != null){

      Iterator iter = infoCategories.iterator();

      while (iter.hasNext()) {

        ICInformationCategory item = (ICInformationCategory)iter.next();

        try {

          item.addTo(ICObjectInstance.class,instanceTo);

        }

        catch (SQLException ex) {

          toReturn = false;

        }

      }

    }

    return toReturn;

  }



}
