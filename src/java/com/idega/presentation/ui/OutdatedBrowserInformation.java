package com.idega.presentation.ui;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;

public class OutdatedBrowserInformation extends Block {

	private IWBundle bundle;
	private IWResourceBundle iwrb;
	
	private boolean closeable;
	
	public OutdatedBrowserInformation() {
		super();
	}
	
	public OutdatedBrowserInformation(boolean closeable) {
		this();
		
		this.closeable = closeable;
	}
	
	@Override
	public void main(IWContext iwc) throws Exception {
		bundle = getBundle(iwc);
		iwrb = bundle.getResourceBundle(iwc);
		
		Layer container = new Layer();
		container.setStyleClass("outdatedBrowserInformation");
		add(container);
		
		if (isCloseable()) {
			container.add(getPart("outdatedBrowserInformationCloser", bundle.getVirtualPathWithFileNameString("icons/browsers/ie6nomore-cornerx.jpg"),
					"outdatedBrowserInformationCloserImage", iwrb.getLocalizedString("close_outdated_browser_info", "Close this notice"),
					"javascript:this.parentNode.parentNode.style.display='none'; return false;", null, null));
		}
		
		Layer content = new Layer();
		content.setStyleClass("outdatedBrowserInformationContent");
		container.add(content);
		
		content.add(getPart("outdatedBrowserInformationContentWarningContainer", bundle.getVirtualPathWithFileNameString("icons/browsers/ie6nomore-warning.jpg"),
				"outdatedBrowserInformationContentWarningImage", iwrb.getLocalizedString("warning_outdated_browser", "Warning!"), null, null, null));
		
		Layer text = new Layer();
		text.setStyleClass("outdatedBrowserInformationContentText");
		content.add(text);
		text.add(getTextPart("outdatedBrowserInformationContentTextHeading",
				iwrb.getLocalizedString("outdated_browser_text_heading", "You are using an outdated browser")));
		text.add(getTextPart("outdatedBrowserInformationContentTextBody",
				iwrb.getLocalizedString("outdated_browser_text_body", "For a better experience using this site, please upgrade to a modern web browser.")));
		
		content.add(getPart("outdatedBrowserInformationFirefox", bundle.getVirtualPathWithFileNameString("icons/browsers/firefox.png"),
				"outdatedBrowserInformationFirefoxImage", iwrb.getLocalizedString("outdated_browser_get_firefox", "Get Firefox"), null, "http://www.firefox.com",
				"_blank"));
		content.add(getPart("outdatedBrowserInformationIE", bundle.getVirtualPathWithFileNameString("icons/browsers/ie.png"),
				"outdatedBrowserInformationIEImage", iwrb.getLocalizedString("outdated_browser_get_ie", "Get Internet Explorer"), null,
				"http://www.browserforthebetter.com/download.html", "_blank"));
		content.add(getPart("outdatedBrowserInformationSafari", bundle.getVirtualPathWithFileNameString("icons/browsers/safari.png"),
				"outdatedBrowserInformationSafariImage", iwrb.getLocalizedString("outdated_browser_get_safari", "Get Safari"), null,
				"http://www.apple.com/safari/download/", "_blank"));
		content.add(getPart("outdatedBrowserInformationChrome", bundle.getVirtualPathWithFileNameString("icons/browsers/chrome.png"),
				"outdatedBrowserInformationChromeImage", iwrb.getLocalizedString("outdated_browser_get_chrome", "Get Chrome"), null,
				"http://www.google.com/chrome", "_blank"));
	}
	
	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
	}

	private Layer getTextPart(String styleClass, String text) {
		Layer container = new Layer();
		container.setStyleClass(styleClass);
		container.add(text);
		return container;
	}
	
	private Layer getPart(String styleClass, String imageUri, String imageStyleClass, String text, String onLinkClick, String url, String target) {
		Layer container = new Layer();
		container.setStyleClass(styleClass);
		
		Image image = new Image(imageUri);
		image.setStyleClass(imageStyleClass);
		image.setTitle(text);
		
		if (StringUtil.isEmpty(onLinkClick) && StringUtil.isEmpty(url)) {
			container.add(image);
		} else {
			Link link = new Link(image, StringUtil.isEmpty(url) ? "javascript:void(0);" : url);
			if (!StringUtil.isEmpty(onLinkClick)) {
				link.setOnClick(onLinkClick);
			}
			if (!StringUtil.isEmpty(target)) {
				link.setTarget(target);
			}
			container.add(link);
		}
		
		return container;
	}
	
	@Override
	public String getBundleIdentifier() {
		return CoreConstants.CORE_IW_BUNDLE_IDENTIFIER;
	}
}
