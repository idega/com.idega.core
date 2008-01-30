package com.idega.presentation;

import com.idega.presentation.text.Heading1;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;

public class DefaultErrorHandlingUriWindow extends Block {
	
	public void main(IWContext iwc) {
		Layer container = new Layer();
		add(container);
		
		container.add(new Heading1(getResourceBundle(iwc).getLocalizedString("error_handling_uri", "Sorry, ePlatform couldn't handle your request...")));
		
		container.add(PresentationUtil.getJavaScriptAction("try {MOOdalBox.addEventToCloseAction(function() {reloadPage();});} catch(e) {}"));
	}
	
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}

}
