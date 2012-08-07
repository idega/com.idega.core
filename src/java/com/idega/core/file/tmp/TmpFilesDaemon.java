package com.idega.core.file.tmp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.idegaweb.IWMainApplicationShutdownEvent;
import com.idega.idegaweb.IWMainApplicationStartedEvent;
import com.idega.util.EventTimer;
import com.idega.util.IWTimestamp;

/**
 * @author <a href="mailto:civilis@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 *          Last modified: $Date: 2008/11/05 16:39:41 $ by $Author: laddi $
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service
public class TmpFilesDaemon implements ApplicationContextAware, ApplicationListener, ActionListener {

	public static final String THREAD_NAME = "uploaded_file_Daemon";
	public static final Logger logger = Logger.getLogger(TmpFilesDaemon.class.getName());
	private EventTimer fileTimer;
	private TmpFilesDaemon deamon;
	private TmpFilesManager fManager;
	private ApplicationContext ctx;

	public void start() {
		// checking uploaded files every hour.
		this.fileTimer = new EventTimer(EventTimer.THREAD_SLEEP_1_HOUR, THREAD_NAME);
		this.fileTimer.addActionListener(this);
		// Starts the thread after 5 mins.
		this.fileTimer.start(EventTimer.THREAD_SLEEP_5_MINUTES);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			if (event.getActionCommand().equalsIgnoreCase(THREAD_NAME)) {
				logger.fine("[Tmp file Daemon - " + IWTimestamp.RightNow().toString() + "] - Periodical cleanup of temporary files");
				getFManager().doPeriodicalCleanup();
			}

		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Exception while doing periodical cleanup of tmp files", e);
		}
	}

	public void stop() {
		if (this.fileTimer != null) {
			this.fileTimer.stop();
			this.fileTimer = null;
		}
	}

	private List<TmpFileResolver> resResolvers;

	@Autowired(required = false)
	public void setResResolvers(List<TmpFileResolver> resResolvers) {

		this.resResolvers = resResolvers;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationevent) {
		if (applicationevent instanceof IWMainApplicationStartedEvent) {
			getFManager().init(resResolvers);

			deamon = new TmpFilesDaemon();
			deamon.setApplicationContext(ctx);
			deamon.setFManager(getFManager());
			deamon.start();
		}
		else if (applicationevent instanceof IWMainApplicationShutdownEvent && deamon != null) {
			deamon.stop();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationcontext) throws BeansException {
		ctx = applicationcontext;
	}

	public TmpFilesManager getFManager() {
		return fManager;
	}

	@Autowired
	public void setFManager(TmpFilesManager manager) {
		fManager = manager;
	}
}
