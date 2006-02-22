package com.idega.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.AWTEventMulticaster;

/**
 * Description:  A threaded event dispatcher class. add an actionlistener to it and it calls
 * it's public synchronized void actionPerformed(ActionEvent e) method with a named actioncommand (default name
 * is "iw_event_timer" and default time is 5 seconds). Just add this line in your actionPerformed method and then parse the string to match your set
 * action command. example (code in your ActionListener implementor class) :
 * <br>  public synchronized void actionPerformed(ActionEvent e){<br>
 * String action = e.getActionCommand();<br>
 * if(action.equalsIgnoreCase("event_timer")){ doStuff() } <br>
 * }
 * Copyright:    Copyright (c) 2001
 * Company:      Idega Software
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 */

public class EventTimer implements Runnable{
  private ActionListener listener;
  private Thread t;
  public static long DEFAULT_THREAD_SLEEP_5_SECONDS = 5000;
  public static long THREAD_SLEEP_0_5_SECONDS = 500;
  public static long THREAD_SLEEP_1_SECONDS = 1000;
  public static long THREAD_SLEEP_2_SECONDS = 2000;
  public static long THREAD_SLEEP_5_SECONDS = 5000;
  public static long THREAD_SLEEP_10_SECONDS = 10000;
  public static long THREAD_SLEEP_30_SECONDS = 30000;
  public static long THREAD_SLEEP_1_MINUTE = 60000;
  public static long THREAD_SLEEP_2_MINUTES = 120000;
  public static long THREAD_SLEEP_5_MINUTES = 300000;
  public static long THREAD_SLEEP_10_MINUTES = 600000;
  public static long THREAD_SLEEP_30_MINUTES = 1800000;
  public static long THREAD_SLEEP_1_HOUR = 3600000;
  public static long THREAD_SLEEP_24_HOURS = 86400000;
  private long interval = DEFAULT_THREAD_SLEEP_5_SECONDS;
  private String actionCommand = "iw_event_timer";
  private long intervalToSleepOnStart = 0;


  private boolean runThread = false;
  private boolean isRunning = false;

  public EventTimer() {
  }

  public EventTimer(String actionCommand) {
    setActionCommand(actionCommand);
  }

  public EventTimer(long interval) {
    setInterval(interval);
  }

  public EventTimer(long interval, String actionCommand) {
    this(interval);
    setActionCommand(actionCommand);
  }

  public void run(){
  	isRunning = true;
    while (runThread) {
      try {
      	if (intervalToSleepOnStart!=0) {
      		Thread.sleep(intervalToSleepOnStart);
      		intervalToSleepOnStart=0;
      	}
        if (listener != null) listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));
        Thread.sleep(interval);
      }
      catch (Exception e) {
      	if (runThread)
	        e.printStackTrace(System.out);
      }
    }
    
    isRunning = false;
  }



	/**
	 * Starts the thread normally but tells the thread to wait for intervalToSleepOnStart millis before normal start happens.
	 **/
  public void start(long intervalToSleepOnStart){
    this.intervalToSleepOnStart=intervalToSleepOnStart;
    start();
  }


  public void start(){
    runThread = true;
    if( t == null || !isRunning){
    		//a new thread must be created here because it was null or 
		//we went out of the run() method. When run is finished the thread is considered dead and cannot be restarted
      t = new Thread(this,"com.idega.util.EventTimer thread");
      t.setPriority(Thread.MIN_PRIORITY);
      t.start();
    }
  }

  public void stop(){
    if ( t != null ){
      runThread = false;
      t.interrupt();
    }
  }

   /**Destroy the thread*/
  public void destroy() {
    stop();
    t = null;
  }

  public void setInterval(long interval){
    this.interval = interval;
  }

  public void setActionCommand(String actionCommand){
    this.actionCommand = actionCommand;
  }

  public long getInterval(){
    return interval;
  }

  public String getActionCommand(){
    return actionCommand;
  }

  public void addActionListener(ActionListener l) {
    listener = AWTEventMulticaster.add(listener, l);
  }

  public void removeActionListener(ActionListener l) {
    listener = AWTEventMulticaster.remove(listener, l);
  }

}
