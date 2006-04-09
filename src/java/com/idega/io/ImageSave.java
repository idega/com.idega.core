package com.idega.io;



import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.driver.OracleResultSet;

import com.idega.util.IWTimestamp;

/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega multimedia

 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>

 * @version 1.0

 */



public class ImageSave {



  public static int saveImageToDB(Connection Conn,int imageId, InputStream in,String ContentType,String FileName,boolean NewImage){

    if (Conn!=null){

      String dataBaseType = com.idega.data.DatastoreInterface.getDataStoreType(Conn);

      if( !dataBaseType.equalsIgnoreCase("oracle")  ) {
				return saveImageToDB(imageId,-1,in,ContentType,FileName,"-1", "-1",NewImage);
			}
			else {
				return saveImageToOracleDB(imageId,-1,in,ContentType,FileName,"-1","-1",NewImage);
			}

    }
		else {
			return -1;
		}

  }



  public static int saveImageToDataBase(int imageId, int parentImageId, InputStream in,String ContentType,String FileName, String width, String height, boolean NewImage){

    Connection Conn = null;

    try{

      Conn = com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").getConnection();

      String dataBaseType = com.idega.data.DatastoreInterface.getDataStoreType(Conn);

      if( !dataBaseType.equalsIgnoreCase("oracle")  ) {
				return saveImageToDB(imageId,parentImageId,in,ContentType,FileName,width, height,NewImage);
			}
			else {
				return saveImageToOracleDB(imageId,parentImageId,in,ContentType,FileName,width, height,NewImage);
			}

    }

    catch(SQLException e){

      e.printStackTrace(System.err);

      return -1;

    }

    finally{

      if(Conn != null ) {
				com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").freeConnection(Conn);
			}

    }



  }



  public static int saveImageToDB(int imageId, int parentImageId, InputStream in,String ContentType,String FileName, String width, String height, boolean NewImage){

    int id = -1;

    String statement ;

    Connection Conn = null;

    boolean dimensions = true;

    if(width.equalsIgnoreCase("-1")) {
			dimensions = false;
		}



    try{

      Conn = com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").getConnection();

      if(Conn== null) {
				return id;
			}



      if(NewImage){

        id = com.idega.data.EntityControl.createUniqueID(com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity"));

        if( dimensions ) {
					statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id,width,height) values("+id+",?,?,?,'"+IWTimestamp.getTimestampRightNow().toString()+"','N',"+parentImageId+",?,?)";
				}
				else {
					statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id) values("+id+",?,?,?,'"+IWTimestamp.getTimestampRightNow().toString()+"','N',"+parentImageId+")";
				}

      }

      else{

        if( dimensions ) {
					statement = "update image set image_value=?,content_type=?,image_name=?,width=?,height=? where image_id="+imageId;
				}
				else {
					statement = "update image set image_value=?,content_type=?,image_name=? where image_id="+imageId;
				}

      }

      Conn.setAutoCommit(false);

      BufferedInputStream bin = new BufferedInputStream(in);

      PreparedStatement PS = Conn.prepareStatement(statement);

      PS.setBinaryStream(1, bin, bin.available() );

      PS.setString(2, ContentType );

      PS.setString(3, FileName );

      if(dimensions){

        PS.setString(4, width );

        PS.setString(5, height );

      }

      PS.execute();

      PS.close();

      Conn.commit();

      Conn.setAutoCommit(true);



    }

    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}

    catch(Exception ex){ex.printStackTrace();}

    finally{

      if(Conn != null) {
				com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").freeConnection(Conn);
			}

    }

    return id;

  }



  public static int saveImageToOracleDB(int imageId, int parentImageId, InputStream in,String ContentType,String FileName, String width, String height, boolean NewImage){

    int id = -1;

    String statement ;

    Connection Conn = null;

    boolean dimensions = false;

    if(width.equalsIgnoreCase("-1")) {
			dimensions = false;
		}

System.out.println("ImageSave : width ="+width);



System.out.println("ImageSave : height ="+height);



    try{

      Conn = com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").getConnection();

      if(Conn == null) {
				return id;
			}



       if(NewImage){

        id = com.idega.data.EntityControl.createUniqueID(com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity"));

        if(dimensions) {
					statement = "insert into image (image_id,image_value,content_type,image_name,width,height,date_added,from_file,parent_id) values("+id+",?,?,?,?,?,"+IWTimestamp.RightNow().toOracleString()+",'N',"+parentImageId+")";
				}
				else {
					statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id) values("+id+",?,?,?,"+IWTimestamp.RightNow().toOracleString()+",'N',"+parentImageId+")";
				}

      }

      else{

        if(dimensions) {
					statement = "update image set image_value=?,content_type=?,image_name=?,width=?,height=? where image_id='"+imageId+"'";
				}
				else {
					statement = "update image set image_value=?,content_type=?,image_name=? where image_id='"+imageId+"'";
				}

      }



        oracle.sql.BLOB blob;

        System.out.println(statement);



	Conn.setAutoCommit(false);

	PreparedStatement myPreparedStatement = Conn.prepareStatement ( statement );

        myPreparedStatement.setString(1, "00000001");// 00000001 i stað hins venjulega empty_blob()

        myPreparedStatement.setString(2, ContentType );

        myPreparedStatement.setString(3, FileName );

        if(dimensions){

          myPreparedStatement.setString(4, width );

          myPreparedStatement.setString(5, height );

        }

        myPreparedStatement.execute();

        myPreparedStatement.close();



        Conn.commit();



        Conn.setAutoCommit(false);

        Statement stmt2 = Conn.createStatement();

        if(!NewImage) {
					id = imageId;
				}

        String cmd = "SELECT image_value FROM image WHERE image_id ='"+id+"' FOR UPDATE ";

        ResultSet RS2 =  stmt2.executeQuery(cmd);



        RS2.next();

        blob = ((OracleResultSet)RS2).getBLOB(1);



          // write the array of binary data to a BLOB

        OutputStream outstream = blob.getBinaryOutputStream();



        int size = blob.getBufferSize();

        byte[] buffer = new byte[size];

        int length = -1;



        while ((length = in.read(buffer)) != -1) {
					outstream.write(buffer, 0, length );
				}



        outstream.flush();

        outstream.close();

        in.close();



        stmt2.close();

        RS2.close();



        Conn.commit();

        Conn.setAutoCommit(true);



    }

    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}

    catch(Exception ex){ex.printStackTrace();}

    finally{

      if(Conn != null) {
				com.idega.data.GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").freeConnection(Conn);
			}

    }

    return id;

  }

}



