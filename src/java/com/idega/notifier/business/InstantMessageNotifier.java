package com.idega.notifier.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.notifier.bean.InstantMessage;
import com.idega.notifier.presentation.BasicNotification;
import com.idega.notifier.presentation.InstantMessageNotification;
import com.idega.notifier.type.NotificationType;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.TextInput;
import com.idega.servlet.filter.RequestProvider;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class InstantMessageNotifier extends BasicNotifier implements Notifier {

	private static final long serialVersionUID = 9198750757903839409L;

	private static final Logger LOGGER = Logger.getLogger(InstantMessageNotifier.class.getName());
	
	private Map<String, InstantMessage> messages; 
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		
		this.messages = null;
	}

	@Override
	public UIComponent getManagementPanel() {
		Layer layer = new Layer();
		
		TextInput titleInput = new TextInput("setTitle" + getClassNameIdentifier());
		Label titleLabel = new Label("Title", titleInput);
		layer.add(titleLabel);
		layer.add(titleInput);
		
		TextInput messageInput = new TextInput("setMessage" + getClassNameIdentifier());
		Label messageLabel = new Label("Message", messageInput);
		layer.add(messageLabel);
		layer.add(messageInput);
		
		return layer;
	}

	@Override
	public String getNotificationIdentifier() {
		return null;
	}

	@Override
	public NotificationType getType() {
		return NotificationType.SHOW_ONCE;
	}
	
	private Map<String, InstantMessage> getMessages() {
		if (messages == null) {
			messages = new HashMap<String, InstantMessage>();
		}
		
		return messages;
	}
	
	private void addNotification(String id, InstantMessage message) {		
		synchronized (getMessages()) {
			if (message == null) {
				LOGGER.warning("Message is not specified!");
				return;
			}
			
			if (getMessages().get(id) == null) {
				getMessages().put(id, message);
			}
		}
	}
	
	private InstantMessage getMessage(String id) {
		if (StringUtil.isEmpty(id)) {
			LOGGER.warning("ID must be specified!");
		}
		
		InstantMessage message = getMessages().get(id);
		if (message == null) {
			message = new InstantMessage();
		}
		
		return message;
	}
	
	public void setTitle(String title) {
		if (!isActive()) {
			return;
		}
		
		String id = getRequestId();
		
		InstantMessage message = getMessage(id);
		if (message == null) {
			LOGGER.warning("Title can not be set for IM!");
			return;
		}
		
		message.setTitle(title);
		addNotification(id, message);
	}
	
	public void setMessage(String text) {
		if (!isActive()) {
			return;
		}
		
		if (StringUtil.isEmpty(text)) {
			LOGGER.warning("Text must be specified for IM!");
			return;
		}
		
		String id = getRequestId();
		
		InstantMessage message = getMessage(id);
		if (message == null) {
			LOGGER.warning("Text can not be set for IM!");
			return;
		}
		
		message.setMessage(text);
		addNotification(id, message);
	}

	@Override
	public List<BasicNotification> getNotifications(HttpSession session) {
		if (messages == null || messages.isEmpty()) {
			return null;
		}
		
		String id = session.getId();
		
		List<BasicNotification> notifications = new ArrayList<BasicNotification>();
		
		synchronized (getMessages()) {
			for (InstantMessage message: getMessages().values()) {
				String messageText = message.getMessage();
				
				if (StringUtil.isEmpty(messageText)) {
					continue;
				}
				
				if (message.canSendToSession(id)) {
					notifications.add(new InstantMessageNotification(message.getTitle(), messageText));
					message.addSentToSession(id);
				}
			}
		}
		
		return notifications;
	}
	
	@Override
	public void markNotificationsAsSent(String httpSessionCallerId, Collection<String> httpSessionsIds) {
		if (StringUtil.isEmpty(httpSessionCallerId) || ListUtil.isEmpty(httpSessionsIds)) {
			return;
		}
		if (messages == null || messages.isEmpty()) {
			return;
		}
		
		synchronized (getMessages()) {
			for (InstantMessage message: getMessages().values()) {
				if (message.getSentToSessions().contains(httpSessionCallerId)) {
					message.addSentToSession(httpSessionsIds);
				}
			}
		}
	}

	private String getRequestId() {
		RequestProvider requestProvider = ELUtil.getInstance().getBean(RequestProvider.class);
		if (requestProvider == null) {
			return null;
		}
		
		return requestProvider.getRequest().toString();
	}

}
