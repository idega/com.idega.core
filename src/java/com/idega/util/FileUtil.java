package com.idega.util;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

 import java.io.File;
 import java.io.InputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.LineNumberReader;
 import java.io.FileReader;
 import java.util.Vector;
 import java.util.StringTokenizer;
 import java.util.Iterator;

public class FileUtil {

  private static String systemSeparatorString =  "file.separator";


  private FileUtil() {
  }

  public static void createFileAndFolder(String path,String fileNameWithoutFullPath){

    createFolder(path);
    String filePath = getFileNameWithPath(path,fileNameWithoutFullPath);
    createFile(filePath);

  }

  /**
   * Creates a folder if it does not exists. Returns true if creation successful, false otherwise
   */
  public static boolean createFolder(String path){
    File folder = new File(path);
    if(!folder.exists()){
      folder.mkdirs();
      return true;
    }
    else{
      return false;
    }
  }

  public static boolean createFile(String fileNameWithFullPath){
      File file = new File(fileNameWithFullPath);
      try{
        return file.createNewFile();
      }
      catch(IOException ex){
        return false;
      }
  }

  /**
   * Gets the System wide path separator
   */
  public static String getFileSeparator(){
    return System.getProperty(systemSeparatorString);
  }

  public static String getFileNameWithPath(String path,String fileNameWithoutFullPath){
    return path+getFileSeparator()+fileNameWithoutFullPath;
  }

  /**
   * Returns a File Object. Creates the File and parent folders if they do not exist.
   */
  public static File getFileAndCreateIfNotExists(String path,String fileNameWithoutFullPath)throws IOException{
    createFolder(path);
    String fullPath = getFileNameWithPath(path,fileNameWithoutFullPath);
    return getFileAndCreateIfNotExists(fullPath);
  }

  /**
   * Returns a File Object. Creates the File if it does not exist.
   */
  public static File getFileAndCreateIfNotExists(String fileNameWithFullPath)throws IOException{
      File file = new File(fileNameWithFullPath);
      file.createNewFile();
      return file;
  }

  /**
   * Deletes a File Object.
   */
  public static File delete(File file){
    file.delete();
    return file;
  }

  /**
   * Deletes a File from an url.
   */

  public static boolean delete(String fileNameWithFullPath){
    File file = new File(fileNameWithFullPath);
    return file.delete();
}

/*
* streams an inputstream to a file
*/
  public static void streamToFile( InputStream input, String filePath, String fileName){
    try{
      File file = getFileAndCreateIfNotExists(filePath,fileName);
      FileOutputStream fileOut = new FileOutputStream(file);

      byte buffer[]= new byte[1024];
      int	noRead	= 0;

      if(input!=null){
        noRead = input.read( buffer, 0, 1024 );

        //Write out the stream to the file
        while ( noRead != -1 ){
          fileOut.write( buffer, 0, noRead );
          noRead = input.read( buffer, 0, 1024 );
        }
        fileOut.flush();
        fileOut.close();
      }
      else System.err.println("FileUtil : InputStream is null!");

    }
    catch(IOException e){
      e.printStackTrace(System.err);
      System.err.println("FileUtil : Error writing to file!");
    }
    finally{
      try{
        if(input!=null) input.close();
      }
      catch(IOException e){
        e.printStackTrace(System.err);
        System.err.println("FileUtil : Error closing the inputstream");
      }
    }

  }

    /**
   * deletes entire contents of a folder. Returns true if deletion successful, false otherwise
   */
  public static boolean deleteAllFilesInDirectory(String path){
    File folder = new File(path);
    if(folder.exists()){
      File[] files = folder.listFiles();
      for (int i = 0; i < files.length; i++) {
       files[i].delete();
      }
      return true;
    }
    else{
      return false;
    }
  }
/** Gets the lines from a file and return the as a vector of strings **/
  public static Vector getLinesFromFile(String pathAndFile) throws IOException{
    Vector strings = new Vector();

    FileReader reader;
    LineNumberReader lineReader = null;

    reader = new FileReader(pathAndFile);
    lineReader = new LineNumberReader(reader);

    lineReader.mark(1);
    while (lineReader.read() != -1) {
        lineReader.reset();
        strings.addElement(lineReader.readLine());
        lineReader.mark(1);
    }

    return strings;
  }

/** uses getLinesFromFile and cuts the lines into java.util.StringTokenizer and returns them in a vector **/
   public static Vector getCommaSeperatedTokensFromLinesFromFile(String pathAndFile, String seperatorToken) throws IOException{
    Vector lines = getLinesFromFile(pathAndFile);
    Vector tokens = new Vector();
    String item;

    Iterator iter = lines.iterator();
    StringTokenizer tokenizer;

    while (iter.hasNext()) {
      item = (String) iter.next();
      tokenizer = new StringTokenizer(item,seperatorToken);
      tokens.add(tokenizer);
    }


    return tokens;
  }


}