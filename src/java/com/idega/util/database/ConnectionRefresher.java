//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/
package com.idega.util.database;

import com.idega.data.IDONoDatastoreError;

/**
* @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
* @version 1.3
* Class to deliver database connections through a poolmanager
*/
public class ConnectionRefresher implements Runnable {
	private Thread refresher;
	private ConnectionPool pool;
	private long setRefreshIntervalMillis;
	private long emergencyIntervalMillis=30*1000;//30 secs.
	private long lastRun;
	private boolean datastoreNotReachable=false;

	private boolean isRunning = false;
	
	protected ConnectionRefresher(ConnectionPool pool, long refreshIntervalMillis) {
		this.pool = pool;
		this.setRefreshIntervalMillis=refreshIntervalMillis;
		setRefreshIntervalMillis = refreshIntervalMillis;
		lastRun=System.currentTimeMillis();
		start();
	}
	
	public void run() {
		//Thread thisThread = Thread.currentThread();
		while (isRunning) {
			try {
				//refresher.sleep(this.setRefreshIntervalMillis + Math.round((this.setRefreshIntervalMillis / 2) * Math.random()));
				refresher.sleep(getSleepTime());
				runRefresh();
			}
			catch (InterruptedException ex) {
				if (isRunning)
					System.err.println("There was an InterruptedException in ConnectionRefresher.run() The error was: " + ex.getMessage());
			}
		}
	}
	
	
	public synchronized void runRefresh() {
			//refresher.interrupt();
		try {
			pool.refresh();
			datastoreNotReachable=false;
		}
		catch (Exception ex) {
			System.err.println("There was an Exception in ConnectionRefresher.run() The error was: " + ex.getMessage());
		}
		catch (IDONoDatastoreError ex) {
			datastoreNotReachable=true;
			System.err.println("There was an IDONoDatastoreError in ConnectionRefresher.run() The error was: " + ex.getMessage());
		}
		catch (Throwable th) {
			datastoreNotReachable=true;
			System.err.println("There was a Throwable caught in ConnectionRefresher.run() The error was: " + th.getClass().getName()+" : "+th.getMessage());
		}
	}
	
	private long getSleepTime() {
		if (datastoreNotReachable) {
			return this.emergencyIntervalMillis;	
		}
		else {
			return this.setRefreshIntervalMillis + Math.round((this.setRefreshIntervalMillis / 2) * Math.random());	
		}
	}
	
	public void stop() {
		isRunning = false;
		refresher.interrupt();
		refresher = null;
	}
	
	public void start() {
		isRunning = true;
		if (refresher==null) {
			refresher = new Thread(this, "IWConnectionRefresher["+pool.getName()+"]");
			refresher.setPriority(Thread.MIN_PRIORITY);
			refresher.start();
		}
	}
}