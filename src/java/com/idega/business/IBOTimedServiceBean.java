package com.idega.business;

import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWService;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 0.5 - Under development
*Class to serve as a service superclass for background timed services (threads )on an IdegaWeb Application
 * Copyright:    Copyright (c) 2002
 * Company:      idega
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
*/
public abstract class IBOTimedServiceBean extends IBOServiceBean implements Runnable,IWService,IBOService {

  public IBOTimedServiceBean() {
  }
  public String getServiceName() {
    return super.getServiceDescription();
  }
  public void startService(IWMainApplication superApplication) {
    /**@todo: Implement this com.idega.idegaweb.IWService method*/
    //throw new java.lang.UnsupportedOperationException("Method startService() not yet implemented.");
    executeService();
  }

  /**
   * Override this method for setting the time between runs in milliseconds
   *
   */
  public long getTimeUntilNextRun(){
    return 1000*60*60;
  }

  /**
   * Override this method for getting the time when the service is run the next time
   *
   */
  public java.util.Date getDateOnNextRun(){
    com.idega.util.IWTimestamp stamp = com.idega.util.IWTimestamp.RightNow();
    stamp.addMinutes((int)getTimeUntilNextRun()/(1000*60));
    return stamp.getSQLDate();
    //return 1000*60*60;
  }

  public void cancel(){
    this.stop();
  }

    private Thread thread;

    /**
     * Returns in milliseconds the time between execute intervals
     */
    //public abstract long getTimeInterval();


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
        //super.endService();
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

      try{
        Thread.sleep(getTimeUntilNextRun());
        executeTimedService();
      }
      catch(InterruptedException ignored){
        ignored.printStackTrace();
      }

    }


  }







}