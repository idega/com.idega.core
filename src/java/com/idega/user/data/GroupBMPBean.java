package com.idega.user.data;


import com.idega.builder.data.IBDomain;
import com.idega.data.*;
import com.idega.core.data.*;

import java.sql.*;
import java.util.Vector;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.Enumeration;
import com.idega.util.datastructures.idegaTreeNode;
import com.idega.core.ICTreeNode;


/**

 * Title:        IW Core

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega.is

 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>

 * @version 1.0

 */



public class GroupBMPBean extends com.idega.core.data.GenericGroupBMPBean implements Group,ICTreeNode {


    public static final int GROUP_ID_EVERYONE = -7913;
    public static final int GROUP_ID_USERS = - 1906;

    private static final String RELATION_TYPE_GROUP_CHILD="GROUP_CHILD";


  public void initializeAttributes(){
    addAttribute(getIDColumnName());
    addAttribute(getNameColumnName(),"Group name", true, true, "java.lang.String");
    addAttribute(getGroupTypeColumnName(),"Group type", true, true,String.class,30,"one-to-many",GroupType.class);
    addAttribute(getGroupDescriptionColumnName(),"Description", true, true, "java.lang.String");
    addAttribute(getExtraInfoColumnName(),"Extra information", true, true, "java.lang.String");
    this.addManyToManyRelationShip(ICNetwork.class,"ic_group_network");
    this.addManyToManyRelationShip(ICProtocol.class,"ic_group_protocol");
  }



  public String getEntityName(){
    return "ic_group";
  }



//        public String getNameOfMiddleTable(IDOLegacyEntity entity1,IDOLegacyEntity entity2){
//            return "ic_group_user";
//        }

  public void setDefaultValues(){
    setGroupType(getGroupTypeValue());
  }

  /**
   * overwrite in extended classes
   */
  public String getGroupTypeValue(){
    return "general";
  }

  /*  ColumNames begin   */



  public static String getColumnNameGroupID(){
    return "ic_group_id";
  }


  public static String getNameColumnName(){
    return "name";
  }

  public static String getGroupTypeColumnName(){
    return "group_type";
  }

  public static String getGroupDescriptionColumnName(){
    return "description";
  }

  public static String getExtraInfoColumnName(){
    return "extra_info";
  }

  /*  ColumNames end   */




  /*  functions begin   */


  public String getName(){
          return (String) getColumnValue(getNameColumnName());
  }

  public void setName(String name){
          setColumn(getNameColumnName(),name);
  }

  public String getGroupType(){
          return (String) getColumnValue(getGroupTypeColumnName());
  }

  public void setGroupType(String groupType){
          setColumn(getGroupTypeColumnName(), groupType);
  }

  public String getDescription(){
          return (String) getColumnValue(getGroupDescriptionColumnName());
  }

  public void setDescription(String description){
          setColumn(getGroupDescriptionColumnName(),description);
  }

  public String getExtraInfo(){
          return (String) getColumnValue(getExtraInfoColumnName());
  }

  public void setExtraInfo(String extraInfo){
          setColumn(getExtraInfoColumnName(),extraInfo);
  }



//        public static Group getStaticInstance(){
//
//          return (Group)getStaticInstance(Group.class);
//
//        }





  //??

//        public Group[] getAllGroupsContainingThis()throws SQLException{
//
//          List vector = this.getListOfAllGroupsContainingThis();
//
//          if(vector != null){
//
//            return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
//
//          }else{
//
//            return new Group[0];
//
//          }
//
//        }



  public List getListOfAllGroupsContainingThis()throws EJBException{
    List theReturn = new Vector();
    try{
      Collection relations = this.getGroupRelationHome().findGroupsRelationshipsContaining(this);
      Iterator iter = relations.iterator();
      while (iter.hasNext()) {
        GroupRelation item = (GroupRelation)iter.next();
        Group parent = item.getGroup();
//              if(!theReturn.contains(parent)){
          theReturn.add(parent);
//              }
      }
    }
    catch(Exception e){
      throw new EJBException(e.getMessage());
    }
    return theReturn;
  }


