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
					for (int i = 1; i <= metaData.getColumnCount(); i++){


						if ( RS.getObject(metaData.getColumnName(i)) != null){

							//System.out.println("ColumName "+i+": "+metaData.getColumnName(i));
							tempobj.fillColumn(metaData.getColumnName(i),RS);
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

 	public static List findAllByColumnOrdered(GenericEntity fromEntity,String columnName1, String toFind1, String columnName2, String toFind2, String orderByColumnName)throws SQLException{
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName1+" like '"+toFind1+"' and "+columnName2+" like '"+toFind2+"' order by "+orderByColumnName);
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
		return findAll(fromEntity,"select * from "+fromEntity.getTableName()+" where "+columnName+" like '"+toFind+"'");
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
			while (RS.next()){

				GenericEntity tempobj=null;
				try{
					tempobj = (GenericEntity)Class.forName(returningEntity.getClass().getName()).newInstance();
					tempobj.findByPrimaryKey(RS.getInt(returningEntity.getIDColumnName()));
				}
				catch(Exception ex){
					System.err.println("There was an error in GenericEntity.findRelated(GenericEntity returningEntity,String SQLString): "+ex.getMessage());

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