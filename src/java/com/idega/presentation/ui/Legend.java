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
public class Legend extends InterfaceObject {
		
	private String _legend;
	private Image _legendImage;
	
	public Legend() {
		this("");
	}
	
	public Legend(String legend) {
		_legend = legend;
	}
	
	public void print(IWContext iwc) throws Exception {
		if (getLanguage().equals("HTML")) {
			print("<legend "+getAttributeString()+" >");
			if (_legendImage != null)
				_legendImage._print(iwc);
			else
				print(_legend);
			println("</legend>");	
		}
	}	
	
	/**
	 * @see com.idega.presentation.ui.InterfaceObject#handleKeepStatus(IWContext)
	 */
	public void handleKeepStatus(IWContext iwc) {
	}
	
	public void setAsLabelForInterface(InterfaceObject object) {
		setAttribute("for", object.getID());
	}
	
	public void setLegend(Image image) {
		_legendImage = image;	
	}

}

