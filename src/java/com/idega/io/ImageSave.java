package com.idega.io;

import java.io.*;
import java.sql.*;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.util.idegaTimestamp;

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
      try{
        if(Conn== null)
          return id;
        else{
        Conn.setAutoCommit(false);
        String statement ;
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

