/*
 * Created on 28.10.2003 by  tryggvil in project com.project
 */
package com.idega.versioncontrol.business;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWBundle;

/**
 * UpdateServiceBean //TODO: tryggvil Describe class
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class UpdateServiceBean extends IBOServiceBean implements UpdateService
{

	public boolean updateBundleToMostRecentVersion(String bundleIdentifier){
		IWBundle bundle = this.getIWApplicationContext().getApplication().getBundle(bundleIdentifier);
		return this.updateBundleToMostRecentVersion(bundle);
	}

	
	public boolean updateBundleToMostRecentVersion(IWBundle bundle){
		String realBundleDir = bundle.getRealPath();
		bundle.storeState();
		if(executeCVSUpdate(realBundleDir)){
			bundle.reloadBundle();
			return true;
		}
		return false;
	}
	
	private boolean executeCVSUpdate(String directory){
		try
		{
			String command = "update -P -A -d";
			int exit = executeCVSCommand(command,directory);
			if(exit==0){
				return true;
			}
			else{
				return false;
			}
		}
		catch (IOException e)
		{
			log("Error executing update to directory: "+directory);
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean executeCVSCommit(String directory,String file,String comment){
		try
		{
			String command = "commit "+file+" -m "+comment;
			int exit = executeCVSCommand(command,directory);
			if(exit==0){
				return true;
			}
			else{
				return false;
			}
		}
		catch (IOException e)
		{
			log("Error executing commit to file:"+file+" in directory: "+directory);
			log(e);
			return false;
		}
	}
	
	private int executeCVSCommand(String command,String directory) throws IOException{
		//if(envp==null || envp.length==0){
			String[] envp2 = {"CVS_RSH=ssh"};
		//}
		int returnValue=-1;
		String cmd = "cvs "+command;
		File dir = new File(directory);
		debug("Executing command:"+cmd+" in dir="+directory);
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd,envp2,dir);
		InputStream input = process.getInputStream();
		InputStream err = process.getErrorStream();
		try
		{
			returnValue = process.waitFor();
		}
		catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			writeInputStreamToPrintStream(input,System.out);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			writeInputStreamToPrintStream(err,System.err);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		returnValue = process.exitValue();
		return returnValue;
	}

	/**
	 * @param stream
	 */
	private void writeInputStreamToPrintStream(InputStream input,PrintStream stream) throws IOException
	{
		int buflen = 10;
		byte[] buf = new byte[buflen];
		while(input.read(buf)!=-1){
			stream.write(buf);
		}
	}
	
	
}
