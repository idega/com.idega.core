/*
 * $Id: GeneratedImageCache.java,v 1.1 2005/12/07 23:41:47 tryggvil Exp $
 * Created on 7.12.2005 in project com.idega.core
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.graphics.generator;

import java.util.Locale;
import com.idega.presentation.Image;


/**
 * <p>
 * TODO tryggvil Describe Type GeneratedImageCache
 * </p>
 *  Last modified: $Date: 2005/12/07 23:41:47 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class GeneratedImageCache {

	private String text;
	private int width;
	private int height;
	private String upImageUrl;
	private String downImageUrl;
	private String overImageUrl;
	private Locale locale;
	
	/**
	 * 
	 */
	public GeneratedImageCache(String text,int width,int height,String upImageUrl,String downImageUrl,String overImageUrl,Locale locale) {
		setText(text);
		setWidth(width);
		setHeight(height);
		setUpImageUrl(upImageUrl);
		setDownImageUrl(downImageUrl);
		setOverImageUrl(overImageUrl);
		setLocale(locale);
	}

	public Image createImage(){
		//String upName = fileVirtualPath + button.getUpName();
		//String downName = fileVirtualPath + button.getDownName();
		//String overName = fileVirtualPath + button.getOverName();
		String upName = getUpImageUrl();
		String overName = getOverImageUrl();
		String downName = getDownImageUrl();
		Image image;
		image = new Image(getText(), upName, overName, downName);
		image.setWidth(getWidth());
		image.setHeight(getHeight());
		return image;
	}
	
	
	/**
	 * @return Returns the downImageUrl.
	 */
	public String getDownImageUrl() {
		return downImageUrl;
	}

	
	/**
	 * @param downImageUrl The downImageUrl to set.
	 */
	public void setDownImageUrl(String downImageUrl) {
		this.downImageUrl = downImageUrl;
	}

	
	/**
	 * @return Returns the height.
	 */
	public int getHeight() {
		return height;
	}

	
	/**
	 * @param height The height to set.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	
	/**
	 * @return Returns the locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	
	/**
	 * @param locale The locale to set.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	
	/**
	 * @return Returns the overImageUrl.
	 */
	public String getOverImageUrl() {
		return overImageUrl;
	}

	
	/**
	 * @param overImageUrl The overImageUrl to set.
	 */
	public void setOverImageUrl(String overImageUrl) {
		this.overImageUrl = overImageUrl;
	}

	
	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}

	
	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	
	/**
	 * @return Returns the upImageUrl.
	 */
	public String getUpImageUrl() {
		return upImageUrl;
	}

	
	/**
	 * @param upImageUrl The upImageUrl to set.
	 */
	public void setUpImageUrl(String upImageUrl) {
		this.upImageUrl = upImageUrl;
	}

	
	/**
	 * @return Returns the width.
	 */
	public int getWidth() {
		return width;
	}

	
	/**
	 * @param width2 The width to set.
	 */
	public void setWidth(int width2) {
		this.width = width;
	}
}
