package com.idega.core.localisation.presentation;

import java.util.Enumeration;
//import java.util.Locale;

import com.idega.development.presentation.*;
import com.idega.development.presentation.IWDeveloper;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
//import com.idega.util.LocaleUtil;

/**
 * Title:        idega Framework
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href=mailto:"tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class LocaleSwitcher extends com.idega.idegaweb.presentation.LocaleChanger {

	public final static String IW_BUNDLE_IDENTIFIER = "com.idega.developer";

	public void make(IWContext iwc) {
		if (showLinks)
			doLinkView(iwc);
		else
			doDeveloperView(iwc);

	}

	private void doDeveloperView(IWContext iwc) {
		add(IWDeveloper.getTitleTable(this.getClass()));
		if (!iwc.isIE())
			getParentPage().setBackgroundColor("#FFFFFF");
		IWMainApplication iwma = iwc.getApplication();

		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, com.idega.core.localisation.business.LocaleSwitcher.languageParameterString);
		//localesDrop.keepStatusOnAction();
		localesDrop.setToSubmit();
		if (!iwc.isParameterSet(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString))
			localesDrop.setSelectedElement(iwc.getCurrentLocale().toString());
		else
			localesDrop.setSelectedElement(iwc.getParameter(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString));

		Form form = new Form();
		form.maintainParameter(IWDeveloper.actionParameter);
		form.maintainParameter(IWDeveloper.PARAMETER_CLASS_NAME);
		//form.setTarget(IWDeveloper.frameName);
		add(form);
		form.add(IWDeveloper.getText("Select language:&nbsp;&nbsp;"));
		form.add(localesDrop);

		Enumeration enum = iwc.getParameterNames();
		while (enum.hasMoreElements()) {
			form.maintainParameter((String) enum.nextElement());
		}

		//doBusiness(iwc);

		add(IWDeveloper.getText("Current Locale:&nbsp;&nbsp;"));
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

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

}
