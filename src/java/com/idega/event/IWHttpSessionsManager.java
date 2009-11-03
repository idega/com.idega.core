package com.idega.event;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWHttpSessionsManager {

	private static final Logger LOGGER = Logger.getLogger(IWHttpSessionsManager.class.getName());
	
	private Map<String, HttpSession> sessions;
	
	private IWHttpSessionsManager() {
		sessions = new HashMap<String, HttpSession>();
	}
	
	void addSession(HttpSession session) {
		String id = session.getId();
		sessions.put(id, session);
		
		LOGGER.info("New session created: " + id);
	}
	
	void removeSession(HttpSession session) {
		String id = session.getId();
		sessions.remove(id);
		
//		LOGGER.info("Session was destroyed: " + id);
	}
	
	public int getSessionsCounted() {
		return sessions.size();
	}
	
}
