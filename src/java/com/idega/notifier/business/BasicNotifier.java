package com.idega.notifier.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import com.idega.notifier.presentation.BasicNotification;
import com.idega.notifier.type.NotificationType;
import com.idega.presentation.IWContext;
import com.idega.reverse.ScriptDispatcher;
import com.idega.util.ListUtil;
import com.idega.util.expression.ELUtil;

public abstract class BasicNotifier implements Notifier {

	private static final long serialVersionUID = 1228612843756809914L;

	private static final Logger LOGGER = Logger.getLogger(BasicNotifier.class.getName());
	
	private boolean active;
	
	public boolean canShow(HttpSession session) {
		if (NotificationType.SHOW_ALWAYS == getType()) {
			return true;
		}
		
		String key = getNotificationKey();
		Object attribute = session.getAttribute(key);
		return attribute instanceof Boolean ? !((Boolean) attribute) : true;
	}

	public String getClassNameIdentifier() {
		return "@" + getClass().getName();
	}

	public String getNotificationKey() {
		return "notification_sent_" + getNotificationIdentifier();
	}
	
	public abstract UIComponent getManagementPanel();

	public abstract NotificationType getType();

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public abstract String getNotificationIdentifier();

	public List<BasicNotification> getNotifications(HttpSession session) {
		return null;
	}
	
	public void dispatchNotifications(IWContext iwc) {
		if (!isActive() || !iwc.getApplicationSettings().isReverseAjaxEnabled()) {
			LOGGER.warning("Notifier ("+getClass()+") is not active or/and reverse Ajax is not enabled");
			return;
		}
		
		HttpSession session = iwc.getSession();
		
		List<BasicNotification> notifications = getNotifications(session);
		if (ListUtil.isEmpty(notifications)) {
			return;
		}
		
		List<BasicNotification> notificationsUI = new ArrayList<BasicNotification>(notifications.size());
		for (BasicNotification notification: notifications) {
			if (notification != null) {
				notificationsUI.add(notification);
			}
		}
		
		if (ListUtil.isEmpty(notificationsUI)) {
			return;
		}
		
		ScriptDispatcher scriptDispatcher = getScriptDispatcher();
		if (scriptDispatcher == null) {
			LOGGER.warning("Unable to get instance of " + ScriptDispatcher.class);
			return;
		}
		
		Collection<String> httpSessionsServed = scriptDispatcher.dispatchRenderedComponents(iwc, notificationsUI);
		markNotificationsAsSent(session.getId(), httpSessionsServed);
	}
	
	public void markNotificationsAsSent(String httpSessionCallerId, Collection<String> httpSessionsIds) {
		return;
	}

	private ScriptDispatcher getScriptDispatcher() {
		return ELUtil.getInstance().getBean(ScriptDispatcher.BEAN_IDENTIFIER);
	}
}
