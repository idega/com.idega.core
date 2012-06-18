package com.idega.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.util.EventTimer;
import com.idega.util.expression.ELUtil;

public class IWSessionsDaemon implements IWBundleStartable, ActionListener {

	private static final Logger LOGGER = Logger.getLogger(IWSessionsDaemon.class.getName());

	static final String IW_SESSIONS_DAEMON = "IW_SESSIONS_DAEMON";

	@Autowired
	private IWHttpSessionsManager sessionsManager;

	private EventTimer timer;

	@Override
	public void start(IWBundle starterBundle) {
		timer = new EventTimer(EventTimer.THREAD_SLEEP_2_MINUTES, IW_SESSIONS_DAEMON);
		timer.addActionListener(this);
		timer.start(90000);
	}

	@Override
	public void stop(IWBundle starterBundle) {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			if (event.getActionCommand().equals(IW_SESSIONS_DAEMON))
				getSessionsManager().removeUselessSessions();
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error cleaning useless sessions", e);
		}
	}

	private IWHttpSessionsManager getSessionsManager() {
		if (sessionsManager == null) {
			ELUtil.getInstance().autowire(this);
		}
		return sessionsManager;
	}
}