//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import java.util.Vector;
import com.idega.core.user.data.User;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/

public abstract class GeneralGroup extends GenericEntity{

	public GeneralGroup(){
		super();
	}

	public GeneralGroup(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute(getNameColumnName(),"Hópnafn", true, true, "java.lang.String");
		addAttribute(getGroupTypeColumnName(),"Hópgerð", true, true, "java.lang.String");
		addAttribute(getGroupDescriptionColumnName(),"Lýsing", true, true, "java.lang.String");
		addAttribute(getExtraInfoColumnName(),"Auka upplýsingar", true, true, "java.lang.String");
                this.addManyToManyRelationShip("com.idega.core.user.data.User","ic_group_user");
	}

	public String getEntityName(){
		return "ic_group";
	}


        public void setDefaultValues(){
          setGroupType(getGroupTypeValue());
        }

        /**
         * overwrite in extended classes
         */
        public abstract String getGroupTypeValue();

        /**
         * overwrite in extended classes
         */
        public static String getClassName(){
          return "com.idega.core.data.GeneralGroup";
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

        public static Group getStaticInstance(){
          return (Group)getStaticInstance(getClassName());
        }


        //??
        public Group[] getAllGroupsContainingThis()throws SQLException{
          String tableToSelectFrom = "GROUP_TREE";
          StringBuffer buffer=new StringBuffer();
          buffer.append("select * from ");
          buffer.append(tableToSelectFrom);
          buffer.append(" where ");
          buffer.append("CHILD_GROUP_ID");
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
			return (Group[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
		}
		else{
			return null;
		}


          //return (Group[])this.findReverseRelated(this);
        }

        public Group[] getAllGroupsContainingUser(User user)throws SQLException{
          return (Group[])user.findRelated(this);
        }

        public void addGroup(Group groupToAddTo)throws SQLException{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = getConnection(getDatasource());
			Stmt = conn.createStatement();
			int i = Stmt.executeUpdate("insert into GROUP_TREE ("+getIDColumnName()+", CHILD_GROUP_ID) values("+getID()+","+groupToAddTo.getID()+")");
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

        public void addUser(User user)throws SQLException{
          user.addTo(this);
        }



}   // Class Group
