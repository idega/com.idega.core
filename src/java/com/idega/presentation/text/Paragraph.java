/*
 * $Id: Paragraph.java,v 1.9 2005/03/08 19:36:45 tryggvil Exp $
 * Created in 2000 by Tryggvi Larusson
 *
 * Copyright (C) 2000-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.presentation.text;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;

/**
 * <p>
 * This component renders out a Paragraph or &lt;P&gt; element around its children.
 * </p>
 *  Last modified: $Date: 2005/03/08 19:36:45 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.9 $
 */
public class Paragraph extends PresentationObjectContainer {

	public static final String HORIZONTAL_ALIGN_LEFT = "left";
	public static final String HORIZONTAL_ALIGN_RIGHT = "right";
	public static final String HORIZONTAL_ALIGN_CENTER = "center";
	public static final String HORIZONTAL_ALIGN_JUSTIFY = "justify";

	public Paragraph() {
		super();
		setTransient(false);
	}

	public Paragraph(String align) {
		super();
		setAlign(align);
		setTransient(false);
	}

	public Paragraph(String align, String ID) {
		super();
		setAlign(align);
		setID(ID);
		setTransient(false);
	}

	public Paragraph(String align, String ID, String Class) {
		super();
		setAlign(align);
		setID(ID);
		setClass(Class);
		setTransient(false);
	}

	public Paragraph(String align, String ID, String Class, String style) {
		super();
		setAlign(align);
		setID(ID);
		setClass(Class);
		setStyle(style);
		setTransient(false);
	}

	public void setAlign(String s) {
		setMarkupAttribute("align", s);
	}

	public void setClass(String s) {
		setMarkupAttribute("class", s);
	}

	public void setStyle(String s) {
		setMarkupAttribute("style", s);
	}

	public void print(IWContext iwc) throws Exception {
		// if ( doPrint(iwc) ){
		if (getMarkupLanguage().equals("HTML")) {
			// if (getInterfaceStyle().equals("something")){
			// }
			// else{
			println("<p " + getMarkupAttributesString() + " >");
			super.print(iwc);
			println("</p>");
			// }
		}
		else if (getMarkupLanguage().equals("WML")) {
			if (this.isEmpty()) {
				print("<p/>");
			}
			else {
				print("<p>");
				super.print(iwc);
				print("</p>");
			}
		}
	}
	// }
}
