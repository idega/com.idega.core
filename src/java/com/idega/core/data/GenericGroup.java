package com.idega.core.data;

import java.sql.*;
import com.idega.data.*;
import java.util.Vector;
import com.idega.core.user.data.User;
import java.util.List;

/**
 * Title:        IW Core
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class GenericGroup extends GenericEntity{

	public GenericGroup(){
		super();
	}

	public GenericGroup(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute(getNameColumnName(),"Hópnafn", true, true, "java.lang.String");
		addAttribute(getGroupTypeColumnName(),"Hópgerð", true, true, "java.lang.String");
		addAttribute(getGroupDescriptionColumnName(),"Lýsing", true, true, "java.lang.String");
		addAttribute(getExtraInfoColumnName(),"Auka upplýsingar", true, true, "java.lang.String");
                this.addManyToManyRelationShip(User.class,"ic_group_user");

                this.addTreeRelationShip();
	}

	public String getEntityName(){
		return "ic_group";
	}

        public String getNameOfMiddleTable(GenericEntity entity1,GenericEntity entity2){
            return "ic_group_user";

        }


        public void setDefaultValues(){
          setGroupType(getGroupTypeValue());
        }

        /**
         * overwrite in extended classes
         */
        public String getGroupTypeValue(){
          return "general";
        }
        /**
         * overwrite in extended classes
         */
        public static String getClassName(){
          return GenericGroup.class.getName();
        }

        /*  ColumNames begin   */

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

        public static GenericGroup getStaticInstance(){
          return (GenericGroup)getStaticInstance(getClassName());
        }


        //??
        public GenericGroup[] getAllGroupsContainingThis()throws SQLException{
          List vector = this.getListOfAllGroupsContainingThis();
          if(vector != null){
            return (GenericGroup[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
          }else{
            return new GenericGroup[0];
          }
        }

        public List getListOfAllGroupsContainingThis()throws SQLException{
          String tableToSelectFrom = "IC_GROUP_TREE";
          StringBuffer buffer=new StringBuffer();
          buffer.append("select * from ");
          buffer.append(tableToSelectFrom);
          buffer.append(" where ");
          buffer.append("CHILD_IC_GROUP_ID");
          buffer.append("=");
          buffer.append(this.getID());
          String SQLString=buffer.toString();

		Connection conn= null;
		Statement Stmt= null;
		Vector vector = new Vector();

		try
		{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			while (RS.next()){

				GenericEntity tempobj=null;
				try{
					tempobj = (GenericEntity)Class.forName(this.getClass().getName()).newInstance();
					tempobj.findByPrimaryKey(RS.getInt(this.getIDColumnName()));
				}
				catch(Exception ex){
					System.err.println("There was an error in " + this.getClass().getName() +".getAllGroupsContainingThis(): "+ex.getMessage());

				}

				vector.addElement(tempobj);

			}
			RS.close();

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}

		if (vector != null){
			vector.trimToSize();
                        return vector;
			//return (GenericGroup[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
		}
		else{
			return null;
		}


          //return (Group[])this.findReverseRelated(this);

        }

        public GenericGroup[] getAllGroupsContainingUser(User user)throws SQLException{
          return (GenericGroup[])user.findRelated(this);
        }

        public void addGroup(GenericGroup groupToAdd )throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into IC_GROUP_TREE ("+getIDColumnName()+", CHILD_IC_GROUP_ID) values("+getID()+","+groupToAdd.getID()+")");
		}catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}
        }



        public void removeGroup(GenericGroup entityToRemoveFrom)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
                        String qry;
                        if( (entityToRemoveFrom.getID()==-1) || (entityToRemoveFrom.getID()==0))//removing all in middle table
                          qry = "delete from IC_GROUP_TREE where "+this.getIDColumnName()+"='"+this.getID()+"' OR CHILD_IC_GROUP_ID ='"+this.getID()+"'";
                        else// just removing this particular one
                          qry = "delete from IC_GROUP_TREE where "+this.getIDColumnName()+"='"+this.getID()+"' AND CHILD_IC_GROUP_ID ='"+entityToRemoveFrom.getID()+"'";


			int i = Stmt.executeUpdate(qry);
		}catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				freeConnection(getDatasource(),conn);
			}
		}
        }




        public static void addUser(int groupId, User user)throws SQLException{
          user.addTo(GenericGroup.class, groupId);
        }


        public void addUser(User user)throws SQLException{
          user.addTo(this);
        }

        public void removeUser(User user)throws SQLException{
          user.removeFrom(this);
        }


        public GenericGroup findGroup(String groupName) throws SQLException{
          List group = EntityFinder.findAllByColumn(GenericEntity.getStaticInstance(this.getClass().getName()),getNameColumnName(),groupName,getGroupTypeColumnName(),this.getGroupTypeValue());
          if(group != null){
            return (GenericGroup)group.get(0);
          }else{
            return null;
          }
        }



        public void insert()throws SQLException{
          try {
            if(SimpleQuerier.executeStringQuery("select * from "+this.getEntityName()+" where "+this.getGroupTypeColumnName()+" = '"+this.getGroupType()+"' and "+this.getNameColumnName()+" = '"+this.getName()+"'").length > 0){
              throw new SQLException("group with same name and type already in database");
            }
            super.insert();
          } catch (Exception ex) {
            if(ex instanceof SQLException ){
              throw (SQLException)ex;
            } else {
              System.err.println(ex.getMessage());
              ex.printStackTrace();
            }

          }
        }




}   // Class Group
