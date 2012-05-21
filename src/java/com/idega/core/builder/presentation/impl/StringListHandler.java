package com.idega.core.builder.presentation.impl;


import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.TextInput;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;

public class StringListHandler implements ICPropertyHandler {
	
	public List<?> getDefaultHandlerTypes() {
		return null;
	}

	public PresentationObject getHandlerObject(String name, String stringValue,
			IWContext iwc, boolean oldGenerationHandler, String instanceId,
			String method) {
		Layer main = new Layer();
		
		TextInput mainInput = new TextInput(name,stringValue);
		main.add(mainInput);
		
		IWBundle iwb = CoreUtil.getCoreBundle();
		String source = PresentationUtil.getJavaScriptSourceLine(iwb.getVirtualPathWithFileNameString("javascript/presentation/string-list-editor.js"));
		main.add(source);
		
		String action = "LazyLoader.load('"+iwb.getVirtualPathWithFileNameString("javascript/presentation/string-list-editor.js")+"', function() {jQuery('#"+mainInput.getId() +"').stringListEditor();}, null);";
		if (!CoreUtil.isSingleComponentRenderingProcess(iwc))
			action = "jQuery(document).ready(function(){" + action + "});";
		main.add(PresentationUtil.getJavaScriptAction(action));
		
		return main;
//		return new TextInput(name,stringValue);
	}

	public void onUpdate(String[] values, IWContext iwc) {
		
	}

}
