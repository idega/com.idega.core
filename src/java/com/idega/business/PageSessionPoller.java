package com.idega.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.component.bean.RenderedComponent;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.notifier.business.Notifier;
import com.idega.notifier.presentation.BasicNotification;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;

public class PageSessionPoller {
	
	public boolean isPollingSettingEnabled() {
		IWMainApplicationSettings applicationSettings = IWMainApplication.getDefaultIWMainApplication().getSettings();
		
		return applicationSettings.getIfUseSessionPolling();
	}
	
	public RenderedComponent pollSession(String ping) {
		return getNotifications();
	}
	
	@SuppressWarnings("unchecked")
	private RenderedComponent getNotifications() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		Map<String, ? extends Notifier> notifiers = WebApplicationContextUtils.getWebApplicationContext(iwc.getServletContext()).getBeansOfType(Notifier.class);
		if (notifiers == null || notifiers.isEmpty()) {
			return null;
		}
		
		boolean reverseAjaxEnabled = iwc.getApplicationSettings().isReverseAjaxEnabled();
		HttpSession session = iwc.getSession();
		List<BasicNotification> notifications = reverseAjaxEnabled ? null : new ArrayList<BasicNotification>();
		
		for (Notifier notifier: notifiers.values()) {
			if (notifier.isActive()) {
				if (reverseAjaxEnabled) {
					notifier.dispatchNotifications(iwc);
				} else {
					List<BasicNotification> notificationsOfNotifier = notifier.getNotifications(session);
					if (!ListUtil.isEmpty(notificationsOfNotifier)) {
						notifications.addAll(notificationsOfNotifier);
					}
				}
			}
		}
		
		if (ListUtil.isEmpty(notifications)) {
			return null;
		}
		
		BuilderService builderService = null;
		try {
			builderService = BuilderServiceFactory.getBuilderService(iwc);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (builderService == null) {
			return null;
		}
		
		Layer container = new Layer();
		for (BasicNotification notification: notifications) {
			container.add(notification);
		}
		
		return builderService.getRenderedComponent(container, null);
	}
}
