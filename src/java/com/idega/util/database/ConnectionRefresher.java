//idega 2000 - Tryggvi Larusson
/*

*Copyright 2000 idega.is All Rights Reserved.

*/
package com.idega.util.database;

import com.idega.data.IDONoDatastoreError;

/**

 *

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.3

* Class to deliver database connections through a poolmanager

*/
public class ConnectionRefresher implements Runnable
{
	private Thread refresher;
	private ConnectionPool pool;
	private long refreshIntervalMillis;
	private long lastRun;
	protected ConnectionRefresher(ConnectionPool pool, long refreshIntervalMillis)
	{
		this.pool = pool;
		this.refreshIntervalMillis = refreshIntervalMillis;
		lastRun=System.currentTimeMillis();
		refresher = new Thread(this);
		refresher.setPriority(Thread.MIN_PRIORITY);
		refresher.start();
	}
	public void run()
	{
		Thread thisThread = Thread.currentThread();
		while (refresher == thisThread)
		{
			try
			{
				refresher.sleep(this.refreshIntervalMillis + Math.round((this.refreshIntervalMillis / 2) * Math.random()));
				runRefresh();
			}
			catch (InterruptedException ex)
			{
				System.err.println("There was an InterruptedException in ConnectionRefresher.run() The error was: " + ex.getMessage());
			}
		}
	}
	
	
	public synchronized void runRefresh()
	{
			//refresher.interrupt();
			try
			{
				//refresher.sleep(this.refreshIntervalMillis + Math.round((this.refreshIntervalMillis / 2) * Math.random()));
				pool.refresh();
			}
			catch (Exception ex)
			{
				System.err.println("There was an Exception in ConnectionRefresher.run() The error was: " + ex.getMessage());
			}
			catch (IDONoDatastoreError ex)
			{
				System.err.println("There was an IDONoDatastoreError in ConnectionRefresher.run() The error was: " + ex.getMessage());
			}
	}
	
	public void stop()
	{
		refresher = null;
	}
}
