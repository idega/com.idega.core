package com.idega.presentation.ui;

import java.util.Iterator;
import java.util.Vector;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FieldSet extends InterfaceObjectContainer {
	
	private Vector theElements;
	private String _legend;
	
	public FieldSet() {
		theElements = new Vector();
	}
	
	public FieldSet(String legend) {
		this();
		_legend = legend;	
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			println("<fieldset>");
			if (_legend != null)
				theElements.add(0, new Legend(_legend));
			
			theElements.trimToSize();
			Iterator iter = theElements.iterator();
			while (iter.hasNext()) {
				PresentationObject object = (PresentationObject) iter.next();
				object._print(iwc);
			}
			
			println("</fieldset>");	
		}
	}
	
	public void add(PresentationObject object) {
		theElements.add(object);
	}
	
}
