package com.idega.notifier.presentation;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.Web2Business;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * Growl style notifier. Based on Gritter: http://boedesign.com/2009/07/11/growl-for-jquery-gritter/
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.0 $
 *
 * Last modified: $Date: 2009.07.16 16:12:48 $ by: $Author: valdas $
 */
public abstract class BasicNotification extends Block {

	private static final Logger LOGGER = Logger.getLogger(BasicNotification.class.getName());
	
	private String notificationVariable;
	
	private String title;
	private String text;
	private String image;
	
	private boolean sticky;
	
	private int time;
	
	private List<String> javaScriptActionsAfterNotificationAdded;
	
	@Autowired
	private JQuery jQuery;
	
	@Autowired
	private Web2Business web2;
	
	@Override
	public void main(IWContext iwc) {
		present(iwc);
		
		if (StringUtil.isEmpty(getTitle()) || StringUtil.isEmpty(getText())) {
			LOGGER.warning("Title and text must be provided for notification: " + getClass().getName());
			return;
		}
		
		ELUtil.getInstance().autowire(this);
		
		StringBuilder action = new StringBuilder();
		
		//	Variable
		if (!StringUtil.isEmpty(getNotificationVariable())) {
			action.append(getNotificationVariable()).append(" = ");
		}
		
		//	Title and text
		action.append("jQuery.gritter.add({title: '").append(getTitle()).append("', text: '").append(getText()).append("'");
		
		//	Image
		if (!StringUtil.isEmpty(getImage())) {
			action.append(", image: '").append(getImage()).append("'");
		}
		
		//	Sticky
		action.append(", sticky: ").append(isSticky());
		
		//	Time
		if (getTime() > 0) {
			action.append(", time: ").append(getTime());
		}
		
		//	Callback(s)
		if (!ListUtil.isEmpty(getJavaScriptActionsAfterNotificationAdded())) {
			StringBuilder callback = new StringBuilder("function(id) {");
			for (String callbackAction: getJavaScriptActionsAfterNotificationAdded()) {
				callback.append(callbackAction);
			}
			callback.append("}");
			action.append(", callback: ").append(callback.toString());
		}
		
		action.append("});");
		
		if (!CoreUtil.isSingleComponentRenderingProcess(iwc)) {
			action = new StringBuilder("jQuery(window).load(function() {").append(action.toString()).append("});");
		}
		
		PresentationUtil.addStyleSheetToHeader(iwc, web2.getBundleUriToGritterStyleSheet());
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
				iwc.getIWMainApplication().getBundle(CoreConstants.CORE_IW_BUNDLE_IDENTIFIER).getVirtualPathWithFileNameString("iw_core.js"),
				CoreConstants.DWR_ENGINE_SCRIPT,
				jQuery.getBundleURIToJQueryLib(),
				web2.getBundleUriToGritterScriptFile()
		));
		PresentationUtil.addJavaScriptActionOnLoad(iwc, action.toString());
	}
	
	public abstract void present(IWContext iwc);
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	/**
	 * Mandatory: the heading of the notification
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	/**
	 * Mandatory: the text inside the notification
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	public String getImage() {
		return image;
	}

	/**
	 * URL or URI to the image
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	public boolean isSticky() {
		return sticky;
	}

	/**
	 * If you want it to fade out on its own or just sit there
	 * @param sticky
	 */
	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}

	public int getTime() {
		return time;
	}

	/**
	 * The time you want it to be alive for before fading out (milliseconds)
	 * @param time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	public String getNotificationVariable() {
		return notificationVariable;
	}

	public void setNotificationVariable(String notificationVariable) {
		this.notificationVariable = notificationVariable;
	}

	public List<String> getJavaScriptActionsAfterNotificationAdded() {
		return javaScriptActionsAfterNotificationAdded;
	}

	public void setJavaScriptActionsAfterNotificationAdded(
			List<String> javaScriptActionsAfterNotificationAdded) {
		this.javaScriptActionsAfterNotificationAdded = javaScriptActionsAfterNotificationAdded;
	}
	
}
