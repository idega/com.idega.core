//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/

package com.idega.idegaweb;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 - Under development
*Class to serve as a service superclass for background timed services (threads )on an IdegaWeb Application
*/
public abstract class IWTimedService extends IWServiceImpl implements Runnable{

    private Thread thread;

    /**
     * Returns in milliseconds the time between execute intervals
     */
    public abstract long getTimeInterval();


    /**
     * Implement this function for the service executing itself
     */
    public abstract void executeTimedService();


    public final void executeService(){
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }


    public void endService(){
        destroy();
        super.endService();
    }

  private void destroy(){
      this.stop();
  }


  public void stop(){
    thread = null;
  }

  public void run(){

    Thread thisThread = Thread.currentThread();
    while(thread == thisThread){

      executeTimedService();
      try{
        thread.sleep(getTimeInterval());
      }
      catch(InterruptedException ignored){
        ignored.printStackTrace();
      }

    }


  }


}
