package com.idega.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IWHttpSessionsManager {

	private Map<String, HttpSession> sessions;
	
	private IWHttpSessionsManager() {
		sessions = new HashMap<String, HttpSession>();
	}
	
	void addSession(HttpSession session) {
		String id = session.getId();
		sessions.put(id, session);
	}
	
	void removeSession(HttpSession session) {
		String id = session.getId();
		sessions.remove(id);
	}
	
	public int getSessionsCounted() {
		return sessions.size();
	}
	
	@SuppressWarnings("deprecation")
	int removeUselessSessions() {
		if (sessions.isEmpty()) {
			return 0;
		}
		
		Collection<HttpSession> sessionsCollection = new ArrayList<HttpSession>(sessions.values());
		
		int count = 0;
		long currentTime = System.currentTimeMillis();
		for (HttpSession session: sessionsCollection) {
			long idleTime = currentTime - session.getLastAccessedTime();
			if (idleTime >= 60000) {
				//	Session "was" idle for minute or more
				Object principal = session.getValue("org.apache.slide.webdav.method.principal");
				//	Checking if session was created by Slide's root user
				if (principal instanceof String && "root".equals(principal)) {
					session.invalidate();
					count++;
				}
			}
		}
		return count;
	}	
}