package com.idega.util;

import javax.faces.context.FacesContext;

import com.idega.presentation.IWContext;

public class CoreUtil {
	
	public static IWContext getIWContext() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			if (context == null) {
				return null;
			}
			return IWContext.getIWContext(context);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
