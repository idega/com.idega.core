/*
 * $Id: IWActionURI.java,v 1.2 2005/02/25 14:50:13 eiki Exp $
 * Created on Jan 31, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;


/**
 * 
 *  Last modified: $Date: 2005/02/25 14:50:13 $ by $Author: eiki $
 * A "parser" class for an action URI that divides an action uri into three parts: action, path and identifier
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class IWActionURI {
	
	private String actionPart;
	private String pathPart;
	private String identifierPart;
	private String originalURI;

	/**
	 * 
	 */
	public IWActionURI(String requestURI) {
		originalURI = requestURI;
		
		actionPart = extractActionPart(requestURI);
		
		pathPart = extractPathPart(requestURI);
		
		identifierPart = extractIdentifierPath(requestURI);
		
//		
//		String[] parts = requestURI.split("/");
//		for (int i = 0; i < parts.length; i++) {
//			String string = parts[i];
//			System.out.println("Part["+i+"] = "+string);
//			
//		}
	}
	
	
	/**
	 * @param requestURI
	 * @return the identifer part of the requesturi
	 */
	protected String extractIdentifierPath(String requestURI) {
		//get the identifier part
		return requestURI.substring(requestURI.lastIndexOf("/")+1);
	}


	/**
	 * @param requestURI
	 * @return the path part of the requesturi
	 */
	protected String extractPathPart(String requestURI) {
		//get the path part
		int index = requestURI.indexOf(getActionPart());
		if(index>=0){
			return requestURI.substring(index+actionPart.length());
		}
		
		return null;
	}


	/**
	 * @param requestURI
	 * @return the action part of the requesturi
	 */
	protected String extractActionPart(String requestURI) {
		//get the action part
		String[] parts = requestURI.split("/");
		return parts[3];
	}


	/**
	 * @return Returns the actionPart, e.g. "edit" from /idegaweb/action/edit/files/cms/article/1.xml
	 */
	public String getActionPart() {
		return actionPart;
	}
	
	/**
	 * @return Returns the pathPart, e.g. "/files/cms/article/" from /idegaweb/action/edit/files/cms/article/01012005.article/en.xml
	 */
	public String getPathPart() {
		return pathPart;
	}
	
	/**
	 * @return Returns the identifierPart, e.g. "01012005.article/en.xml" from /idegaweb/action/edit/files/cms/article/01012005.article/en.xml
	 */
	public String getIdentifierPart() {
		return identifierPart;
	}
	
	public static void main(String[] args){
		new IWActionURI("/idegaweb/action/edit/files/cms/article/01012005.article/en.xml");
	}
	/**
	 * @return Returns the originalURI.
	 */
	public String getOriginalURI() {
		return originalURI;
	}
	/**
	 * @param originalURI The originalURI to set.
	 */
	public void setOriginalURI(String originalURI) {
		this.originalURI = originalURI;
	}
	/**
	 * @param actionPart The actionPart to set.
	 */
	public void setActionPart(String actionPart) {
		this.actionPart = actionPart;
	}
	/**
	 * @param identifierPart The identifierPart to set.
	 */
	public void setIdentifierPart(String identifierPart) {
		this.identifierPart = identifierPart;
	}
	/**
	 * @param pathPart The pathPart to set.
	 */
	public void setPathPart(String pathPart) {
		this.pathPart = pathPart;
	}
	
	public String toString(){
		return getOriginalURI();
	}
}
