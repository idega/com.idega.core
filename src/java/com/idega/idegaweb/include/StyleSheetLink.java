/*
 * $Id: StyleSheetLink.java,v 1.1 2005/03/06 13:17:37 tryggvil Exp $
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
 *  Last modified: $Date: 2005/03/06 13:17:37 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class StyleSheetLink extends ExternalLink {

	/**
	 * By default the media is set to screen
	 */
	public StyleSheetLink(){
		setType(TYPE_CSS);
		setMedia(MEDIA_SCREEN);
		setRelationship(RELATIONSHIP_STYLESHEET);
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
		setMedia(MEDIA_SCREEN);
	}
	
	public void addAsScreen(){
		addMedia(MEDIA_SCREEN);
	}
	
	public void setAsTty(){
		setMedia(MEDIA_TTY);
	}
	
	public void addAsTty(){
		addMedia(MEDIA_TTY);
	}
	
	public void setAsTv(){
		setMedia(MEDIA_TV);
	}
	
	public void addAsTv(){
		addMedia(MEDIA_TV);
	}
	
	public void setAsProjection(){
		setMedia(MEDIA_PROJECTION);
	}
	
	public void addAsProjection(){
		addMedia(MEDIA_PROJECTION);
	}

	public void setAsHandheld(){
		setMedia(MEDIA_HANDHELD);
	}
	
	public void addAsHandheld(){
		addMedia(MEDIA_HANDHELD);
	}
	
	public void setAsPrint(){
		setMedia(MEDIA_PRINT);
	}
	
	public void addAsPrint(){
		addMedia(MEDIA_PRINT);
	}
	
	public void setAsBraille(){
		setMedia(MEDIA_BRAILLE);
	}
	
	public void addAsBraille(){
		addMedia(MEDIA_BRAILLE);
	}
	
	public void setAsAural(){
		setMedia(MEDIA_AURAL);
	}
	
	public void addAsAural(){
		addMedia(MEDIA_AURAL);
	}
	
	public void setAsALL(){
		setMedia(MEDIA_ALL);
	}
	
}
