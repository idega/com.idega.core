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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.idega.core.file.data.ICFile;
import com.idega.file.bean.EmptyItem;
import com.idega.file.bean.FileItem;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.repository.bean.RepositoryItem;
import com.idega.servlet.filter.IWBundleResourceFilter;

public class FileUtil {

	private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

  public static final char UNIX_FILE_SEPARATOR = '/';
  public static final char WINDOWS_FILE_SEPARATOR = '\\';
  public static final String BACKUP_SUFFIX = "backup~";

  public static final FileUtil instance = new FileUtil();

  private FileUtil() {
  	// empty
  }

  public static final FileUtil getInstance() {
	  return instance;
  }

  public static boolean exists(String path, String fileNameWithoutFullPath) {
	  String filePath = getFileNameWithPath(path, fileNameWithoutFullPath);
	  return exists(filePath);
  }

  public static boolean exists(String fileNameWithFullPath)  {
	  File file = new File(fileNameWithFullPath);
	  return file.exists();
  }

  public Boolean getExistence(String fileNameWithPath) {
	  return FileUtil.exists(fileNameWithPath);
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
    File folder = getFolder(path);
    return folder == null || !folder.exists() ? false : true;
  }

  public static File getFolder(String path) {
	  File folder = new File(path);
	  if (!folder.exists()) {
	      folder.mkdirs();
	      return folder;
	  }
	  return folder;
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
   * 	 Creates a file or folder if not existent.
   * 	 If true is returned the file exists else something went wrong.
   *
   * @param file
   * @return true if everything is fine else false
   * @author thomas
   */
  public static boolean createFileIfNotExistent(File file) {
  	if (file.exists()) {
  		// nothing to do
  		return true;
  	}
  	// create parent directories (or the folder itself) if necessary
  	boolean isDirectory = file.isDirectory();
	File parentFile = (isDirectory) ? file : file.getParentFile();
	if (parentFile != null && !parentFile.exists()) {
		if (! parentFile.mkdirs()) {
			return false;
		}
	}
	// create file (if it is not a directory)
	if (! isDirectory) {
		try {
			file.createNewFile();
		}
		catch (IOException ex) {
			return false;
		}
	}
	if (file.isDirectory()) {
		LOGGER.info("Weser");
	}
	// everything is okay
	return true;
  }


  /**
   * Gets the System wide path separator
   *
   */
public static String getFileSeparator(){
    return File.separator;
  }

  public static String getFileNameWithPath(String path, String fileNameWithoutFullPath) {
	  if (!path.endsWith(File.separator)) {
		path = path.concat(File.separator);
	}
	  if (fileNameWithoutFullPath.startsWith(File.separator)) {
		fileNameWithoutFullPath = fileNameWithoutFullPath.substring(1);
	}
	  return path.concat(fileNameWithoutFullPath);
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
        LOGGER.log(Level.WARNING, "Error making directory: " + path, e);
      }
      File file = null;

      if(dirs!=null) {
				file = new File(dirs,name);
			}
			else {
				file = new File(name);
			}

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
    public static void streamToFile(InputStream input, File toFile) throws IOException{
    	FileOutputStream out = new FileOutputStream(toFile);
    	streamToOutputStream(input, out);
    }

    /*
    * streams an inputstream to a file
    */
    public static File streamToFile(InputStream input, String filePath, String fileName) {
    	return streamToFile(input, filePath, fileName, Boolean.TRUE);
    }

    public static File streamToFile(InputStream input, String filePath, String fileName, boolean closeStream) {
    	File file = null;
        try {
        	if (input == null) {
        		LOGGER.warning("Provided InputStream is undefined!");
        		return null;
        	}

        	input.available();	//	This method throws an IO exception if the stream is invalid
        	file = getFileAndCreateIfNotExists(filePath, fileName);
        	FileOutputStream fileOut = new FileOutputStream(file);
        	streamToOutputStream(input, fileOut, closeStream);
        } catch(IOException e) {
        	LOGGER.log(Level.WARNING, "Error writing to file: " + filePath + fileName + ": " + e.getMessage());
        }

        return file;
    }

  public static void streamToOutputStream( InputStream input, OutputStream out) throws IOException {
	  streamToOutputStream(input, out, Boolean.TRUE);
  }

  private static void streamToOutputStream(InputStream input, OutputStream out, boolean closeInputStream) throws IOException {
	  BufferedInputStream buffered = null;
	  try {
	      if (input == null) {
	    	  LOGGER.warning("Provided InputStream is undefined!");
	    	  return;
	      }

	      buffered = new BufferedInputStream(input);
	      buffered.available();

	      int bytesRead;
	      byte[] buf = new byte[4 * 1024];
	      while ((bytesRead = buffered.read(buf)) != -1) {
//	    	  try {
//	    		  // slow internet simulation
//	    		  Thread.sleep(20);
//	    	  } catch (Exception e) {}
	    	  out.write(buf, 0, bytesRead);
	      }

	      out.flush();
	      out.close();

      } finally {
    	  if (closeInputStream) {
    		  IOUtil.close(input);
    		  IOUtil.close(buffered);
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
  	return FileUtil.deleteContentOfFolder(new File(path));
  }


    /**
   * Deletes content of folder.
   * !! Be careful !!
   * Returns also false if the specified path doesn't exist.
   * @author thomas
   */
  public static boolean deleteContentOfFolder(File folder) {
  	boolean result = true;
  	boolean successful = true;
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
   * Recursively delete not empty directory.
   * @author arunas
   */
  public static boolean deleteNotEmptyDirectory(String path) {
	return FileUtil.deleteNotEmptyDirectory(new File(path));
  }

  /**
   * Recursively delete not empty directory.
   * @author arunas
   */

  public static boolean deleteNotEmptyDirectory(File directory) {
      if (directory.exists() && directory.isDirectory()) {
	  String [] children = directory.list();
	  for (int i = 0; i < children.length; i++) {
	    boolean success = deleteNotEmptyDirectory(new File(directory,children[i]));
	    if (!success) {
			return false;
		}
	}

    }
      return directory.delete();
  }

  /** Deletes all files and folders in the specified folder that are older than the
   * specified time in milliseconds. Only the time of the files and folders in the specified folder
   * are checked not the time of files or folders that belong to subfolders.
   * !! Be careful !!
   * @param folderPath
   * @param timeInMillis
   * @author thomas
   */
  public static void deleteAllFilesAndFolderInFolderOlderThan(String folderPath, long timeInMillis) {
  	FileUtil.deleteAllFilesAndFolderInFolderOlderThan(new File(folderPath), timeInMillis);
  }


  /** Deletes all files and folders in the specified folder that are older than the
   * specified time in milliseconds. Only the time of the files and folders in the specified folder
   * are checked not the time of files or folders that belong to subfolders.
   * !! Be careful !!
   * @param folderPath
   * @param timeInMillis
   * @author thomas
   */
  public static void deleteAllFilesAndFolderInFolderOlderThan(File folder, long timeInMillis) {
  	if (! folder.exists()) {
  		return;
  	}
    File[] files = folder.listFiles();
    if(files!=null){
    	long currentTime = System.currentTimeMillis();
  	    for (int i = 0; i < files.length; i++) {
  	    	File file = files[i];
  	    	long modifiedFile = file.lastModified();
  	    	if (currentTime - modifiedFile > timeInMillis)	{
  	    		FileUtil.deleteFileAndChildren(file);
  	    		LOGGER.info("Deleted " + file + " and it's children. Last time it was modified " + new IWTimestamp(modifiedFile));
  	    	}
  	    }
    	}
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
	 * Returns only files of a folder but not folder inside the folder. Returns null if no folder exist.
	 * @param path
	 * @return
	 * @author thomas
	 */
	public static List<File> getFilesInDirectory(File folder) {
		if (folder.exists()) {
			FileFilter filter = new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.isFile();
				}
			};
			File[] folders = folder.listFiles(filter);
			return Arrays.asList(folders);
		}
		return null;
	}

	/**
	 * Returns folders of a folder. Returns null if no folders exist.
	 * @param path
	 * @return
	 * @author thomas
	 */
	public static List<File> getDirectoriesInDirectory(File folder) {
		if (folder.exists()) {
			FileFilter filter = new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.isDirectory();
				}
			};
			File[] folders = folder.listFiles(filter);
			return Arrays.asList(folders);
		}
		return null;
	}

  public static List<String> getLinesFromFile(File fromFile) throws IOException{
    List<String> strings = new ArrayList<>();

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

    lineReader.close();

    return strings;
  }

  	public static final File getFileFromWorkspace(String pathToFile) throws IOException {
  		File file = new File(pathToFile);
	    if (!file.exists()) {
	    	int virtualPathStart = pathToFile.indexOf("/idegaweb/bundles/");
	    	if (virtualPathStart != -1) {
	    		file = IWBundleResourceFilter.copyResourceFromJarToWebapp(IWMainApplication.getDefaultIWMainApplication(), pathToFile.substring(virtualPathStart));
	    	}
	    }
	    if (file != null && file.exists()) {
	    	return file;
	    }

	    throw new IOException("File '" + pathToFile + "' doesn't exist!");
  	}

/** Gets the lines from a file and return the as a vector of strings **/
  public static List<String> getLinesFromFile(String pathAndFile) throws IOException{
    File f = new File(pathAndFile);
    if (!f.exists()) {
    	f = getFileFromWorkspace(pathAndFile);
    }
    return getLinesFromFile(f);
  }

/** Uses getLinesFromFile and returns them in a string with "\n" between them **/
  public static String getStringFromFile(String pathAndFile) throws IOException{
	  StringBuffer buffer = new StringBuffer();
	  List<String> lines = getLinesFromFile(pathAndFile);
	  if (lines != null) {
		  for (String line: lines) {
			  buffer.append(line).append('\n');
		  }
	  }
	  return buffer.toString();
  }

  /** Uses getLinesFromFile and returns them in a string with "\n" between them **/
  public static String getStringFromFile(File file) throws IOException{
	  StringBuffer buffer = new StringBuffer();
	  List<String> lines = getLinesFromFile(file);
	  if (lines != null) {
		  for (String line: lines) {
			  buffer.append(line).append('\n');
		  }
	  }
	  return buffer.toString();
  }

  /** Gets a file relative to the specified file according the specified path.
   * Note: Works with windows or unix separators.
   * E.g.
   * file = "/a/b/c.txt" (is a file)
   * path = "../d/e.txt"
   * returns file = "/a/d/e.txt"
   * E.g.
   * file = "/a/b/c" (is a folder)
   * path = "../d/e.txt"
   * returns file = "/a/b/d/e.txt"
   * @param file
   * @param relativePath
   * @return
   * @author thomas
   */
  public static File getFileRelativeToFile(File file, String relativePath) {
  	char[] separatorsChar = {UNIX_FILE_SEPARATOR , WINDOWS_FILE_SEPARATOR};
  	String separators = new String(separatorsChar);
  	File result = file;
  	if (result.isFile()) {
  		result = result.getParentFile();
  	}
  	StringTokenizer tokenizer = new StringTokenizer(relativePath, separators);
  	while (tokenizer.hasMoreTokens()) {
  		String token = tokenizer.nextToken();
  		if (".".equals(token)) {
  			// do nothing
  		}
  		else if ("..".equals(token)) {
  			// go to parent
			result = result.getParentFile();
  		}
  		else {
  			// go to child
  			result = new File(result, token);
  		}
  	}
  	return result;
  }

  /** Creates a file plus folders relative to the specified (existing) file according the specified path.
   * Note: Works with windows or unix separators.
   * + returns the specified file if the path is null or empty
   * + returns a folder if the specified path ends with a separator
   * E.g.
   * file = "/a/b/c.txt" (is a file)
   * path = "../d/e.txt"
   * returns file = "/a/d/e.txt"
   * E.g.
   * file = "/a/b/c" (is a folder)
   * path = "../d/e.txt"
   * returns file = "/a/b/d/e.txt"
   * @param file
   * @param relativePath
   * @return
   * @author thomas
   */
  public static File createFileRelativeToFile(File file, String relativePath) throws IOException {
  	char[] separatorsChar = {UNIX_FILE_SEPARATOR ,WINDOWS_FILE_SEPARATOR};
  	String separators = new String(separatorsChar);
  	if (! file.exists()) {
  		throw new IOException("[FileUtil] File does not exist: "+ file.getPath());
  	}
  	if (relativePath == null) {
  		return file;
  	}
  	int length = relativePath.length();
  	if (length <= 0) {
  		// relativePath is empty
  		return file;
  	}
  	char lastCharacter = relativePath.charAt(--length);
  	// if the relative path ends with a separator create a folder!
  	// e.g. relative path is "/a/b/" that is ends with a separator
  	boolean resultShouldBeAFolder = separators.indexOf(lastCharacter) != -1;
  	File result = file;
  	if (result.isFile()) {
  		result = result.getParentFile();
  	}
  	StringTokenizer tokenizer = new StringTokenizer(relativePath, separators);
  	boolean hasMoreTokens = tokenizer.hasMoreTokens();
  	while (hasMoreTokens) {
  		String token = tokenizer.nextToken();
		hasMoreTokens = tokenizer.hasMoreTokens();
  		if (".".equals(token)) {
  			// do nothing
  		}
  		else if ("..".equals(token)) {
  			// go to parent
			result = result.getParentFile();
  		}
  		else {
  			// do something
  			result = new File(result, token);
  			if (! result.exists()) {
  				// do something
  				boolean success = false;
  				if (hasMoreTokens || resultShouldBeAFolder) {
  					// create a folder
  					success = result.mkdir();
  				}
  				else {
  					// it is a file
  					success = result.createNewFile();
  				}
				if (! success) {
  					throw new IOException("[FileUtil] File could not be created: " + result.getPath());
  				}
  			}
  		}
  	}
  	return result;
  }

  /** This uses a BufferInputStream and an URLConnection to get an URL and return it as a String.  Uses UTF-8 as default encoding **/
  public static String getStringFromURL(String uri){
	  return getStringFromURL(uri, "UTF-8");
  }

  /** This uses a BufferInputStream and an URLConnection to get an URL and return it as a String **/
  public static String getStringFromURL(String uri, String encoding){
    StringBuffer buffer = new StringBuffer("");
    String line;
    BufferedInputStream bin;
    BufferedReader in;
    URL url;

    try {
      url = new URL(uri);
      bin = new BufferedInputStream(url.openStream());
      in = new BufferedReader(new InputStreamReader(bin, encoding));
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


  /**
   * Works well to e.g. save images from a website to a file
   * @param uri
   * @param file
   */
  public static void createFileFromURL(String uri,File file){
  	try {
  		URL url = new URL(uri);
  		BufferedInputStream input = new BufferedInputStream(url.openStream());

  		FileOutputStream output  = new FileOutputStream(file);

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
  	catch(MalformedURLException mue) { // URL c'tor
  		//return "MalformedURLException: Site not available or wrong url";
  		LOGGER.log(Level.WARNING, "Site not available or wrong url", mue);
  	}
  	catch(IOException ioe) { // Stream constructors
  		//return "IOException: Site not available or wrong url";
  		LOGGER.log(Level.WARNING, "Site not available or wrong url", ioe);
  	}

  }




  /** uses getLinesFromFile and cuts the lines into java.util.StringTokenizer and returns them in a vector **/

   public static List<StringTokenizer> getCommaSeperatedTokensFromLinesFromFile(String pathAndFile, String seperatorToken) throws IOException{
    List<String> lines = getLinesFromFile(pathAndFile);
    List<StringTokenizer> tokens = new ArrayList<>();

    StringTokenizer tokenizer;

    for (String line: lines) {
      tokenizer = new StringTokenizer(line, seperatorToken);
      tokens.add(tokenizer);
    }


    return tokens;
  }


   /**
    * Copies the specified sourcefile (source folder) to a backup file (backup folder), creates always a new
    * backup file without destroying an existing old backup file
    * by adding a number to the suffix if necessary.
    * e.g. hello.txt -> hello.txt.backup~
    * e.g. hello.txt -> hello.txt.1_backup~
    *
    * @param sourceFile
    * @throws IOException
    * @throws FileNotFoundException
    * @author thomas
    */
   public static void backup(File sourceFile) throws FileNotFoundException, IOException {
   		FileUtil.backupToFolder(sourceFile, null);
   }



   /**
    * Copies the specified sourcefile (source folder) to a backup file (backup folder) into the specified backup folder,
    * creates always a new
    * backup file without destroying an existing old backup file
    * by adding a number to the suffix if necessary.
    * e.g. hello.txt -> hello.txt.backup~
    * e.g. hello.txt -> hello.txt.1_backup~
    *
    * @param sourceFile
    * @param destination
    * @throws IOException
    * @throws FileNotFoundException
    * @author thomas
    */
   public static void backupToFolder(File sourceFile, File destination) throws FileNotFoundException, IOException {
   	String name = sourceFile.getName();
   	if (destination == null) {
   		destination = sourceFile.getParentFile();
   		if (destination == null) {
   			// can not copy the root
   			throw new IOException("[FileUtil] Can not backup root");
   		}
   	}
   	else if (! destination.exists()) {
   		if (! destination.mkdirs()) {
   			throw new IOException("[FileUtil] Can not create backup destination folder: " + destination.getAbsolutePath());
   		}
   	}
   	StringBuffer buffer = null;
   	File backupFile = null;
   	int i = 0;
   	do {
   		buffer = new StringBuffer(name);
   		buffer.append(".");
   		if (i > 0) {
   			buffer.append(i).append("_");
   		}
   		buffer.append(BACKUP_SUFFIX);
   		backupFile = new File(destination, buffer.toString());
   		i++;
   	}
   	while (backupFile.exists());
   	if (sourceFile.isDirectory()) {
   		copyDirectoryRecursivelyKeepTimestamps(sourceFile, backupFile);
   	}
   	else {
   		copyFileKeepTimestamp(sourceFile, backupFile);
   	}
   }

  public static void copyFile(File sourceFile,String newFileName)throws java.io.FileNotFoundException,java.io.IOException{
    File newFile = new File(newFileName);
    copyFile(sourceFile,newFile);
  }

  public static void copyFileKeepTimestamp(File sourceFile, File newFile) throws FileNotFoundException, IOException {
  	FileUtil.copyFile(sourceFile, newFile);
  	long oldTimestamp = sourceFile.lastModified();
  	newFile.setLastModified(oldTimestamp);
  }

  public static void splitFileContentIntoLines(File sourceFile, File newFile, int lineLength) throws FileNotFoundException, IOException {
	  InputStreamReader input = new InputStreamReader(new FileInputStream(sourceFile), CoreConstants.ENCODING_UTF8);
	  if (!newFile.exists()) {
		  newFile.createNewFile();
	  }

	  FileOutputStream output  = new FileOutputStream(newFile);
	  int bufferSize = lineLength;
	  char buffer[]= new char[bufferSize];
	  int noRead = 0;

	  noRead = input.read(buffer, 0, bufferSize);

	  //Write out the stream to the file
	  byte[] lineSeparator = System.lineSeparator().getBytes();
	  while (noRead != -1) {
		  output.write(new String(buffer).getBytes(CoreConstants.ENCODING_UTF8), 0, noRead);
		  output.write(lineSeparator);
		  noRead = input.read(buffer, 0, bufferSize);
	  }

	  output.flush();
	  output.close();
	  input.close();
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
    input.close();

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

    input.close();
    output.close();
  }

  public static final File getZippedFiles(Collection<File> filesToZip) throws IOException {
	  return getZippedFiles(filesToZip, "Archive.zip");
  }
  public static final File getZippedFiles(Collection<File> filesToZip, String fileName) throws IOException {
	  return getZippedFiles(filesToZip, fileName, Boolean.TRUE);
  }
  public static final File getZippedFiles(Collection<File> filesToZip, String fileName, Boolean deleteZippedFiles) throws IOException {
	  if (ListUtil.isEmpty(filesToZip) || StringUtil.isEmpty(fileName)) {
		  return null;
	  }

	  Collection<RepositoryItem> files = new ArrayList<>(filesToZip.size());
	  for (File file: filesToZip) {
		  files.add(new FileItem(file));
	  }
	  return getZippedFiles(files, fileName, deleteZippedFiles);
  }

  public static final File getZippedItems(Collection<RepositoryItem> itemsToZip, String fileName) throws IOException {
	  return getZippedFiles(itemsToZip, fileName, true);
  }

  public static final File getZippedFiles(Collection<RepositoryItem> filesToZip, String fileName, boolean deleteZippedFiles) throws IOException {
	  return getZippedFiles(filesToZip, fileName, deleteZippedFiles, false);
  }

  public static final File getZippedFiles(
		  Collection<RepositoryItem> filesToZip,
		  String fileName,
		  boolean deleteZippedFiles,
		  boolean handleEmptyDir
  ) throws IOException {
	  if (StringUtil.isEmpty(fileName)) {
		  return null;
	  }

	  if (ListUtil.isEmpty(filesToZip) && !handleEmptyDir) {
		  return null;
	  }

	  File zippedFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
	  if (!zippedFile.exists()) {
		  zippedFile.createNewFile();
	  }
	  if (filesToZip == null) {
		  filesToZip = new ArrayList<>(1);
	  }
	  if (filesToZip.size() == 0) {
		  filesToZip.add(new EmptyItem(CoreConstants.EMPTY));
	  }

	  ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zippedFile));

	  Integer bytesRead = 0;
	  CRC32 crc = new CRC32();
	  byte[] buffer = new byte[1024];

	  doAddZipEntries(CoreConstants.EMPTY, filesToZip, crc, bytesRead, buffer, zos, deleteZippedFiles);

	  zos.close();

	  return zippedFile;
  }

  private static void doAddZipEntries(
		  String namePrefix,
		  Collection<RepositoryItem> filesToZip,
		  CRC32 crc,
		  Integer bytesRead,
		  byte[] buffer,
		  ZipOutputStream zos,
		  boolean deleteZippedFiles
  ) throws IOException {
	  if (ListUtil.isEmpty(filesToZip)) {
		  return;
	  }

	  for (RepositoryItem item: filesToZip) {
		  if (item.isDirectory()) {
			  Collection<RepositoryItem> children = item.getChildren();
			  doAddZipEntries(
					  StringUtil.isEmpty(namePrefix) ? item.getName() : namePrefix + File.separator + item.getName(),
					  children,
					  crc,
					  bytesRead,
					  buffer,
					  zos,
					  deleteZippedFiles
			  );
		  } else {
			  doAddZipEntry(namePrefix, item, crc, bytesRead, buffer, zos, deleteZippedFiles);
		  }
	  }
  }

  private static void doAddZipEntry(
		  String namePrefix,
		  RepositoryItem item,
		  CRC32 crc,
		  Integer bytesRead,
		  byte[] buffer,
		  ZipOutputStream zos,
		  boolean deleteZippedFiles
  ) throws IOException {
	  InputStream bis = new BufferedInputStream(item.getInputStream());
	  crc.reset();
	  while ((bytesRead = bis.read(buffer)) != -1) {
          crc.update(buffer, 0, bytesRead);
      }
      bis.close();

      //	Adding new entry
      long itemSize = item.getLength();
      itemSize = itemSize < 0 ? 0 : itemSize > 0xFFFFFFFFL ? Long.MAX_VALUE : itemSize;
      bis = new BufferedInputStream(item.getInputStream());

      String name = item.getName();
      if (!StringUtil.isEmpty(namePrefix)) {
    	  name = namePrefix + File.separator + name;
      }
      ZipEntry entry = new ZipEntry(name);

      entry.setMethod(ZipEntry.STORED);
      entry.setCompressedSize(itemSize);
      entry.setSize(itemSize);
      entry.setCrc(crc.getValue());
      zos.putNextEntry(entry);
      while ((bytesRead = bis.read(buffer)) > 0) {
          zos.write(buffer, 0, bytesRead);
      }
      bis.close();

      if (deleteZippedFiles) {
    	  item.delete();
      }
  }

  public static final File getZippedICFiles(Collection<ICFile> filesToZip, String fileName) throws IOException {
	  if (ListUtil.isEmpty(filesToZip) || StringUtil.isEmpty(fileName)) {
		  return null;
	  }

	  ArrayList<RepositoryItem> files = new ArrayList<> (filesToZip.size());
	  for (ICFile file : filesToZip) {
		  files.add(new FileItem(file));
	  }
	  return getZippedFiles(files, fileName, false);
  }

  public static void copyDirectoryRecursivelyKeepTimestamps(File inputDirectory,File outputDirectory) throws IOException {
  	FileUtil.copyDirectoryRecursively(inputDirectory, outputDirectory, true);
  }

  public static void copyDirectoryRecursively(File inputDirectory, File outputDirectory) throws IOException {
  	FileUtil.copyDirectoryRecursively(inputDirectory, outputDirectory, false);
  }


  private static void copyDirectoryRecursively(File inputDirectory,File outputDirectory, boolean keepTimestamp ) throws java.io.IOException{
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
          copyDirectoryRecursively(inputFile,tempOutFile,  keepTimestamp);
        }
        else if (keepTimestamp) {
          FileUtil.copyFileKeepTimestamp(inputFile,tempOutFile);
        }
        else {
        	FileUtil.copyFile(inputFile, tempOutFile);
        }
      }
      if (keepTimestamp) {
      	long oldTimestamp = inputDirectory.lastModified();
      	outputDirectory.setLastModified(oldTimestamp);
      }
    }
    else{
      throw new IOException(inputDirectory.toString()+" is not a directory");
    }
  }

  /**
   *
   * @param folder to search filed and directories, not <code>null</code>;
   * @return all folders and files in given directory or
   * {@link Collections#emptyList()} on failure;
   * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
   */
  public static List<String> getAllFilesRecursively(File folder){
	  ArrayList<String> filepaths = new ArrayList<>();

	  if (folder != null) {
		  if (folder.isDirectory()) {
			  File[] innerFolders = folder.listFiles();
			  if (!ArrayUtil.isEmpty(innerFolders)) {
				  for (File innerFolder: innerFolders) {
					  filepaths.addAll(getAllFilesRecursively(innerFolder));
				  }
			  }
		  }

		  filepaths.add(folder.getAbsolutePath());
	  }

	  return filepaths;
  }

  /**
   *
   * @param directory to search filed and directories, not <code>null</code>;
   * @return all folders and files in given directory or
   * {@link Collections#emptyList()} on failure;
   * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
   */
  public static List<String> getAllFilesRecursively(String directory) {
	  if (!StringUtil.isEmpty(directory)) {
		  return getAllFilesRecursively(new File(directory));
	  }

	  return Collections.emptyList();
  }

  /**
   * Converts a long number representing bytes into human readable format.
   * <p>
   * This value is mainly for formating values like a file size, memory size
   * and so forth, so instead of seeing a large incoherent number you can see
   * something like '308.123KB' or '9.68MB'
   * @param bytes to format into a string.
   * @return human readable format for larger byte counts.
   */
  public static String getHumanReadableSize(long bytes) {

      long mb = (long) Math.pow(2, 20);
      long kb = (long) Math.pow(2, 10);
      long gb = (long) Math.pow(2, 30);
      long tb = (long) Math.pow(2, 40);

      NumberFormat nf = NumberFormat.getNumberInstance();
      nf.setMaximumFractionDigits(1);
      double relSize = 0.0d;
      long abytes = Math.abs(bytes);
      String id = "";
      if ((abytes / tb) >= 1) {
        relSize = (double) abytes / (double) kb;
        id = "TB";
      }
      else if ((abytes / gb) >= 1) {
          relSize = (double) abytes / (double) gb;
          id = "GB";
      } else if ((abytes / mb) >= 1) {
          relSize = (double) abytes / (double) mb;
          id = "MB";
      } else if ((abytes / kb) >= 1) {
          relSize = (double) abytes / (double) kb;
          id = "KB";
      }
      else {
          relSize = abytes;
          id = "b";
      }
      return nf.format((bytes < 0 ? -1 : 1) * relSize) + " "+id;
  }

  /**
   * <p>
   * Gets the "head" of a text file, as a List of Strings with the first lines with length numberOfLinesToRead
   * </p>
   * @param file
   * @param encoding
   * @param numberOfLinesToRead
   * @return
   * @throws IOException
   */
  public static List<String> head(File file, int numberOfLinesToRead) throws IOException
  {
      return head(file, CoreConstants.ENCODING_UTF8 , numberOfLinesToRead);
  }

  /**
   * <p>
   * Gets the "head" of a text file, as a List of Strings with the first lines with length numberOfLinesToRead
   * </p>
   * @param file
   * @param encoding
   * @param numberOfLinesToRead
   * @return
   * @throws IOException
   */
  public static List<String> head(File file, String encoding, int numberOfLinesToRead) throws IOException
  {
      //assert (file != null) && file.exists() && file.isFile() && file.canRead();
      //assert numberOfLinesToRead > 0;
      //assert encoding != null;

      LinkedList<String> lines = new LinkedList<>();
      BufferedReader reader= new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
      for (String line = null; (numberOfLinesToRead-- > 0) && (line = reader.readLine()) != null;)
      {
          lines.addLast(line);
      }
      reader.close();
      return lines;
  }
  /**
   * <p>
   * Gets the "tail" of a text file, as a List of Strings of the last lines with length numberOfLinesToRead
   * </p>
   * @param file
   * @param encoding
   * @param numberOfLinesToRead
   * @return
   * @throws IOException
   */
  public static List<String> tail(File file, int numberOfLinesToRead) throws IOException
  {
      return tail(file, CoreConstants.ENCODING_UTF8 , numberOfLinesToRead);
  }

  /**
   * <p>
   * Gets the "tail" of a text file, as a List of Strings of the last lines with length numberOfLinesToRead
   * </p>
   * @param file
   * @param encoding
   * @param numberOfLinesToRead
   * @return
   * @throws IOException
   */
  public static List<String> tail(File file, String encoding, int numberOfLinesToRead) throws IOException
  {
      //assert (file != null) && file.exists() && file.isFile() && file.canRead();
      //assert numberOfLinesToRead > 0;
      //assert (encoding != null) && encoding.matches("(?i)(iso-8859|ascii|us-ascii).*");

      LinkedList<String> lines = new LinkedList<>();
      BufferedReader reader= new BufferedReader(new InputStreamReader(new BackwardsFileInputStream(file), encoding));
      for (String line = null; (numberOfLinesToRead-- > 0) && (line = reader.readLine()) != null;)
      {
          // Reverse the order of the characters in the string
          char[] chars = line.toCharArray();
          for (int j = 0, k = chars.length - 1; j < k ; j++, k--)
          {
              char temp = chars[j];
              chars[j] = chars[k];
              chars[k]= temp;
          }
          lines.addFirst(new String(chars));
      }
      reader.close();
      return lines;
  }

  public static final boolean isDownloadFileViaUniqueIdOnly(IWMainApplicationSettings settings) {
	  settings = settings == null ? IWMainApplication.getDefaultIWMainApplication().getSettings() : settings;
	  return settings.getBoolean("file.access_via_unique_id_and_token", true);
  }

  public static final boolean isFilesCacheTurneOff(IWMainApplicationSettings settings) {
	  settings = settings == null ? IWMainApplication.getDefaultIWMainApplication().getSettings() : settings;
	  return settings.getBoolean("file.cache_turned_off", true);
  }

}