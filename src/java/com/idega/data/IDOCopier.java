package com.idega.data;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

 import java.util.*;
 import java.sql.*;

public class IDOCopier {

  private GenericEntity fromEntity;
  private String toDataSourceName;
  private GenericEntity toEntity;
  private List entityToCopyList;

  private List entityRelationshipInfos= new Vector();
  private List copiedEntityClasses=new Vector();

  protected IDOCopier(){
  }

  private IDOCopier(GenericEntity entity) {
    this.fromEntity=entity;
    addEntityToCopy(entity);
  }

  public static IDOCopier getCopierInstance(){
    return new IDOCopier();
  }

  public static IDOCopier getCopierInstance(GenericEntity entity){
    return new IDOCopier(entity);
  }

  public void setToDatasource(String dataSourceName){
    this.toDataSourceName=dataSourceName;
  }

  public void addEntityToCopy(GenericEntity entity){
    if(fromEntity==null){
      fromEntity=entity;
    }
    getEntityCopyList().add(entity);
  }

  private List getEntityCopyList(){
    if(entityToCopyList==null){
      entityToCopyList = new Vector();
    }
    return entityToCopyList;
  }

  public String getToDatasource(){
    return toDataSourceName;
  }

  public void copyAllData(){
    try{
      toEntity = (GenericEntity)fromEntity.getClass().newInstance();
      toEntity.setDatasource(getToDatasource());
      toEntity.setToInsertStartData(false);

    IDODependencyList dep = IDODependencyList.createDependencyList();
    dep.addAllEntityClasses(this.getEntityCopyList());
    dep.compile();

    List depList = dep.getDependencyListAsClasses();
    //List depList = compileDependencyList(com.idega.core.data.ICFile.class);
    //List depList = compileDependencyList(com.idega.core.user.data.User.class);

    Iterator iter = com.idega.util.ListUtil.reverseList(depList).iterator();
    while (iter.hasNext()) {
      Class item = (Class)iter.next();
    	//out.println(item.getName()+"\n<br>");
        try{
          GenericEntity toInstance = (GenericEntity)item.newInstance();
          toInstance.setDatasource(this.getToDatasource());
          toInstance.setToInsertStartData(false);

          GenericEntity fromInstance = (GenericEntity)item.newInstance();
          fromInstance.setDatasource(this.fromEntity.getDatasource());

          DatastoreInterface.getInstance(toInstance).createEntityRecord(toInstance);

          copyAllData(fromInstance,toInstance,true);
          copyManyToManyData(fromInstance,toInstance);
        }
        catch(java.lang.IllegalAccessException illae){
          //illae.printStackTrace();
        }
    }


    }
    catch(Exception e){
      e.printStackTrace();
    }
  }



