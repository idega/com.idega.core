package com.idega.core.localisation.presentation;

import java.util.Enumeration;

import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSwitcher extends com.idega.idegaweb.presentation.LocaleChanger {

	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.core";

	public void make(IWContext iwc) {
		if (this.showLinks) {
			doLinkView(iwc);
		}
		else {
			doDeveloperView(iwc);
		}

	}

	private void doDeveloperView(IWContext iwc) {
		add(getTitle());
		if (!iwc.isIE()) {
			getParentPage().setBackgroundColor("#FFFFFF");
		}
		IWMainApplication iwma = iwc.getIWMainApplication();

		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, com.idega.core.localisation.business.LocaleSwitcher.languageParameterString);
		//localesDrop.keepStatusOnAction();
		localesDrop.setToSubmit();
		if (!iwc.isParameterSet(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString)) {
			localesDrop.setSelectedElement(iwc.getCurrentLocale().toString());
		}
		else {
			localesDrop.setSelectedElement(iwc.getParameter(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString));
		}

		Form form = new Form();
		addMaintainedFormParameters(form);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		form.add(getText("Select language:&nbsp;&nbsp;"));
		form.add(localesDrop);

		Enumeration enumer = iwc.getParameterNames();
		while (enumer.hasMoreElements()) {
			form.maintainParameter((String) enumer.nextElement());
		}

		//doBusiness(iwc);

		add(getText("Current Locale:&nbsp;&nbsp;"));
		add(iwc.getCurrentLocale().getDisplayName() + " (" + iwc.getCurrentLocale().toString() + ")");
	}

	/*private void doBusiness(IWContext iwc) {
		String localeValue = iwc.getParameter(localesParameter);
		if (localeValue != null) {
			Locale locale = LocaleUtil.getLocale(localeValue);
			if (locale != null) {
				iwc.setCurrentLocale(locale);
			}
		}
	}*/


	protected Text getText(String text){
		return new Text(text);
	}
	
	protected Text getTitle(){
		//return IWDeveloper.getTitleTable(this.getClass());
		return new Text("LocaleSwitcher");
	}

	protected void addMaintainedFormParameters(Form form){
		//form.maintainParameter(IWDeveloper.actionParameter);
		//form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

}
