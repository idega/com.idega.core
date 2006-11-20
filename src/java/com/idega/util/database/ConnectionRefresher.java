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
	// not used private long lastRun;
	private boolean datastoreNotReachable=false;

	private boolean isRunning = false;
	
	protected ConnectionRefresher(ConnectionPool pool, long refreshIntervalMillis) {
		this.pool = pool;
		this.setRefreshIntervalMillis=refreshIntervalMillis;
		this.setRefreshIntervalMillis = refreshIntervalMillis;
		// lastRun=System.currentTimeMillis();
		start();
	}
	
	public void run() {
		//Thread thisThread = Thread.currentThread();
		while (this.isRunning) {
			try {
				//refresher.sleep(this.setRefreshIntervalMillis + Math.round((this.setRefreshIntervalMillis / 2) * Math.random()));
				Thread.sleep(getSleepTime());
				runRefresh();
			}
			catch (InterruptedException ex) {
				System.out.println("Killing ConectionRefresher (caused by interrupt)");
				if (this.isRunning) {
					System.err.println("There was an InterruptedException in ConnectionRefresher.run() The error was: " + ex.getMessage());
				}
			}
		}
	}
	
	
	public synchronized void runRefresh() {
			//refresher.interrupt();
		try {
			this.pool.refresh();
			this.datastoreNotReachable=false;
		}
		catch (Exception ex) {
			System.err.println("There was an Exception in ConnectionRefresher.run() The error was: " + ex.getMessage());
		}
		catch (IDONoDatastoreError ex) {
			this.datastoreNotReachable=true;
			System.err.println("There was an IDONoDatastoreError in ConnectionRefresher.run() The error was: " + ex.getMessage());
		}
		//catch (Throwable th) {
		//	datastoreNotReachable=true;
		//	System.err.println("There was a Throwable caught in ConnectionRefresher.run() The error was: " + th.getClass().getName()+" : "+th.getMessage());
			// See description of class ThreadDeath:
			// An application should catch instances of this class only if it must clean up after being terminated asynchronously. 
			// If ThreadDeath is caught by a method, it is important that it be rethrown so that the thread actually dies.
			// changed by Thomas 
			//
			// !!!! Do not change this code. If this thread never dies the application will not restart proper !!!
			// 
		//	if (th instanceof ThreadDeath) {
		//		System.out.println("Killing ConnectionRefresher (caused by ThreadDeath)");
		//		throw (Error) th;
		//	}
		//}
	}
	
	private long getSleepTime() {
		if (this.datastoreNotReachable) {
			return this.emergencyIntervalMillis;	
		}
		return this.setRefreshIntervalMillis + Math.round((this.setRefreshIntervalMillis / 2) * Math.random());	
	}
	
	public void stop() {
		System.out.println("Stopping ConnectionRefresher");
		this.isRunning = false;
		this.refresher.interrupt();
		this.refresher= null;
	}
	
	public void start() {
		this.isRunning = true;
		if (this.refresher==null) {
			this.refresher = new Thread(this, "IWConnectionRefresher["+this.pool.getName()+"]");
			this.refresher.setDaemon(true);
			this.refresher.setPriority(Thread.MIN_PRIORITY);
			this.refresher.start();
		}
	}
}