package com.idega.idegaweb.include;

import java.util.ArrayList;
import java.util.List;

import com.idega.util.StringUtil;

/**
 * Holds JavaScript tag information
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2009/04/24 08:39:08 $ by: $Author: valdas $
 */
public class JavaScriptLink extends ExternalLink {

	private static final long serialVersionUID = -8414777303690605947L;
	
	private List<String> actions;
	
	public JavaScriptLink() {
		actions = new ArrayList<String>();
		
		setType(PageResourceConstants.TYPE_JAVA_SCRIPT);
	}
	
	public JavaScriptLink(String url) {
		this();
		setUrl(url);
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
	public void addAction(String action) {
		if (StringUtil.isEmpty(action) || actions.contains(action)) {
			return;
		}
		actions.add(action);
	}
}
