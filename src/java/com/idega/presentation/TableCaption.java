/*
 * $Id: TableCaption.java,v 1.3 2005/09/19 15:00:22 laddi Exp $
 * Created on Aug 5, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;
import com.idega.presentation.text.Text;


/**
 * Last modified: $Date: 2005/09/19 15:00:22 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.3 $
 */
public class TableCaption extends PresentationObject {

	/**
	 * Sets the caption text.
	 * 
	 * @param caption	The caption text to set.
	 */
	public void setCaption(String caption) {
		Text text = new Text(caption);
		getChildren().add(text);
	}
	
	/**
	 * Sets the caption text.
	 * 
	 * @param caption	The caption text to set.
	 */
	public void setCaption(Text caption) {
		getChildren().add(caption);
	}

	public void print(IWContext iwc) throws Exception {
		if (getMarkupLanguage().equals("HTML")) {
			print("<caption" + getMarkupAttributesString() + ">");

			List theObjects = getChildren();
			if (theObjects != null) {
				Iterator iter = theObjects.iterator();
				while (iter.hasNext()) {
					PresentationObject item = (PresentationObject) iter.next();
					renderChild(iwc,item);
				}
			}
			
			println("</caption>");
		}
	}

	public void encodeBegin(FacesContext context) throws IOException {
		print("<caption" + getMarkupAttributesString() + ">");
	}

	public void encodeEnd(FacesContext arg0) throws IOException {
		println("</caption>");
	}
}