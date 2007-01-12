/*
 * $Id: IWActionURI.java,v 1.4.2.1 2007/01/12 19:32:25 idegaweb Exp $
 * Created on Jan 31, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import java.util.StringTokenizer;
import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:25 $ by $Author: idegaweb $
 * A "parser" class for an action URI that divides an action uri into three parts: action, path and identifier
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.4.2.1 $
 */
public class IWActionURI {
	
	private String actionPart;
	private String pathPart;
	private String identifierPart;
	private String handlerIdentifierPart;
	private String contextURI = "/";
	
	private final static String UNDEFINED_HANDLER_IDENTIFER = "default";
	
	private int INDEX_OF_ACTION = 0;
	private int INDEX_OF_HANDLER_IDENTIFIER = 1;

	public IWActionURI() {
		
	}
	
	/**
	 * 
	 */
	public IWActionURI(String requestURI) {		
		parseRequestURI(requestURI);
	}
	
	public void parseRequestURI(String requestURI){
		setActionPart(extractActionPart(requestURI));
		
		setHandlerIdentifier(extractHandlerIdentifierPart(requestURI));
		
		setPathPart(extractPathPart(requestURI));
		
		setIdentifierPart(extractIdentifierPath(requestURI));
		
		setContextURI(IWMainApplication.getDefaultIWMainApplication().getApplicationContextURI());
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
	 * @return the handler identifier part of the requesturi
	 */
	protected String extractHandlerIdentifierPart(String requestURI) {
		//get the path part
		StringTokenizer tokenizer = new StringTokenizer(extractActionURI(requestURI),"/");
		for(int i = 0; i < this.INDEX_OF_HANDLER_IDENTIFIER;i++){
			if(tokenizer.hasMoreTokens()){
				tokenizer.nextToken();
			}
		}
		
		if(tokenizer.hasMoreTokens()){
			return tokenizer.nextToken();
		}
		
		return null;
	}
	
	/**
	 * @param requestURI
	 * @return the path part of the requesturi
	 */
	protected String extractPathPart(String requestURI) {
		//get the path part
		String actionPath = extractActionURI(requestURI);
		String prefix = getActionPart()+"/"+getHandlerIdentifier();
		int index = actionPath.indexOf(prefix);
		if(index>=0){
			return actionPath.substring(index+prefix.length());
		}
		
		return null;
	}


	/**
	 * @param requestURI
	 * @return the action part of the requesturi
	 */
	protected String extractActionPart(String requestURI) {
		//get the action part
		String action = extractActionURI(requestURI);
		action = action.substring(0,action.indexOf("/"));
		return action;
	}
	
	/**
	 * @param requestURI
	 * @return the action path of the requesturi e.g. "edit/files/cms/article/1.xml" from "/idegaweb/action/edit/files/cms/article/1.xml"
	 */
	protected String extractActionURI(String requestURI) {
		//get the action path
		int index = requestURI.indexOf(IWActionURIManager.IDEGAWEB_ACTION_PATH_PREFIX);
		String actionPath = requestURI.substring(index+IWActionURIManager.IDEGAWEB_ACTION_PATH_PREFIX.length());
		return actionPath;
	}


	/**
	 * @return Returns the actionPart, e.g. "edit" from /idegaweb/action/edit/default/files/cms/article/1.xml
	 */
	public String getActionPart() {
		return this.actionPart;
	}
	
	/**
	 * @return Returns the pathPart, e.g. "/files/cms/article/01012005.article/en.xml" from /idegaweb/action/edit/default/files/cms/article/01012005.article/en.xml
	 */
	public String getPathPart() {
		return this.pathPart;
	}
	
	/**
	 * @return Returns the identifierPart, e.g. "01012005.article/en.xml" from /idegaweb/action/edit/default/files/cms/article/01012005.article/en.xml
	 */
	public String getIdentifierPart() {
		return this.identifierPart;
	}
	
	
	/**
	 * @return Returns the handlerIdentifier, e.g. "default" from /idegaweb/action/edit/default/files/cms/article/1.xml
	 */
	public String getHandlerIdentifier() {
		return (this.handlerIdentifierPart!=null)?this.handlerIdentifierPart:UNDEFINED_HANDLER_IDENTIFER;
	}
	
	public String getContextURI(){
		return this.contextURI;
	}
	
	public static void main(String[] args){
		IWActionURI uri = new IWActionURI("/idegaweb/action/edit/default/files/cms/article/01012005.article/en.xml");
		
		System.out.println(uri.getContextURI());
		System.out.println(IWActionURIManager.IDEGAWEB_ACTION_PATH_PREFIX);
		System.out.println(uri.getActionPart());
		System.out.println(uri.getHandlerIdentifier());
		System.out.println(uri.getPathPart());
			
		System.out.println(uri.toString());
		
		
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
	
	
	/**
	 * @param handlerIdentifier The handlerIdentifier to set.
	 */
	public void setHandlerIdentifier(String handlerIdentifier) {
		this.handlerIdentifierPart = handlerIdentifier;
	}
	
	
	public void setContextURI(String uri){
		this.contextURI = uri;
		if(this.contextURI != null && !this.contextURI.endsWith("/")){
			this.contextURI = this.contextURI+"/";
		} else if(this.contextURI==null){
			this.contextURI = "/";
		}
	}
	
	public String buildActionURI(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getContextURI());
		buffer.append(IWActionURIManager.IDEGAWEB_ACTION_PATH_PREFIX);
		buffer.append(getActionPart());
		buffer.append("/");
		buffer.append(getHandlerIdentifier());
		buffer.append(getPathPart());
		return buffer.toString();
	}
	
	public String toString(){
		return buildActionURI();
	}
}
