package com.idega.io;

/**

*@author <a href="mailto:aron@idega.is">Aron Birkir</a>

*@version 1.0

*/



import java.io.*;

import com.idega.presentation.IWContext;

import com.oreilly.servlet.multipart.*;



public class FileSaver {



  public FileSaver(){



  }



  static public void SaveFileToDir(IWContext iwc)throws IOException {

    File dir = null;



    try {

      MultipartParser mp = new MultipartParser(iwc.getRequest(), 10*1024*1024); // 10MB

      Part part;

      while ((part = mp.readNextPart()) != null) {

        if(part.isParam() && part.getName().equalsIgnoreCase(FileSaver.getUploadDirParameterName())){

          ParamPart paramPart = (ParamPart) part;

          dir = new File(paramPart.getStringValue());

        }

        if (part.isFile() && dir != null) {

          // it's a file part

          FilePart filePart = (FilePart) part;

          String fileName = filePart.getFileName();

          if (fileName != null) {

            // the part actually contained a file

            long size = filePart.writeTo(dir);

          }

        }

      }

    }

    catch (IOException lEx) {

      System.err.print( "error reading or saving file");

    }

  }



  static public File FileToDir(IWContext iwc)throws IOException {

    File dir = null;



    try {

      MultipartParser mp = new MultipartParser(iwc.getRequest(), 10*1024*1024); // 10MB

      Part part;

      while ((part = mp.readNextPart()) != null) {

        if(part.isParam() && part.getName().equalsIgnoreCase(FileSaver.getUploadDirParameterName())){

          ParamPart paramPart = (ParamPart) part;

          dir = new File(paramPart.getStringValue());

        }

        if (part.isFile() && dir != null) {

          // it's a file part

          FilePart filePart = (FilePart) part;

          String fileName = filePart.getFileName();

          if (fileName != null) {

            // the part actually contained a file

            long size = filePart.writeTo(dir);

          }

        }

      }

    }

    catch (IOException lEx) {

      System.err.print( "error reading or saving file");

    }

    return dir;

  }

/*

   static public void SaveFileToOutPutStream(IWContext iwc,OutPutStream out)throws IOException {

    File dir;

    String dirName = sDirName;

    if (dirName == null) {

      throw new IOException("Please supply uploadDir parameter");

    }

    dir = new File(dirName);



    try {

      MultipartParser mp = new MultipartParser(iwc.getRequest(), 10*1024*1024); // 10MB

      Part part;

      while ((part = mp.readNextPart()) != null) {

        if (part.isFile()) {

          // it's a file part

          FilePart filePart = (FilePart) part;

          String fileName = filePart.getFileName();

          if (fileName != null) {

            // the part actually contained a file

            long size = filePart.writeTo(dir);

          }



        }

      }

    }

    catch (IOException lEx) {

      System.err.print( "error reading or saving file");

    }

  }



*/





  static public String getUploadDirParameterName(){

    return "FileSaverUploadDir";

  }



  static public String getUploadDir(IWContext iwc){

    String s = (String) iwc.getSession().getAttribute(getUploadDirParameterName());

    return s;

  }



  static public void setUploadDir(IWContext iwc,String sFilePath){

    iwc.getSession().setAttribute(getUploadDirParameterName(),sFilePath);

  }

}