  /**
   * @deprecated
   */
  public List getListOfAllGroupsContaining(int group_id)throws EJBException{
    try{
      Group group = this.getGroupHome().findByPrimaryKey(new Integer(group_id));
      return group.getListOfAllGroupsContainingThis();
    }
    catch(Exception e){
      throw new EJBException(e.getMessage());
    }
  }

  public Integer ejbCreateGroup() throws CreateException {
    return (Integer)this.ejbCreate();
  }

  public void ejbPostCreateGroup() throws CreateException {
  }

  public Integer ejbFindGroupByPrimaryKey(Object pk) throws FinderException {
    return (Integer)this.ejbFindByPrimaryKey(pk);
  }

//        private List getListOfAllGroupsContainingLegacy(int group_id)throws EJBException{
//          String tableToSelectFrom = "IC_GROUP_TREE";
//          StringBuffer buffer=new StringBuffer();
//          buffer.append("select * from ");
//          buffer.append(tableToSelectFrom);
//          buffer.append(" where ");
//          buffer.append("CHILD_IC_GROUP_ID");
//          buffer.append("=");
//          buffer.append(group_id);
//          String SQLString=buffer.toString();
//          Connection conn= null;
//          Statement Stmt= null;
//          Vector vector = new Vector();
//          try
//          {
//            conn = getConnection(getDatasource());
//            Stmt = conn.createStatement();
//            ResultSet RS = Stmt.executeQuery(SQLString);
//            while (RS.next()){
//              IDOLegacyEntity tempobj=null;
//              try{
//                tempobj = (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance();
//                tempobj.findByPrimaryKey(RS.getInt(this.getIDColumnName()));
//              }
//              catch(Exception ex){
//                System.err.println("There was an error in " + this.getClass().getName() +".getAllGroupsContainingThis(): "+ex.getMessage());
//              }
//              vector.addElement(tempobj);
//            }
//
//            RS.close();
//
//          }
//          catch(Exception e){
//            throw new EJBException(e.getMessage());
//          }
//          finally{
//            if(Stmt != null){
//              try{
//                Stmt.close();
//              }
//              catch(SQLException e){}
//            }
//            if (conn != null){
//                freeConnection(getDatasource(),conn);
//            }
//          }
//
//          if (vector != null){
//            vector.trimToSize();
//            return vector;
//            //return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
//          }
//          else{
//            return null;
//          }
//        }
//






  //??

//        public Group[] getAllGroupsContained()throws SQLException{
//
//          List vector = this.getListOfAllGroupsContained();
//
//          if(vector != null){
//
//            return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
//
//          }else{
//
//            return new Group[0];
//
//          }
//
//        }



  /**
   * @todo change name to getGroupsContained();
   */
  public List getListOfAllGroupsContained()throws EJBException{
    List theReturn = new Vector();
    try{
      Collection relations = this.getGroupRelationHome().findGroupsRelationshipsUnder(this);
      Iterator iter = relations.iterator();
      while (iter.hasNext()) {
        GroupRelation item = (GroupRelation)iter.next();
        Group related = item.getRelatedGroup();
        theReturn.add(related);
      }
    }
    catch(Exception e){
      throw new EJBException(e.getMessage());
    }
    return theReturn;
  }


