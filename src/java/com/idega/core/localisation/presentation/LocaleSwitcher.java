package com.idega.core.localisation.presentation;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Span;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.FieldSet;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.util.PresentationUtil;

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

	@Override
	public void make(IWContext iwc) {
		if (this.showLinks) {
			doLinkView(iwc);
		}
		else {
			doDeveloperView(iwc);
		}
	}

	private void doDeveloperView(IWContext iwc) {
		IWMainApplication iwma = iwc.getIWMainApplication();

		IWBundle iwb = iwma.getBundle("com.idega.developer");
		PresentationUtil.addStyleSheetToHeader(iwc, iwb.getVirtualPathWithFileNameString("style/developer.css"));

		Layer topLayer = new Layer(Layer.DIV);
		topLayer.setStyleClass("developer");
		topLayer.setID("localeSwitcher");
		add(topLayer);

		FieldSet fieldSet = new FieldSet("Locale Switcher");
		topLayer.add(fieldSet);
		
		Form form = new Form();
		fieldSet.add(form);

		DropdownMenu localesDrop = LocalePresentationUtil.getAvailableLocalesDropdown(iwma, com.idega.core.localisation.business.LocaleSwitcher.languageParameterString);
		localesDrop.setToSubmit();
		if (!iwc.isParameterSet(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString)) {
			localesDrop.setSelectedElement(iwc.getCurrentLocale().toString());
		}
		else {
			localesDrop.setSelectedElement(iwc.getParameter(com.idega.core.localisation.business.LocaleSwitcher.languageParameterString));
		}

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		Label label = new Label("Select language", localesDrop);
		formItem.add(label);
		formItem.add(localesDrop);
		form.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label();
		label.setLabel("Current Locale");
		formItem.add(label);
		formItem.add(new Span(new Text(iwc.getCurrentLocale().getDisplayName() + " (" + iwc.getCurrentLocale().toString() + ")")));
		form.add(formItem);
	}

	protected Text getText(String text){
		return new Text(text);
	}
	
	@Override
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}