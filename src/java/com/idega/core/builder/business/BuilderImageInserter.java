package com.idega.core.builder.business;

import com.idega.presentation.PresentationObjectType;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 10, 2004
 */
public interface BuilderImageInserter extends PresentationObjectType {
	
	void setHasUseBox(boolean hasUseBox);

	void limitImageWidth(boolean limitWidth);

	void setHeight(String imageHeight);

	void setWidth(String imageWidth);
	
	void setImageId(int imageId);
	
	void setImSessionImageName(String imSessionImageName);
	
	void setWindowToReload(boolean reload);
	
	void setNullImageIDDefault();
	
	
}
