package com.idega.presentation.remote;

import java.net.URL;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.core.localisation.business.LocaleSwitcher;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.idega.util.expression.ELUtil;

public class RemotePageViewer extends Block {

	@Autowired
	private JQuery jQuery;
	
	private String url, regionsToShow, personalId;
	
	private boolean loadAutoLogin = Boolean.TRUE;
	
	@Override
	public void main(IWContext iwc) throws Exception {
		ELUtil.getInstance().autowire(this);
		
		Layer container = new Layer();
		add(container);
		
		IWBundle bundle = getBundle(iwc);
		String js = bundle.getVirtualPathWithFileNameString("javascript/RemotePageViewer.js");
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
				jQuery.getBundleURIToJQueryLib(),
				CoreConstants.DWR_ENGINE_SCRIPT,
				CoreConstants.DWR_UTIL_SCRIPT,
				"/dwr/interface/WebUtil.js",
				js
		));
		
		String personalId = getPersonalId();
		String server = CoreConstants.EMPTY;
		if (loadAutoLogin) {
			if (!StringUtil.isEmpty(url)) {
				URL link = new URL(url);
				server = link.getProtocol() + "://" + link.getHost();
			}
			
			if (StringUtil.isEmpty(personalId)) {
				if (iwc.isLoggedOn())
					personalId = iwc.getCurrentUser().getPersonalID();
				if (StringUtil.isEmpty(personalId))
					personalId = CoreConstants.EMPTY;
			}
		}
		
		url = StringUtil.isEmpty(url) ? CoreConstants.PAGES_URI_PREFIX : url;
		if (!url.endsWith(CoreConstants.SLASH))
			url = url.concat(CoreConstants.SLASH);
		URIUtil uri = new URIUtil(url);
		uri.setParameter(LocaleSwitcher.languageParameterString, iwc.getCurrentLocale().toString());
		url = uri.getUri();
		
		js = "/idegaweb/bundles/com.idega.core.bundle/resources/javascript/RemotePageViewer.js";
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
		String regions = StringUtil.isEmpty(regionsToShow) ? "null" : "\'" + regionsToShow + "\'";
		String remoteAction = "registerEvent(window,\'load\',function(){RemotePageViewer.displayRegions(" + regions + ");});";
		String action = "RemotePageViewer.loadPage('" + iwrb.getLocalizedString("loading", "Loading...") + "', '" + url + "', '" + server + "', '" + personalId + "', '" +
				container.getId() + "', " +	loadAutoLogin + ", '" + js +"', \""+ remoteAction +"\");";
		if (!CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			action = "jQuery(window).load(function() {" + action + "});";
		}
		PresentationUtil.addJavaScriptActionToBody(iwc, action);
	}

	public boolean isLoadAutoLogin() {
		return loadAutoLogin;
	}

	public void setLoadAutoLogin(boolean loadAutoLogin) {
		this.loadAutoLogin = loadAutoLogin;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getRegionsToShow() {
		return regionsToShow;
	}

	public void setRegionsToShow(String regionsToShow) {
		this.regionsToShow = regionsToShow;
	}

	public String getPersonalId() {
		return personalId;
	}

	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}
	
}