package com.idega.presentation.plaf;

import com.idega.util.IWColor;
import com.idega.presentation.PresentationObject;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author
 * @version 1.0
 */

public interface TabPagePresentation {

	public void add(PresentationObject obj);
//	public void empty();
	public void setWidth(String width);
	public void setHeight(String height);
	public void fireContentChange();
        public void setColor(IWColor color);


}