  /**
   * @todo change name to getGroupsContained();
   */
//        private List getListOfAllGroupsContainedLegacy()throws EJBException{
//          String tableToSelectFrom = "IC_GROUP_TREE";
//          StringBuffer buffer=new StringBuffer();
//          buffer.append("select CHILD_IC_GROUP_ID from ");
//          buffer.append(tableToSelectFrom);
//          buffer.append(" where ");
//          buffer.append("IC_GROUP_ID");
//          buffer.append("=");
//          buffer.append(this.getID());
//          String SQLString=buffer.toString();
//		Connection conn= null;
//		Statement Stmt= null;
//		Vector vector = new Vector();
//		try
//		{
//			conn = getConnection(getDatasource());
//			Stmt = conn.createStatement();
//			ResultSet RS = Stmt.executeQuery(SQLString);
//			while (RS.next()){
//
//				IDOLegacyEntity tempobj=null;
//				try{
//					tempobj = (IDOLegacyEntity)Class.forName(this.getClass().getName()).newInstance();
//					tempobj.findByPrimaryKey(RS.getInt("CHILD_IC_GROUP_ID"));
//				}
//				catch(Exception ex){
//					System.err.println("There was an error in " + this.getClass().getName() +".getAllGroupsContainingThis(): "+ex.getMessage());
//
//				}
//
//				vector.addElement(tempobj);
//
//			}
//			RS.close();
//
//		}
//                catch(Exception e){
//                  throw new EJBException(e.getMessage());
//                }
//		finally{
//			if(Stmt != null){
//                          try{
//				Stmt.close();
//			  }
//                          catch(SQLException sqle){}
//                        }
//			if (conn != null){
//				freeConnection(getDatasource(),conn);
//			}
//		}
//
//		if (vector != null){
//			vector.trimToSize();
//                        return vector;
//			//return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
//		}
//		else{
//			return null;
//		}
//
//
//          //return (Group[])this.findReverseRelated(this);
//
//        }



  /**

   * @todo change implementation: let the database handle the filtering

   */

  public List getGroupsContained(String[] groupTypes, boolean returnSepcifiedGroupTypes)throws EJBException{
    List list = this.getListOfAllGroupsContained();
    List specifiedGroups = new Vector();
    List notSpecifiedGroups = new Vector();
    int j = 0;
    int k = 0;
    Iterator iter2 = list.iterator();
    if(groupTypes != null && groupTypes.length > 0){
      boolean specified = false;
      while (iter2.hasNext()) {
        Group tempObj = (Group)iter2.next();
        for (int i = 0; i < groupTypes.length; i++) {
          try{
            if (tempObj.getGroupType().equals(groupTypes[i])){
              specifiedGroups.add(j++, tempObj);
              specified = true;
            }
          }
          catch(RemoteException rme){
          }
        }
        if(!specified) {
          notSpecifiedGroups.add(k++, tempObj);
        } else {
          specified = false;
        }
      }
      notSpecifiedGroups.remove(this);
      specifiedGroups.remove(this);
    } else {
      while (iter2.hasNext()) {
        Group tempObj = (Group)iter2.next();
        notSpecifiedGroups.add(j++, tempObj);
      }
      notSpecifiedGroups.remove(this);
      returnSepcifiedGroupTypes = false;
    }
    return (returnSepcifiedGroupTypes) ? specifiedGroups : notSpecifiedGroups;
  }





  public Collection getAllGroupsContainingUser(User user)throws EJBException,RemoteException{
    return this.getListOfAllGroupsContaining(user.getGroupID());
  }



  public void addGroup(Group groupToAdd )throws EJBException,RemoteException{
    this.addGroup(this.getGroupIDFromGroup(groupToAdd));
  }


  public void addGroup(int groupId)throws EJBException{
    try{
      GroupRelation rel = this.getGroupRelationHome().create();
      rel.setGroup(this);
      rel.setRelatedGroup(groupId);
      rel.store();
    }
    catch(Exception e){
      throw new EJBException(e.getMessage());
    }
  }

        public void addRelation(Group groupToAdd,String relationType)throws CreateException,RemoteException{
          this.addRelation(this.getGroupIDFromGroup(groupToAdd),relationType);
        }

        public void addRelation(Group groupToAdd,GroupRelationType relationType)throws CreateException,RemoteException{
          this.addRelation(this.getGroupIDFromGroup(groupToAdd),relationType);
        }

        public void addRelation(int relatedGroupId,GroupRelationType relationType)throws CreateException,RemoteException{
          this.addRelation(relatedGroupId,relationType.getType());
        }

        public void addRelation(int relatedGroupId,String relationType)throws CreateException,RemoteException{
          //try{
            GroupRelation rel = this.getGroupRelationHome().create();
            rel.setGroup(this);
            rel.setRelatedGroup(relatedGroupId);
            rel.setRelationshipType(relationType);
            rel.store();
          //}
          //catch(Exception e){
          //  throw new EJBException(e.getMessage());
          //}
        }

