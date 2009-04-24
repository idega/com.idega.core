/*
 * $Id: StyleSheetLink.java,v 1.2 2009/04/24 08:39:08 valdas Exp $
 * Created on 12.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.idegaweb.include;


/**
 *  <p>
 *  	Class to serve as a representation of a link to an external stylesheet file.
 *  </p>
 *  Last modified: $Date: 2009/04/24 08:39:08 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class StyleSheetLink extends ExternalLink {

	private static final long serialVersionUID = 5126013562070769025L;
	
	private String media;
	
	/**
	 * By default the media is set to screen
	 */
	public StyleSheetLink(){
		setType(PageResourceConstants.TYPE_CSS);
		setMedia(PageResourceConstants.MEDIA_SCREEN);
		setRelationship(PageResourceConstants.RELATIONSHIP_STYLESHEET);
	}
	/**
	 * By default the media is set to screen
	 */
	public StyleSheetLink(String url){
		this();
		setUrl(url);
	}
	
	public StyleSheetLink(String url,String media){
		this(url);
		setMedia(media);
	}
	
	/**
	 * Adds the stylesheet to be of specified media if another is set. 
	 * This method adds the media by adding a comma between the previously set media and the new one
	 * @param media
	 */
	private void addMedia(String media){
		String setMedia = getMedia();
		if(setMedia!=null){
			media = setMedia+", "+media;
		}
		setMedia(media);
	}
	
	public void setAsScreen(){
		setMedia(PageResourceConstants.MEDIA_SCREEN);
	}
	
	public void addAsScreen(){
		addMedia(PageResourceConstants.MEDIA_SCREEN);
	}
	
	public void setAsTty(){
		setMedia(PageResourceConstants.MEDIA_TTY);
	}
	
	public void addAsTty(){
		addMedia(PageResourceConstants.MEDIA_TTY);
	}
	
	public void setAsTv(){
		setMedia(PageResourceConstants.MEDIA_TV);
	}
	
	public void addAsTv(){
		addMedia(PageResourceConstants.MEDIA_TV);
	}
	
	public void setAsProjection(){
		setMedia(PageResourceConstants.MEDIA_PROJECTION);
	}
	
	public void addAsProjection(){
		addMedia(PageResourceConstants.MEDIA_PROJECTION);
	}

	public void setAsHandheld(){
		setMedia(PageResourceConstants.MEDIA_HANDHELD);
	}
	
	public void addAsHandheld(){
		addMedia(PageResourceConstants.MEDIA_HANDHELD);
	}
	
	public void setAsPrint(){
		setMedia(PageResourceConstants.MEDIA_PRINT);
	}
	
	public void addAsPrint(){
		addMedia(PageResourceConstants.MEDIA_PRINT);
	}
	
	public void setAsBraille(){
		setMedia(PageResourceConstants.MEDIA_BRAILLE);
	}
	
	public void addAsBraille(){
		addMedia(PageResourceConstants.MEDIA_BRAILLE);
	}
	
	public void setAsAural(){
		setMedia(PageResourceConstants.MEDIA_AURAL);
	}
	
	public void addAsAural(){
		addMedia(PageResourceConstants.MEDIA_AURAL);
	}
	
	public void setAsALL(){
		setMedia(PageResourceConstants.MEDIA_ALL);
	}
	
	/**
	 * @return Returns the media.
	 */
	public String getMedia() {
		return this.media;
	}
	/**
	 * @param media The media to set.
	 */
	public void setMedia(String media) {
		this.media = media;
	}
}
