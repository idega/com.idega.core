package com.idega.util.timer;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.idega.util.CoreConstants;

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
	public void schedule(int minute, int hour, Runnable runnable) {
		schedule(0, minute, hour, runnable);
	}

	public void schedule(int everyMinutes, Runnable runnable) {
		schedule(-1, everyMinutes, -1, true, runnable);
	}

	/**
	 * Schedules to run daemon every day at specified hour and minute
	 * @param second
	 * @param minute
	 * @param hour
	 * @param runnable
	 */
	public void schedule(int second, int minute, int hour, Runnable runnable) {
		schedule(second, minute, hour, false, runnable);
	}

	private void schedule(int second, int minute, int hour, boolean everyMinutes, Runnable runnable) {
		//	second, minute, hour, day of month, month, day(s) of week
		StringBuilder expression = new StringBuilder()
				.append((second < 0 ? 0 : second)).append(CoreConstants.SPACE)
				.append((minute < 0 ? CoreConstants.STAR : (everyMinutes ? CoreConstants.STAR.concat(CoreConstants.SLASH).concat(String.valueOf(minute)) : minute))).append(CoreConstants.SPACE)
				.append((hour < 0 ? CoreConstants.STAR : hour))
				.append(" * * ?");
		CronTrigger trigger = new CronTrigger(expression.toString());
		taskScheduler().schedule(runnable, trigger);
		Logger.getLogger(getClass().getName()).info("Scheduled " + runnable.getClass().getName() + " at " + expression);
	}

}