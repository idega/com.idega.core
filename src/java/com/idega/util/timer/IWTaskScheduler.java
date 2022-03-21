package com.idega.util.timer;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
public class IWTaskScheduler {

	private TaskScheduler taskScheduler = null;

	public IWTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(100);	//	Number of threads that can be run at the same time
		taskScheduler.setThreadNamePrefix("iwThreadPoolTaskScheduler");
		this.taskScheduler = taskScheduler;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return taskScheduler;
	}

	/**
	 * Schedules to run daemon every day at specified hour and minute
	 * @param minute
	 * @param hour
	 * @param runnable
	 */
	public void schedule(int minute, int hour, Runnable runnable
    ) {
		//	second, minute, hour, day of month, month, day(s) of week
		StringBuilder expression = new StringBuilder("0 ").append(minute).append(" ").append(hour).append(" * * ?");
		CronTrigger trigger = new CronTrigger(expression.toString());
		taskScheduler().schedule(runnable, trigger);
		Logger.getLogger(getClass().getName()).info("Scheduled " + runnable.getClass().getName() + " at " + expression);
	}

}