package com.idega.notifier.business;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import com.idega.business.SpringBeanName;
import com.idega.notifier.presentation.BasicNotification;
import com.idega.notifier.type.NotificationType;
import com.idega.presentation.IWContext;

@SpringBeanName("idegaWebNotifier")
public interface Notifier extends Serializable {
	
	public abstract List<BasicNotification> getNotifications(HttpSession session);
	
	public abstract void dispatchNotifications(IWContext iwc);
	
	public abstract boolean isActive();
	
	public abstract void setActive(boolean active);
	
	public abstract UIComponent getManagementPanel();

	public abstract NotificationType getType();
	
	public abstract boolean canShow(HttpSession session);
	
	public abstract String getNotificationKey();
	public abstract String getNotificationIdentifier();
	
	public abstract String getClassNameIdentifier();
	
	public abstract void markNotificationsAsSent(String httpSessionCallerId, Collection<String> httpSessionsIds);
}
