package com.idega.util.timer;

/**
  * This class manages the thread which sleeps until the next timer.
  * Methods are synchronized to prevent interference fromthe TimerThread thread
  * and external threads.
  *
  * @author  Olivier Dedieu, David Sims, Jim Lerner
  * @version 1.4, 01/02/2002
  * @modified Eirikur Hrafnsson eiki@idega.is
  */
public class TimerThread implements Runnable {
  protected TimerManager mgr;
  protected Thread thread;
  private long sleepUntil = -1;
  private boolean debug = false;
  private boolean shutdown = false;

  private void debug(String s) {
    if (this.debug) {
			System.out.println("[" + Thread.currentThread().getName() + "] TimerThread: " + s);
		}
  }

  /**
    * Creates a new TimerThread.
    *
    * @param isDaemon true if the waiter thread should run as a daemon.
    * @param threadName the name of the waiter thread
    */
  public TimerThread(TimerManager mgr, boolean isDaemon, String waiterName) {
    this.mgr = mgr;

    // start the thread
    this.thread = new Thread(this, waiterName);
    this.thread.setDaemon(isDaemon);
    this.thread.start();
  }

  /**
    * Updates the time to sleep.
    *
    * @param sleepUntil the new time to sleep until.
    */
  public synchronized void update(long sleepUntil) {
    this.sleepUntil = sleepUntil;
    debug("Update for " + sleepUntil); // timeToSleep);
    debug("I notify the thread to update its sleeping time");
    notify();
  }

  /**
    * Restarts the thread for a new time to sleep until.
    *
    * @param sleepUntil the new time to sleep until.
    */
  public synchronized void restart(long sleepUntil) {
    this.sleepUntil = sleepUntil;
    notify();
  }

  /**
    * Stops (destroy) the thread.
    */
  public synchronized void stop() {
    this.shutdown = true;
    notify();
  }


  public synchronized void run() {
    debug("I'm running");
    while(!this.shutdown) {
      try {
        // check if there's timer here
        if (this.sleepUntil <= 0) {
          // no timer here. So wait for a new timer to come along.
          wait();
        } // if
        else {
          // yes, there's an timer here. Wait until the timer is ready.
          long timeout = this.sleepUntil - System.currentTimeMillis();
          if (timeout > 0) {
            wait(timeout);
          } // if
        } // if

        // now that we've awakened again, check if an timer is due (within
        // 1 second)
        if (this.sleepUntil >= 0 && (this.sleepUntil - System.currentTimeMillis() < 1000)) {
          // yes, an timer is ready. Notify the listeners.
          this.sleepUntil = -1;
          debug("notifying listeners");
          this.mgr.notifyListeners();
        } // if

      }
      catch(InterruptedException e) {
        debug("I'm interrupted");
        stop();
      }
    }
    debug("I'm stopping");
  }

}


