package com.idega.util;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

 import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class FileUtil {

  public static char UNIX_FILE_SEPARATOR = '/';
  public static char WINDOWS_FILE_SEPARATOR = '\\';
  
	
  private static String systemSeparatorString =  "file.separator";


  private FileUtil() {
  	// empty
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
    return false;
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
   * Returns a File Object. Creates the File and recursively all the directory structure prefixing the file
   * if it does not exist.
   */
  public static File getFileAndCreateRecursiveIfNotExists(String fileNameWithFullPath)throws IOException{

      String separator = File.separator;
      int index = fileNameWithFullPath.lastIndexOf(separator);

      String path = fileNameWithFullPath.substring(0,index);
      String name = fileNameWithFullPath.substring(index+1);

      File dirs = null;
      try{
        dirs = new File(path);
        dirs.mkdirs();
      }
      catch(Exception e){
        e.printStackTrace();
      }
      File file = null;

      if(dirs!=null)
        file = new File(dirs,name);
      else
        file = new File(name);

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
      if(input!=null){
        input.available();//this casts an ioexception if the stream is null
        File file = getFileAndCreateIfNotExists(filePath,fileName);
        FileOutputStream fileOut = new FileOutputStream(file);

        byte buffer[]= new byte[1024];
        int	noRead	= 0;

        noRead = input.read( buffer, 0, 1024 );
        //Write out the stream to the file
        while ( noRead != -1 ){
          fileOut.write( buffer, 0, noRead );
          noRead = input.read( buffer, 0, 1024 );
        }

        fileOut.flush();
        fileOut.close();
      }

    }
    catch(IOException e){
      //e.printStackTrace(System.err);
      System.err.println("FileUtil : Error or skipping (for folders) writing to file");
    }
    finally{
      try{
        if(input!=null) input.close();
      }
      catch(IOException e){
        //e.printStackTrace(System.err);
        System.err.println("FileUtil : Error closing the inputstream");
      }
    }

  }

    /** 
   * Deletes content of folder.
   * !! Be careful !!
   * Returns also false if the specified path doesn't exist.
   * @author thomas
   */
  public static boolean deleteContentOfFolder(String path) {
  	boolean result = true;
  	boolean successful = true;
  	File folder = new File(path);
  	if (folder.exists() && folder.isDirectory()) {
  		File children[] = folder.listFiles();
  		for (int i = 0; i < children.length; i++) {
  			successful = FileUtil.deleteFileAndChildren(children[i]);
  			if (! successful) {
  				result = false;
  			}
  		}
  		return result;
  	}
  	return false;
  }
  
  
  /** 
   * Deletes file and children.
   * !! Be careful !!
   * @author thomas
   */
  public static boolean deleteFileAndChildren(File file) {
  	boolean successful = true;
  	if (file.exists()) {
  		if (file.isDirectory()) {
  			File children[] = file.listFiles();
  			for (int i = 0; i < children.length; i++) {
  				successful = FileUtil.deleteFileAndChildren(children[i]);
  				if (! successful) {
  					return false;
  				}
  			}
  			// folder is now empty
  		}
  		return file.delete();
  	}
  	return true;
  }
  			
    /**
   * deletes entire contents of a folder. Returns true if deletion successful, false otherwise
   * 
   * comment added by thomas:
   * doesn't delete folders that contain nonempty folders
   */
  public static boolean deleteAllFilesInDirectory(String path){
   	File folder = new File(path);
    if(folder!=null && folder.exists() && folder.isDirectory()){
      File[] files = folder.listFiles();
      if(files!=null){
	      for (int i = 0; i < files.length; i++) {
	       files[i].delete();
	      }
	  }
      return true;
    }
    return false;
  }

	/**
  * Returns the entire contents of a folder. Returns NULL if no files exist.
  */
	public static File[] getAllFilesInDirectory(String path){
		File folder = new File(path);
		if(folder.exists()){
			return folder.listFiles();
		}
		return null;
	}
	
	/** 
	 * Returns folders of a folder. Returns null if no folders exist.
	 * @param path
	 * @return
	 */
	public static List getDirectoriesInDirectory(String path) {
		File folder = new File(path);
		if (folder.exists()) {
			FileFilter filter = new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory();
				}
			};
			File[] folders = folder.listFiles(filter);
			return Arrays.asList(folders);
		}
		return null;
	}

  public static List getLinesFromFile(File fromFile) throws IOException{
    List strings = new ArrayList();

    FileReader reader;
    LineNumberReader lineReader = null;

    reader = new FileReader(fromFile);
    lineReader = new LineNumberReader(reader);

    lineReader.mark(1);
    while (lineReader.read() != -1) {
        lineReader.reset();
        strings.add(lineReader.readLine());
        lineReader.mark(1);
    }

    return strings;
  }

/** Gets the lines from a file and return the as a vector of strings **/
  public static List getLinesFromFile(String pathAndFile) throws IOException{
    File f = new File(pathAndFile);
    return getLinesFromFile(f);
  }

