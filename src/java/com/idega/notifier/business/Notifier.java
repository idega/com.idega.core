package com.idega.notifier.business;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import com.idega.business.SpringBeanName;
import com.idega.notifier.presentation.BasicNotification;
import com.idega.notifier.type.NotificationType;

@SpringBeanName("idegaWebNotifier")
public interface Notifier extends Serializable {
	
	public abstract BasicNotification getNotification(HttpSession session);
	
	public abstract boolean isActive();
	
	public abstract void setActive(boolean active);
	
	public abstract UIComponent getManagementPanel();

	public abstract NotificationType getType();
	
	public abstract boolean canShow(HttpSession session);
	
	public abstract String getNotificationKey();
	public abstract String getNotificationIdentifier();
	public abstract String getValueIdentifier();
}
