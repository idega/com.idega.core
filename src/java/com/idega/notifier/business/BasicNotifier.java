package com.idega.notifier.business;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import com.idega.notifier.presentation.BasicNotification;
import com.idega.notifier.type.NotificationType;

public abstract class BasicNotifier implements Notifier {

	private static final long serialVersionUID = 1228612843756809914L;

	private boolean active;
	
	public boolean canShow(HttpSession session) {
		if (NotificationType.SHOW_ALWAYS == getType()) {
			return true;
		}
		
		String key = getNotificationKey();
		Object attribute = session.getAttribute(key);
		return attribute instanceof Boolean ? !((Boolean) attribute) : true;
	}

	public String getValueIdentifier() {
		return "@" + getClass().getName();
	}

	public String getNotificationKey() {
		return "notification_sent_" + getNotificationIdentifier();
	}
	
	public abstract UIComponent getManagementPanel();

	public abstract BasicNotification getNotification(HttpSession session);

	public abstract NotificationType getType();

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public abstract String getNotificationIdentifier();

}