  private void copyAllData(GenericEntity fromInstance,GenericEntity toInstance,boolean maintainIDs){
    try{
      System.out.println("[idoCopier] Copying data for "+fromInstance.getClass().getName());
      List l = null;

      if(fromInstance instanceof com.idega.builder.data.IBPage){
       /**
       *  @todo change - Shitty mix change this as soon as possible!!!
       *
       */
       List l1 = EntityFinder.getInstance().findAll(fromInstance,"select * from ib_page where template_id is null");
       l = new Vector();
       l.addAll(l1);
       List l2 = EntityFinder.getInstance().findAll(fromInstance,"select * from ib_page where template_id is not null order by template_id asc,ib_page_id asc");
       l.addAll(l2);
      }
      else{
        l = EntityFinder.getInstance().findAll(fromInstance);
      }

      if(l!=null){
        int highestID=1;
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
          GenericEntity tempEntity = (GenericEntity)iter.next();
          String originalDatasource = tempEntity.getDatasource();
          tempEntity.setDatasource(this.getToDatasource());
          if(!maintainIDs){
            /**
             * @todo: Implement
             */
          }
          try{
            tempEntity.insert();
          }
          catch(SQLException e){
            System.err.println("Error in inserting "+tempEntity.getClass().getName()+" for id="+tempEntity.getID()+" Message: "+e.getMessage());
          }
          try{
            if(tempEntity.getID()>highestID){
              highestID=tempEntity.getID();
            //  DatastoreInterface.getInstance(tempEntity).setSequenceValue(tempEntity,highestID);
            }
          }
          catch(ClassCastException e){
            //e.printStackTrace();
            System.err.println("ClassCastException: "+e.getMessage());
          }
          tempEntity.setDatasource(originalDatasource);
        }
        updateNumberGeneratorValue(toInstance,highestID);
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


  private void copyManyToManyData(GenericEntity fromInstance,GenericEntity toInstance){
      addToCopiedEntityList(fromInstance);
      List infoList = getManyToManyRelatedAndCopied(fromInstance);
      Iterator iter = infoList.iterator();
      while (iter.hasNext()) {
        IDOEntityRelationshipCopyInfo info = (IDOEntityRelationshipCopyInfo)iter.next();
        if(!info.copied){
          String crossTableName = info.relation.getTableName();
          Connection toConn=null;
          Connection fromConn=null;
          try{
            toConn = this.toEntity.getConnection();
            fromConn = fromEntity.getConnection();
            this.copyManyToManyData(crossTableName,fromConn,toConn);
          }
          catch(SQLException e){
            e.printStackTrace();
          }
          finally{
            if(toConn!=null){
              toEntity.freeConnection(toConn);
            }
            if(fromConn!=null){
              fromEntity.freeConnection(fromConn);
            }
          }
          info.copied=true;
        }
      }
  }



  private void addToCopiedEntityList(GenericEntity entity){
    copiedEntityClasses.add(entity.getClass());
    List relations = EntityControl.getManyToManyRelationShips(entity);
    if(relations!=null){
      Iterator iter = relations.iterator();
      while (iter.hasNext()) {
        boolean areAllReferencedEntitiesCopied = true;
        EntityRelationship item = (EntityRelationship)iter.next();
        Collection referencedClasses = item.getColumnsAndReferencingClasses().values();
        Iterator classIter = referencedClasses.iterator();
        while (classIter.hasNext()) {
          Class classToCheck = (Class)classIter.next();
          if(copiedEntityClasses.contains(classToCheck)&&areAllReferencedEntitiesCopied){
            //areAllReferencedEntitiesCopied still kept true
            areAllReferencedEntitiesCopied=true;
          }
          else{
            areAllReferencedEntitiesCopied=false;
          }
        }

        if(areAllReferencedEntitiesCopied){
          IDOEntityRelationshipCopyInfo info = new IDOEntityRelationshipCopyInfo();
          info.relation=item;
          if(!entityRelationshipInfos.contains(info)){
            entityRelationshipInfos.add(info);
          }
        }
      }
    }
  }

  /**
   *   Returns a List of IDOEntityRelationshipCopyInfo Objects
   */
  private List getManyToManyRelatedAndCopied(GenericEntity entity){
    return entityRelationshipInfos;
  }


  private void copyManyToManyData(String crossTableName,Connection fromConnection,Connection toConnection){
    System.out.println("[idoCopier] Copying data for cross-table: "+crossTableName);
    Statement stmt=null;
    PreparedStatement ps=null;
    ResultSet RS=null;
    try{
      stmt = fromConnection.createStatement();
      ps = toConnection.prepareStatement("insert into "+crossTableName+" values(?,?)");
      RS = stmt.executeQuery("select * from "+crossTableName);
      ResultSetMetaData rsm = RS.getMetaData();
      int columnCount = rsm.getColumnCount();

      while (RS.next()) {
        try{
          int i1 = RS.getInt(1);
          int i2 = RS.getInt(2);
          ps.setInt(1,i1);
          ps.setInt(2,i2);
          ps.executeUpdate();
        }
        catch(SQLException e){
          //e.printStackTrace();
          System.err.println(e.getMessage());
        }
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    finally{
      if(RS!=null){
        try{
          RS.close();
        }
        catch(SQLException e){
          e.printStackTrace();
        }
      }
      if(stmt!=null){
        try{
          stmt.close();
        }
        catch(SQLException e){
          e.printStackTrace();
        }
      }
      if(ps!=null){
        try{
          ps.close();
        }
        catch(SQLException e){
          e.printStackTrace();
        }

      }
    }

  }



  private class IDOEntityRelationshipCopyInfo{

    EntityRelationship relation;
    boolean copied = false;


    public boolean equals(Object o){
      if(o!=null){
        if(o instanceof IDOEntityRelationshipCopyInfo){
          return ((IDOEntityRelationshipCopyInfo)o).relation.equals(this.relation);
        }
      }
      return false;
    }

  }


  private void updateNumberGeneratorValue(GenericEntity entity, int highestValue){
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    int valueToSet = highestValue;
    try{
      conn = entity.getConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery("select max("+entity.getIDColumnName()+") from "+entity.getTableName());
      rs.next();
      int i = rs.getInt(1);
      if(i>valueToSet){
        valueToSet = i;
      }
      rs.close();
      stmt.close();
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    finally{
      if(conn!=null){
        entity.freeConnection(conn);
      }
    }
    DatastoreInterface.getInstance(entity).setNumberGeneratorValue(entity,valueToSet);
  }

}