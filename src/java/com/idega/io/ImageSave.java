package com.idega.io;

import java.io.*;
import java.sql.*;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.util.idegaTimestamp;
import oracle.sql.*;
import oracle.jdbc.driver.*;
import com.idega.data.GenericEntity;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class ImageSave {

  public ImageSave(){

  }

  public static int saveImageToDB(Connection Conn,int ImageID, InputStream in,String ContentType,String FileName,boolean NewImage){
    int id = -1;
    String statement ;
      try{
        if(Conn== null)
          return id;
        else{
        Conn.setAutoCommit(false);
        if(NewImage){
          id = com.idega.data.EntityControl.createUniqueID(new com.idega.data.genericentity.Image());
          statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file) values("+id+",?,?,?,'"+idegaTimestamp.getTimestampRightNow().toString()+"','N')";
        }
        else
          statement = "update image set image_value=?,content_type=?,image_name=? where image_id="+ImageID;

        com.idega.data.EntityControl.createUniqueID(new com.idega.data.genericentity.Image() );

        BufferedInputStream bin = new BufferedInputStream(in);
        PreparedStatement PS = Conn.prepareStatement(statement);
        PS.setBinaryStream(1, bin, bin.available() );
        PS.setString(2, ContentType );
        PS.setString(3, FileName );
        PS.execute();
        PS.close();
        Conn.setAutoCommit(true);
        }
      }
      catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}
      catch(Exception ex){ex.printStackTrace();}
      return id;
    }

  public static int saveImageToDB(int ImageID, int parentImageId, InputStream in,String ContentType,String FileName, String width, String height, boolean NewImage){
    int id = -1;
    String statement ;
    Connection Conn = null;
    boolean dimensions = true;
    if(width.equalsIgnoreCase("-1")) dimensions = false;

    try{
      Conn = GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").getConnection();
      if(Conn== null) return id;

      if(NewImage){
        id = com.idega.data.EntityControl.createUniqueID(new com.idega.data.genericentity.Image());
        if( dimensions ) statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id,width,height) values("+id+",?,?,?,'"+idegaTimestamp.getTimestampRightNow().toString()+"','N',"+parentImageId+",?,?)";
        else statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id) values("+id+",?,?,?,'"+idegaTimestamp.getTimestampRightNow().toString()+"','N',"+parentImageId+")";
      }
      else{
        if( dimensions ) statement = "update image set image_value=?,content_type=?,image_name=?,width=?,height=? where image_id="+ImageID;
        else statement = "update image set image_value=?,content_type=?,image_name=? where image_id="+ImageID;
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
      if(Conn != null) GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").freeConnection(Conn);
    }
    return id;
  }

  public static int saveImageToOracleDB(int ImageID, int parentImageId, InputStream in,String ContentType,String FileName, String width, String height, boolean NewImage){
    int id = -1;
    String statement ;
    Connection Conn = null;
    boolean dimensions = true;
    if(width.equalsIgnoreCase("-1")) dimensions = false;

    try{
      Conn = GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").getConnection();
      if(Conn== null) return id;

       if(NewImage){
        id = com.idega.data.EntityControl.createUniqueID(new com.idega.data.genericentity.Image());
      //debug skips date_added for now
       // statement = "insert into image (image_id,image_value,content_type,image_name,date_added,from_file,parent_id,width,height) values("+id+",?,?,?,'"+idegaTimestamp.getTimestampRightNow().toString()+"','N',"+parentImageId+",?,?)";
        if(dimensions) statement = "insert into image (image_id,image_value,content_type,image_name,from_file,parent_id,width,height) values("+id+",?,?,?,'N',"+parentImageId+",?,?)";
        else statement = "insert into image (image_id,image_value,content_type,image_name,from_file,parent_id) values("+id+",?,?,?,'N',"+parentImageId+")";
      }
      else{
        if(dimensions) statement = "update image set image_value=?,content_type=?,image_name=?,width=?,height=? where image_id='"+ImageID+"'";
        else statement = "update image set image_value=?,content_type=?,image_name=? where image_id='"+ImageID+"'";
      }
        oracle.sql.BLOB blob;
	Conn.setAutoCommit(false);
	PreparedStatement myPreparedStatement = Conn.prepareStatement ( statement );
        myPreparedStatement.setString(1, "00000001");//i stað hins venjulega empty_blob()
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
        String cmd = "SELECT image_value FROM image WHERE image_id ='"+ImageID+"' FOR UPDATE";
        ResultSet RS2 = stmt2.executeQuery(statement);

        RS2.next();
        blob = ((OracleResultSet)RS2).getBLOB("image_value");

          // write the array of binary data to a BLOB
        OutputStream     outstream = blob.getBinaryOutputStream();

        int size = blob.getBufferSize();
        byte[] buffer = new byte[size];
        int length = -1;

        while ((length = in.read(buffer)) != -1)
            outstream.write(buffer, 0, length );

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
      if(Conn != null) GenericEntity.getStaticInstance("com.idega.jmodule.image.data.ImageEntity").freeConnection(Conn);
    }
    return id;
  }

  static public String getUploadDirParameterName(){
    return "FileSaverUploadDir";
  }

  static public String getUploadDir(ModuleInfo modinfo){
    String s = (String) modinfo.getSession().getAttribute(getUploadDirParameterName());
    return s;
  }

  static public void setUploadDir(ModuleInfo modinfo,String sFilePath){
    modinfo.getSession().setAttribute(getUploadDirParameterName(),sFilePath);
  }
}

