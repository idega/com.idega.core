package com.idega.presentation.ui;

import com.idega.presentation.IWContext;
import com.idega.presentation.Image;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Label extends InterfaceObject {
		
	private String _label;
	private Image _labelImage;
	
	public Label(InterfaceObject object) {
		this("", object);
	}
	
	public Label(String label, InterfaceObject object) {
		_label = label;
		setMarkupAttribute("for", object.getID());
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			print("<label "+getMarkupAttributesString()+" >");
			print(_label);
			println("</label>");	
		}
	}	
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}
	
}

