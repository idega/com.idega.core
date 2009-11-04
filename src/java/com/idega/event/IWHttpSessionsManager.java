package com.idega.event;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWHttpSessionsManager {
	
	private Logger LOGGER = Logger.getLogger(IWHttpSessionsManager.class.getName());
	
	private IWHttpSessionsManager() {
	}
	
	void addSession(HttpSession session) {
		String id = session.getId();
		LOGGER.info("Session created: " + id);
	}
	
	void removeSession(String id) {
	}
}