/** Uses getLinesFromFile and returns them in a string with "\n" between them **/
  public static String getStringFromFile(String pathAndFile) throws IOException{
    StringBuffer buffer = new StringBuffer();
    List list = getLinesFromFile(pathAndFile);
    if ( list != null ) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        buffer.append((String) iter.next());
        buffer.append('\n');
      }
    }
    return buffer.toString();
  }




/** This uses a BufferInputStream and an URLConnection to get an URL and return it as a String **/
  public static String getStringFromURL(String uri){
    StringBuffer buffer = new StringBuffer("");
    String line;
    BufferedInputStream bin;
    BufferedReader in;
    URL url;

    try {
      url = new URL(uri);
      bin = new BufferedInputStream(url.openStream());
      in = new BufferedReader(new InputStreamReader(bin));
      //Put the contents in a string
      while ((line = in.readLine()) != null) {
        buffer.append(line);
        buffer.append('\n');
      }
      in.close();
    }
    catch(MalformedURLException mue) { // URL c'tor
      return "MalformedURLException: Site not available or wrong url";
    }
    catch(IOException ioe) { // Stream constructors
      return "IOException: Site not available or wrong url";
    }

    return buffer.toString();
  }


/** This uses a BufferInputStream and an URLConnection to get an URL and return it as a String **/
  public static void getURLToFile(String uri,File file){
    //FileOutputStream fos = new FileOutputStream(file);
    FileWriter writer= null;
    String line;
    BufferedInputStream bin;
    BufferedReader in;
    URL url;

    try {
      writer = new FileWriter(file);
      url = new URL(uri);
      bin = new BufferedInputStream(url.openStream());
      in = new BufferedReader(new InputStreamReader(bin));
      //Put the contents in a string
      while ((line = in.readLine()) != null) {
        writer.write(line);
        writer.write('\n');
      }
      in.close();
      writer.close();
    }
    catch(MalformedURLException mue) { // URL c'tor
      //return "MalformedURLException: Site not available or wrong url";
      mue.printStackTrace();
    }
    catch(IOException ioe) { // Stream constructors
      //return "IOException: Site not available or wrong url";
      ioe.printStackTrace();
    }

  }





  /** uses getLinesFromFile and cuts the lines into java.util.StringTokenizer and returns them in a vector **/

   public static List getCommaSeperatedTokensFromLinesFromFile(String pathAndFile, String seperatorToken) throws IOException{
    List lines = getLinesFromFile(pathAndFile);
    List tokens = new ArrayList();
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


  public static void copyFile(File sourceFile,String newFileName)throws java.io.FileNotFoundException,java.io.IOException{
    File newFile = new File(newFileName);
    if(!newFile.exists()){
      newFile.createNewFile();
    }
    copyFile(sourceFile,newFile);
  }

  public static void copyFile(File sourceFile,File newFile)throws java.io.FileNotFoundException,java.io.IOException{

    java.io.FileInputStream input = new java.io.FileInputStream(sourceFile);
    if(!newFile.exists()){
      newFile.createNewFile();
    }
    java.io.FileOutputStream output  = new FileOutputStream(newFile);


    byte buffer[]= new byte[1024];
    int	noRead	= 0;

    noRead = input.read( buffer, 0, 1024 );

    //Write out the stream to the file
    while ( noRead != -1 ){
      output.write( buffer, 0, noRead );
      noRead = input.read( buffer, 0, 1024 );
    }
    output.flush();
    output.close();


  }

  /**
   * Gunzips one file from the inputGZippedFile and unzips to outputUnZippedFile
   */
  public static void gunzipFile(File inputGZippedFile,File outputFile)throws java.io.FileNotFoundException,java.io.IOException{
    java.util.zip.GZIPInputStream input = new java.util.zip.GZIPInputStream(new java.io.FileInputStream(inputGZippedFile));
    java.io.FileOutputStream output = new java.io.FileOutputStream(outputFile);
    //java.util.zip.GZIPOutputStream output = new java.util.zip.GZIPOutputStream();

    int buffersize=100;
    byte[] buf = new byte[buffersize];
    int read = input.read(buf,0,buffersize);

    while(read!=-1){
      output.write(buf,0,read);
      read = input.read(buf,0,buffersize);
    }
  }


  public static void copyDirectoryRecursively(File inputDirectory,File outputDirectory)throws java.io.IOException{
    if(inputDirectory.isDirectory()){
      if(!outputDirectory.exists()){
        outputDirectory.mkdir();
      }

      File tempOutFile;
      File inputFile;
      File[] files = inputDirectory.listFiles();
      for (int i = 0; i < files.length; i++) {
        inputFile = files[i];
        String name = inputFile.getName();
        tempOutFile = new File(outputDirectory,name);
        if(inputFile.isDirectory()){
          copyDirectoryRecursively(inputFile,tempOutFile);
        }
        else{
          tempOutFile.createNewFile();
          copyFile(inputFile,tempOutFile);
        }
      }

    }
    else{
      throw new IOException(inputDirectory.toString()+" is not a directory");
    }
  }
}
