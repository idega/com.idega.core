package com.idega.presentation.ui;

import java.util.Iterator;
import java.util.Vector;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author laddi
 */
public class FieldSet extends InterfaceObject {
	
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
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}

}
