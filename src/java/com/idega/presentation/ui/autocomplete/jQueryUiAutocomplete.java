package com.idega.presentation.ui.autocomplete;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.faces.context.FacesContext;

import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.Web2Business;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;
import com.idega.util.expression.ELUtil;

public class jQueryUiAutocomplete extends Autocomplete{

	@Override
	protected void initializeComponent(FacesContext context) {
		// TODO Auto-generated method stub
		IWContext iwc = IWContext.getIWContext(context);
		super.initializeComponent(context);
		addFiles(iwc);
	}

	@Override
	public List<String> getScripts(IWContext iwc) {
		List<String> scripts = new ArrayList<String>();

		try{
			Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.SPRING_BEAN_IDENTIFIER);
			JQuery  jQuery = web2.getJQuery();
			scripts.add(jQuery.getBundleURIToJQueryLib());
			scripts.add(jQuery.getBundleURIToJQueryUILib("1.8.17","js/jquery-ui-1.8.17.custom.min.js"));
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed getting Web2Business no jQuery and it's plugins files were added", e);
		}

		IWBundle iwb = CoreUtil.getCoreBundle();
		scripts.add(iwb.getVirtualPathWithFileNameString("javascript/ui/jQueryUiAutocompleteHelper.js"));

		return scripts;
	}

	@Override
	public List<String> getStyleSheets(IWContext iwc) {
		List<String> styles = new ArrayList<String>();

		try{
			Web2Business web2 = ELUtil.getInstance().getBean(Web2Business.SPRING_BEAN_IDENTIFIER);
			JQuery  jQuery = web2.getJQuery();
			styles.add(jQuery.getBundleURIToJQueryUILib("1.8.17","css/themes/smoothness/ui-1.8.17.custom.css"));
		}catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed getting Web2Business no css were added", e);
		}
		return styles;
	}

}
