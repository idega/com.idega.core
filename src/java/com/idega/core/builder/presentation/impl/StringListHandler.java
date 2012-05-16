package com.idega.core.builder.presentation.impl;


import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

public class StringListHandler implements ICPropertyHandler {
	
	public List<?> getDefaultHandlerTypes() {
		return null;
	}

	public PresentationObject getHandlerObject(String name, String stringValue,
			IWContext iwc, boolean oldGenerationHandler, String instanceId,
			String method) {
		
		// 	FIXME: not finished
		return null;
	}

	public void onUpdate(String[] values, IWContext iwc) {
	}

}