        public void removeRelation(Group relatedGroup,String relationType)throws RemoveException,RemoteException{
          int groupId = this.getGroupIDFromGroup(relatedGroup);
          this.removeRelation(groupId,relationType);
        }

        public void removeRelation(int relatedGroupId,String relationType)throws RemoveException,RemoteException{
          GroupRelation rel = null;
          try{
            //Group group = this.getGroupHome().findByPrimaryKey(relatedGroupId);
            Collection rels;
            rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(),relatedGroupId,relationType);
            Iterator iter = rels.iterator();
            while (iter.hasNext()) {
              rel = (GroupRelation)iter.next();
              rel.remove();
            }
          }
          catch(FinderException e){
            throw new RemoveException(e.getMessage());
          }
        }

        /**
         * Returns a collection of Group objects that are related by the relation type relationType with this Group
         */
        public Collection getRelatedBy(GroupRelationType relationType)throws FinderException,RemoteException{
          return getRelatedBy(relationType.getType());
        }

        /**
         * Returns a collection of Group objects that are related by the relation type relationType with this Group
         */
        public Collection getRelatedBy(String relationType)throws FinderException,RemoteException{
          GroupRelation rel = null;
            Collection theReturn = new Vector();
            Collection rels = null;
            rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(),relationType);
            Iterator iter = rels.iterator();
            while (iter.hasNext()) {
              rel = (GroupRelation)iter.next();
              Group g = rel.getRelatedGroup();
              theReturn.add(g);
            }
            return theReturn;
        }

//        private void addGroupLegacy(int groupId)throws EJBException{
//          Connection conn= null;
//          Statement Stmt= null;
//          try{
//            conn = getConnection(getDatasource());
//            Stmt = conn.createStatement();
//            String sql = "insert into IC_GROUP_TREE ("+getIDColumnName()+", CHILD_IC_GROUP_ID) values("+getID()+","+groupId+")";
//            //System.err.println(sql);
//            int i = Stmt.executeUpdate(sql);
//            //System.err.println(sql);
//          }catch (Exception ex) {
//            ex.printStackTrace(System.out);
//          }finally{
//            if(Stmt != null){
//              try{
//                Stmt.close();
//              }
//              catch(SQLException sqle){}
//            }
//            if (conn != null){
//              freeConnection(getDatasource(),conn);
//            }
//          }
//        }


  public void removeGroup(Group entityToRemoveFrom)throws EJBException,RemoteException{
    int groupId = this.getGroupIDFromGroup(entityToRemoveFrom);
    if( (groupId==-1) || (groupId==0))//removing all in middle table
      this.removeGroup(groupId,true);
    else// just removing this particular one
      this.removeGroup(groupId,false);
  }



  public void removeGroup() throws EJBException{
    this.removeGroup(-1,true);
  }

  public void removeGroup(int relatedGroupId, boolean AllEntries)throws EJBException{
    try{
      Collection rels = null;
      if(AllEntries){
        rels = this.getGroupRelationHome().findGroupsRelationshipsUnder(this);
      }
      else{
        rels = this.getGroupRelationHome().findGroupsRelationshipsContaining(this.getID(),relatedGroupId);
      }
      Iterator iter = rels.iterator();
      while (iter.hasNext()) {
        GroupRelation item = (GroupRelation)iter.next();
        item.remove();
      }
    }
    catch(Exception e){
      throw new EJBException(e.getMessage());
    }
  }

//        private void removeGroupLegacy(int groupId, boolean AllEntries)throws EJBException{
//          Connection conn= null;
//          Statement Stmt= null;
//          try{
//            conn = getConnection(getDatasource());
//            Stmt = conn.createStatement();
//            String qry;
//            if(AllEntries)//removing all in middle table
//              qry = "delete from IC_GROUP_TREE where "+this.getIDColumnName()+"='"+this.getID()+"' OR CHILD_IC_GROUP_ID ='"+this.getID()+"'";
//            else// just removing this particular one
//              qry = "delete from IC_GROUP_TREE where "+this.getIDColumnName()+"='"+this.getID()+"' AND CHILD_IC_GROUP_ID ='"+groupId+"'";
//            int i = Stmt.executeUpdate(qry);
//          }catch (Exception ex) {
//            ex.printStackTrace(System.out);
//          }finally{
//            if(Stmt != null){
//              try{
//                Stmt.close();
//              }
//              catch(SQLException sqle){}
//            }
//            if (conn != null){
//              freeConnection(getDatasource(),conn);
//            }
//          }
//        }
//



