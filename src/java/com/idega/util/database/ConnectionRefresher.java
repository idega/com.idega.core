//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/

package com.idega.util.database;



import java.util.*;

import java.io.*;





/**

 *

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.3

* Class to deliver database connections through a poolmanager

*/

public class ConnectionRefresher implements Runnable{



  private Thread refresher;

  private ConnectionPool pool;

  private long refreshIntervalMillis;



  protected ConnectionRefresher(ConnectionPool pool,long refreshIntervalMillis){

    this.pool = pool;

    this.refreshIntervalMillis = refreshIntervalMillis;

    refresher=new Thread(this);

    refresher.setPriority(Thread.MIN_PRIORITY);

    refresher.start();

  }



  public void run(){

    Thread thisThread = Thread.currentThread();

    while(refresher == thisThread){

      try{

        refresher.sleep(this.refreshIntervalMillis+Math.round((this.refreshIntervalMillis/2)*Math.random()));

        pool.refresh();

      }

      catch(InterruptedException ex){

        System.err.println("There was an error in ConnectionRefresher.run() The error was: "+ex.getMessage());

      }



    }

  }



  public void stop(){

    refresher=null;

  }



}
