//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;


import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;


/**
 *
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class EntityFinder{

        public static boolean debug = false;

        /**
         * Returns null if there was no match
         */
	public static List findAll(GenericEntity entity)throws SQLException{
		return findAll(entity,"select * from "+entity.getTableName());
	}

        /**
         * Returns null if there was no match
         */
         public static List findAll(GenericEntity entity,String SQLString)throws SQLException{
          return findAll(entity,SQLString,-1);
        }


        /**
         * Returns null if there was no match
         */
	public static List findAll(GenericEntity entity, String SQLString,int returningNumberOfRecords)throws SQLException{
		if(debug){
                  System.err.println("EntityFinder sql query :");
                  System.err.println(SQLString );
		}


                Connection conn= null;
		Statement Stmt= null;
		ResultSetMetaData metaData;
		//Vector vector = new Vector();
                Vector vector=null;
                boolean check=true;
		//Vector theIDs = new Vector();
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			metaData = RS.getMetaData();
                        int count = 1;
			while (RS.next() && check){
                          count++;
                          if(returningNumberOfRecords!=-1){
                            if(count>returningNumberOfRecords){
                              check=false;
                            }
                          }

				GenericEntity tempobj=null;
				try{
					tempobj = (GenericEntity)Class.forName(entity.getClass().getName()).newInstance();
				}
				catch(Exception ex){
					System.err.println("There was an error in GenericEntity.findAll "+ex.getMessage());
					ex.printStackTrace(System.err);
				}
				if(tempobj != null){
                                  String columnName = null;
					for (int i = 1; i <= metaData.getColumnCount(); i++){

                                          //debug getting an object every time? that sucks tryggvi ;)
                                          columnName = metaData.getColumnName(i);
						if ( RS.getObject(columnName) != null ){
                                                  //this must be done if using AS in an sql query
                                                  if ( "".equals(columnName) ) columnName = metaData.getColumnLabel(i);

                                                  tempobj.fillColumn(columnName,RS);
						}
					}

				}
                                if(vector==null){
                                  vector=new Vector();
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
				entity.freeConnection(conn);
			}
		}
		/*
		for (Enumeration enum = theIDs.elements();enum.hasMoreElements();){
			Integer tempInt = (Integer) enum.nextElement();
			vector.addElement(new GenericEntity(tempInt.intValue()));
		}*/


		if (vector != null){
			vector.trimToSize();
                        return vector;
			//return (GenericEntity[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(this.getClass(),0));
			//return vector.toArray(new GenericEntity[0]);
		}
		else{
			return null;
		}
	}

	/**
	*Finds all instances of the fromEntity in the otherEntity
	**/
	public static List findAssociated(GenericEntity fromEntity,GenericEntity otherEntity)throws SQLException{
		return findAll(otherEntity,"select * from "+otherEntity.getTableName()+" where "+fromEntity.getIDColumnName()+"='"+fromEntity.getID()+"'");
	}

	public static List findAssociatedOrdered(GenericEntity fromEntity,GenericEntity otherEntity,String column_name)throws SQLException{
		return findAll(otherEntity,"select * from "+otherEntity.getTableName()+" where "+fromEntity.getIDColumnName()+"='"+fromEntity.getID()+"' order by "+column_name+"");
	}

	public static List findAllOrdered(GenericEntity fromEntity,String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" order by "+orderByColumnName);
	}

	public static List findAllByColumnOrdered(GenericEntity fromEntity,String columnName, String toFind, String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName+" like '"+toFind+"' order by "+orderByColumnName);
	}

	public static List findAllByColumnOrdered(GenericEntity fromEntity,String columnName, int toFind, String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName+" = "+toFind+" order by "+orderByColumnName);
	}

 	public static List findAllByColumnOrdered(GenericEntity fromEntity,String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName);
	}

 	public static List findAllByColumnOrdered(GenericEntity fromEntity,String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName, String condition1, String condition2, String criteria, String returnColumn)throws SQLException{
		return findAll(fromEntity,"select "+criteria+" "+returnColumn+" from "+fromEntity.getTableName()+" where "+columnName1+" "+condition1+" '"+toFind1+"' and "+columnName2+" "+condition2+" '"+toFind2+"' order by "+orderByColumnName);
	}

	public static List findAllByColumnDescendingOrdered(GenericEntity fromEntity,String columnName, String toFind, String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName+" like '"+toFind+"' order by "+orderByColumnName+" desc");
	}

 	public static List findAllByColumnDescendingOrdered(GenericEntity fromEntity,String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName+" desc");
	}

 	public static List findAllDescendingOrdered(GenericEntity fromEntity,String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" order by "+orderByColumnName+" desc");
	}

	public static List findAllByColumn(GenericEntity fromEntity,String columnName, String toFind)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName+" like '"+toFind+"'");
	}

	public static List findAllByColumn(GenericEntity fromEntity,String columnName, int toFind)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName+"="+toFind+"");
	}


	public static List findAllByColumn(GenericEntity fromEntity,String columnName1, String toFind1,String columnName2, String toFind2)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"'");
	}

        public static List findAllByColumn(GenericEntity fromEntity,String columnName1, String toFind1,String columnName2, String toFind2, String columnName3, String toFind3)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' and "+columnName3+" like '"+toFind3+"'");
	}

        public static List findReverseRelated(GenericEntity fromEntity,GenericEntity returningEntity)throws SQLException{
		String tableToSelectFrom = EntityControl.getNameOfMiddleTable(fromEntity,returningEntity);
		String SQLString="select * from "+tableToSelectFrom+" where "+fromEntity.getIDColumnName()+"="+fromEntity.getID();
		return findRelated(fromEntity,returningEntity,SQLString);
	}


        /**
         * Returns null if nothing found
         */
	protected static List findRelated(GenericEntity fromEntity,GenericEntity returningEntity,String SQLString)throws SQLException{
		if(debug){
			System.err.println("EntityFinder : findRelated :");
			System.err.println(SQLString);
		}

		Connection conn= null;
		Statement Stmt= null;
		//Vector vector = new Vector();
		Vector vector=null;
                String tableToSelectFrom = "";
		if (returningEntity.getTableName().endsWith("_")){
			tableToSelectFrom = returningEntity.getTableName()+fromEntity.getTableName();
		}
		else{
			tableToSelectFrom = returningEntity.getTableName()+"_"+fromEntity.getTableName();
		}

		try
		{
			conn = fromEntity.getConnection();
			Stmt = conn.createStatement();
			ResultSet RS = Stmt.executeQuery(SQLString);
			while (RS !=null && RS.next()){

				GenericEntity tempobj=null;
				try{
					tempobj = (GenericEntity)Class.forName(returningEntity.getClass().getName()).newInstance();
				  if(debug)
					System.err.println("Entity "+returningEntity.getEntityName()+ " Id Columnname "+returningEntity.getIDColumnName()) ;
					int id = RS.getInt(returningEntity.getIDColumnName());
					tempobj.findByPrimaryKey(id);
				}
				catch(Exception ex){

					System.err.println("There was an error in GenericEntity.findRelated(GenericEntity returningEntity,String SQLString): "+ex.getMessage());
					ex.printStackTrace();

				}
                                if(vector==null){
                                  vector=new Vector();
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
				fromEntity.freeConnection(conn);
			}
		}

		if (vector != null){
			vector.trimToSize();
			//return (GenericEntity[]) vector.toArray((Object[])java.lang.reflect.Array.newInstance(returningEntity.getClass(),0));
			//return vector.toArray(new GenericEntity[0]);
		        return vector;
                }
		else{
			return null;
		}
	}



	public static List findRelated(GenericEntity fromEntity,GenericEntity returningEntity)throws SQLException{
		String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntity,fromEntity);
                StringBuffer buffer=new StringBuffer();
                buffer.append("select * from ");
                buffer.append(tableToSelectFrom);
                buffer.append(" where ");
                buffer.append(fromEntity.getIDColumnName());
                buffer.append("=");
                buffer.append(fromEntity.getID());
                String SQLString=buffer.toString();

		//String SQLString="select * from "+tableToSelectFrom+" where "+fromEntity.getIDColumnName()+"="+fromEntity.getID();

                //System.out.println("FindRelated SQLString="+SQLString+"crap");
                return findRelated(fromEntity,returningEntity,SQLString);
	}


  public static List findNonRelated(GenericEntity fromEntity,GenericEntity returningEntity) {
		try {
      String tableToSelectFrom = EntityControl.getNameOfMiddleTable(returningEntity,fromEntity);

      StringBuffer buffer=new StringBuffer();
        buffer.append("select "+returningEntity.getTableName()+".* from ");
        buffer.append(returningEntity.getTableName());
        buffer.append(" where ");
        buffer.append(returningEntity.getIDColumnName());
        buffer.append(" not in (select "+returningEntity.getIDColumnName()+" from ");
        buffer.append(tableToSelectFrom);
        buffer.append(")");

      String SQLString=buffer.toString();

      return findRelated(fromEntity,returningEntity,SQLString);
		}
    catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

        /**
         * If ascending==true ordering is descending, else it is ascending
         */
	public static List findRelatedOrdered(GenericEntity fromEntity,GenericEntity returningEntity,String returningEntityColumnToOrderBy,boolean ascending)throws SQLException{
		String fromTable = fromEntity.getTableName();
                String middleTable = EntityControl.getNameOfMiddleTable(returningEntity,fromEntity);
                String returningTable = returningEntity.getTableName();
                String comma = ",";
                String dot = ".";
                StringBuffer buffer=new StringBuffer();
                buffer.append("select ");
                buffer.append(returningTable);
                buffer.append(dot);
                buffer.append("* from ");
                buffer.append(middleTable);
                buffer.append(comma);
                buffer.append(returningTable);
                buffer.append(comma);
                buffer.append(fromTable);
                buffer.append(" where ");
                buffer.append(middleTable);
                buffer.append(dot);
                buffer.append(fromEntity.getIDColumnName());
                buffer.append("=");
                buffer.append(fromEntity.getID());
                buffer.append(" and ");
                buffer.append(fromTable);
                buffer.append(dot);
                buffer.append(fromEntity.getIDColumnName());
                buffer.append("=");
                buffer.append(middleTable);
                buffer.append(dot);
                buffer.append(fromEntity.getIDColumnName());
                buffer.append(" and ");
                buffer.append(middleTable);
                buffer.append(dot);
                buffer.append(returningEntity.getIDColumnName());
                buffer.append("=");
                buffer.append(returningTable);
                buffer.append(dot);
                buffer.append(returningEntity.getIDColumnName());
                buffer.append(" order by ");
                buffer.append(returningTable);
                buffer.append(".");
                buffer.append(returningEntityColumnToOrderBy);
                if(ascending){
                buffer.append(" asc");
                }
                else{
                buffer.append(" desc");
                }
                String SQLString=buffer.toString();
                return findAll(returningEntity,SQLString);
	}



	public static GenericEntity findByPrimaryKey(String entityClassName,int primaryKeyID)throws Exception{
               GenericEntity entity = (GenericEntity)Class.forName(entityClassName).newInstance();
                return findByPrimaryKey(entity,primaryKeyID);

        }

	public static GenericEntity findByPrimaryKey(GenericEntity entity,int primaryKeyID)throws Exception{

		Connection conn= null;
		Statement Stmt= null;
		try{
			conn = entity.getConnection();
			Stmt = conn.createStatement();
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("select * from ");
                        buffer.append(entity.getTableName());
                        buffer.append(" where ");
                        buffer.append(entity.getIDColumnName());
                        buffer.append("=");
                        buffer.append(primaryKeyID);

                        ResultSet RS = Stmt.executeQuery(buffer.toString());

			//ResultSet RS = Stmt.executeQuery("select * from "+getTableName()+" where "+getIDColumnName()+"="+id);

                        RS.next();
			String[] columnNames = entity.getColumnNames();
			for (int i = 0; i < columnNames.length; i++){
				try{
                                  if (RS.getString(columnNames[i]) != null){
				  	entity.fillColumn(columnNames[i],RS);
			          }
                                }
                                catch(SQLException ex){
                                  //NOCATH
                                  try{
                                    if (RS.getString(columnNames[i].toUpperCase()) != null){
				  	entity.fillColumn(columnNames[i],RS);
			            }
                                  }
                                  catch(SQLException exe){
                                    try{
                                      if (RS.getString(columnNames[i].toLowerCase()) != null){
				    	  entity.fillColumn(columnNames[i],RS);
			                }
                                      }
                                    catch(SQLException exep){
                                         System.err.println("Exception in GenericEntity findByPrimaryKey, RS.getString() not found: "+exep.getMessage());
                                          exep.printStackTrace(System.err);
                                    }
                                  }

                                }

			}
			RS.close();

		}
		finally{
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
                if(entity.getID() != -1){
                  return entity;
                }
                return null;
	}









}