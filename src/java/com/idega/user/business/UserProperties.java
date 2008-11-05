package com.idega.user.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWPropertyList;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.FileUtil;

/**
 * @author Laddi
 */
public class UserProperties extends IWPropertyList implements HttpSessionBindingListener {

	private int userID = -1;
	
	public UserProperties(IWMainApplication application,int userID) {
		super((File) null);
//		super(application.getPropertiesRealPath() + FileUtil.getFileSeparator() + "users", "user_"+String.valueOf(userID)+"_properties.pxml", true);
		this.userID = userID;
		String path = application.getPropertiesRealPath() + FileUtil.getFileSeparator() + "users";
		String fileNameWithoutFullPath = "user_"+String.valueOf(userID)+"_properties.pxml";
		File file = createFile(path, fileNameWithoutFullPath);
		load(file);
	}
	
	@Override
	protected File createFile(String path, String fileNameWithoutFullPath) {
		File file = null;
		try {
			UserHome uHome = (UserHome) IDOLookup.getHome(User.class);
			User user = uHome.findByPrimaryKey(new Integer(userID));
			ICFile icFile = user.getUserProperties();
			if (icFile != null) {
				file = FileUtil.streamToFile(icFile.getFileValue(), path+FileUtil.getFileSeparator(), "tmp_"+fileNameWithoutFullPath);
				return file;
			} else {
				file = new File(path, fileNameWithoutFullPath);
				// added 08.02.2002 by aron: was before
				// if(!file.exists() )
				if (!file.exists() || file.length() == 0) {
					Logger.getLogger(this.getClass().getName()).info("Creating new " + fileNameWithoutFullPath);
					file = FileUtil.getFileAndCreateIfNotExists(path, fileNameWithoutFullPath);
					FileOutputStream stream = new FileOutputStream(file);
					char[] array = ("<" + rootElementTag + "></" + rootElementTag + ">").toCharArray();
					for (int i = 0; i < array.length; i++) {
						stream.write(array[i]);
					}
					stream.flush();
					stream.close();
				}

				FileInputStream is = new FileInputStream(file);
				ICFileHome fHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
				icFile = fHome.create();
				icFile.setFileValue(is);
				icFile.setName(fileNameWithoutFullPath);
				icFile.store();
				user.setUserProperties(icFile);
				user.store();
				return file;
			}

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		} catch (CreateException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public IWPropertyList getProperties(String propertyListName) {
		IWPropertyList list = getPropertyList(propertyListName);
		if ( list == null ) {
			list = this.getNewPropertyList(propertyListName);
		}
		return list;
	}
	
	/**
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		store();
	}
	
	@Override
	public void store() {
		try {
			String fileName = this.xmlFile.getName();
			String fileNameBeginning = fileName.substring(0, fileName.lastIndexOf("."));
			String fileNameEnding = fileName.substring(fileName.lastIndexOf(".") + 1);
			String tempFileName = fileNameBeginning + "-temp." + fileNameEnding;
			File tempXMLFile = new File(this.xmlFile.getParentFile(), tempFileName);
			store(new FileOutputStream(tempXMLFile));
			try {
				FileUtil.copyFile(tempXMLFile, this.xmlFile);
				FileUtil.delete(tempXMLFile);
				UserHome uHome = (UserHome) IDOLookup.getHome(User.class);
				User user = uHome.findByPrimaryKey(new Integer(userID));
				ICFile f = user.getUserProperties();
				if (f != null) {
					f.setFileValue(new FileInputStream(xmlFile));
					f.store();
				}
			}
			catch (IOException io) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error storing " + this.xmlFile.getAbsolutePath() + this.xmlFile.getName(), io);
			}
			catch (FinderException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error storing user properties file", e);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

}