//        /**
//         * @deprecated moved to UserGroupBusiness
//         */
//        public static void addUserOld(int groupId, User user){
//          //((com.idega.user.data.GroupHome)com.idega.data.IDOLookup.getHomeLegacy(Group.class)).findByPrimaryKeyLegacy(groupId).addGroup(user.getGroupID());
//          throw new java.lang.UnsupportedOperationException("Method adduser moved to UserBusiness");
//        }



  public void addUser(User user)throws RemoteException{
    this.addGroup(user.getGroupID());
  }



  public void removeUser(User user)throws RemoteException{
    this.removeGroup(user.getGroupID(),false);
  }





//        public Group findGroup(String groupName) throws SQLException{
//
//          List group = EntityFinder.findAllByColumn(com.idega.data.GenericEntity.getStaticInstance(this.getClass().getName()),getNameColumnName(),groupName,getGroupTypeColumnName(),this.getGroupTypeValue());
//
//          if(group != null){
//
//            return (Group)group.get(0);
//
//          }else{
//
//            return null;
//
//          }
//
//        }


  public Collection ejbFindAllGroups(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws FinderException {
//          String typeList = "";
    if (groupTypes != null && groupTypes.length > 0){
//            for(int g = 0; g < groupTypes.length; g++){
//              if(g>0){ typeList += ", "; }
//              typeList += "'"+groupTypes[g]+"'";
//            }
      String typeList = IDOUtil.getInstance().convertArrayToCommaseparatedString(groupTypes,true);

      return super.idoFindIDsBySQL("select * from "+getEntityName()+" where "+getGroupTypeColumnName()+((returnSepcifiedGroupTypes)?" in (":" not in (")+typeList+") order by "+getNameColumnName());
    }
    return super.idoFindAllIDsOrderedBySQL(getNameColumnName());
  }


  /**
   *
   * @return all groups where getGroupType() is same as this.getGroupTypeValue()
   * @throws FinderException
   */
  public Collection ejbFindAll() throws FinderException {
//          String[] types = {this.getGroupTypeValue()};
//          return ejbFindAllGroups(types,true);
    return super.idoFindIDsBySQL("select * from "+getEntityName()+" where "+getGroupTypeColumnName()+" like '"+this.getGroupTypeValue()+"' order by "+getNameColumnName());
  }



  /**
   * @deprecated replaced with ejbFindAllGroups
   */
  public static List getAllGroupsOld(String[] groupTypes, boolean returnSepcifiedGroupTypes) throws SQLException {
    /*String typeList = "";
    if (groupTypes != null && groupTypes.length > 0){
      for(int g = 0; g < groupTypes.length; g++){
        if(g>0){ typeList += ", "; }
        typeList += "'"+groupTypes[g]+"'";
      }
      Group gr = (Group)com.idega.user.data.GroupBMPBean.getStaticInstance();
      return EntityFinder.findAll(gr,"select * from "+gr.getEntityName()+" where "+com.idega.user.data.GroupBMPBean.getGroupTypeColumnName()+((returnSepcifiedGroupTypes)?" in (":" not in (")+typeList+") order by "+com.idega.user.data.GroupBMPBean.getNameColumnName());
    }
    return EntityFinder.findAllOrdered(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getNameColumnName());
    */
    return null;
  }



  protected boolean identicalGroupExistsInDatabase() throws Exception {
    return SimpleQuerier.executeStringQuery("select * from "+this.getEntityName()+" where "+this.getGroupTypeColumnName()+" = '"+this.getGroupType()+"' and "+this.getNameColumnName()+" = '"+this.getName()+"'",this.getDatasource()).length > 0;
  }





  public void insert()throws SQLException{

    try {

//            if(!this.getName().equals("")){

        if(identicalGroupExistsInDatabase()){

          throw new SQLException("group with same name and type already in database");

        }

//            }

      super.insert();

    } catch (Exception ex) {

      if(ex instanceof SQLException ){

        throw (SQLException)ex;

      } else {

        //System.err.println(ex.getMessage());

        //ex.printStackTrace();

        throw new SQLException(ex.getMessage());

      }



    }

  }


//        public boolean equals(IDOLegacyEntity entity){
//          if(entity != null){
//            if(entity instanceof Group){
//              return this.equals((Group)entity);
//            } else {
//              return super.equals(entity);
//            }
//          }
//          return false;
//        }


  public boolean equals(Group group){
    if(group!=null){
      try{
        if(group.getPrimaryKey().equals(this.getPrimaryKey())){
          return true;
        }
      }
      catch(Exception e){
        return false;
      }
      return false;
    }
    return false;
  }



  protected int getGroupIDFromGroup(Group group)throws RemoteException{
    Integer groupID = ((Integer)group.getPrimaryKey());
    if(groupID!=null)
      return groupID.intValue();
    else
      return -1;
  }



  private GroupHome getGroupHome(){
    return ((GroupHome)this.getEJBHome());
  }

  private GroupRelationHome getGroupRelationHome()throws RemoteException{
    return ((GroupRelationHome)IDOLookup.getHome(GroupRelation.class));
  }

  public String ejbHomeGetGroupType(){
   return this.getGroupTypeValue();
  }



  public  Collection ejbFindGroups(String[] groupIDs) throws FinderException {
    Collection toReturn = new Vector(0);
    String sGroupList = "";
/*    if (groupIDs != null && groupIDs.length > 0){
      for(int g = 0; g < groupIDs.length; g++){
        if(g>0){ sGroupList += ", "; }
        sGroupList += groupIDs[g];
      }
    }*/
    sGroupList = IDOUtil.getInstance().convertArrayToCommaseparatedString(groupIDs);
    if(!sGroupList.equals("")){
      String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIDColumnName() + " in (" + sGroupList + ")";
      toReturn = super.idoFindPKsBySQL(sql);
    }
    return toReturn;
  }


  public Integer ejbFindSystemUsersGroup()throws FinderException{
    return new Integer(this.GROUP_ID_USERS);
  }


  public Iterator getChildren() {
    /**
     * @todo: Change implementation
     *
     */
    if(this.getID()==this.GROUP_ID_USERS){
      String[] groupTypes = {"ic_user_representative"};
      try{
        return this.getGroupHome().findGroups(groupTypes).iterator();
      }
      catch(Exception e){
        throw new RuntimeException(e.getMessage());
      }
    }
    else{
      return this.getListOfAllGroupsContained().iterator();
    }
  }
  public boolean getAllowsChildren() {
    return true;
  }
  public ICTreeNode getChildAtIndex(int childIndex) {
    try{
      return ((GroupHome)this.getEJBHome()).findByPrimaryKey(new Integer(childIndex));
    }
    catch(Exception e){
      throw new RuntimeException(e.getMessage());
    }
  }
  public int getChildCount() {
    return this.getListOfAllGroupsContained().size();
  }
  public int getIndex(ICTreeNode node) {
    return node.getNodeID();
  }

  /**
   * @todo reimplement
   */
  public ICTreeNode getParentNode() {
    ICTreeNode parent = null;
    try{
      parent = (ICTreeNode)this.getListOfAllGroupsContainingThis().iterator().next();
    }
    catch(Exception e){}
    return parent;
  }
  public boolean isLeaf() {
    /**
     * @todo reimplement
     */
    return !getChildren().hasNext();
  }
  public String getNodeName() {
    return this.getName();
  }
  public int getNodeID() {
    return ((Integer)this.getPrimaryKey()).intValue();
  }
  public int getSiblingCount() {
    ICTreeNode parent = getParentNode();
    if(parent != null){
      return parent.getChildCount();
    } else {
      return 0;
    }
  }


}   // Class Group

