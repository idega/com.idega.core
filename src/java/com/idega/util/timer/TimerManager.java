package com.idega.util.timer;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
  * This class implements an timer manager similar to Unix <code>cron</code>
  * and <code>at</code> daemons. It is intended to fire events
  * when timers' date and time match the current ones. Timers are
  * added dynamically and can be one-shot or repetitive
  * (i.e. rescheduled when matched). Time unit is seconds. Timers
  * scheduled less than one second to the current time are rejected (a
  * <code>PastDateException</code> is thrown).<p>
  *
  * The timer scheduler has been designed to
  * manage a large quantity of timers (it uses a priority queue to
  * optimize timer dates selection) and to reduce the use of the CPU
  * time (the TimerManager's thread is started only when there are
  * timers to be managed and it sleeps until the next timer
  * date). <p>
  *
  * Note : because of clocks' skews some timer dates may be erroneous,
  * particularly if the next timer date is scheduled for a remote time
  * (e.g. more than a few days). In order to avoid that problem,
  * well-connected machines can use the <a
  * href="ftp://ftp.inria.fr/rfc/rfc13xx/rfc1305.Z">Network Time
  * Protocol</a> (NTP) to synchronize their clock.<p>
  *
  * Example of use:
  * <pre>
  *  // Creates a new TimerManager
  *  TimerManager mgr = new TimerManager();
  *
  *  // Date timer (non repetitive)
  *  mgr.addTimer(new Date(System.currentTimeMillis() + 300000),
  *               new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("5 minutes later");
  *    }
  *  });
  *
  *  Calendar cal = Calendar.getInstance();
  *  cal.add(Calendar.WEEK_OF_YEAR, 1);
  *  mgr.addTimer(cal.getTime(), new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("One week later");
  *    }
  *  });
  *
  *  // Timer with a delay (in minute) relative to the current time.
  *  mgr.addTimer(1, true, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("1 more minute ! (" + new Date() + ")");
  *    }
  *  });
  *
  *  // Cron-like timer (minute, hour, day of month, month, day of week, year)
  *  // Repetitive when the year is not specified.
  *
  *  mgr.addTimer(-1, -1, -1, -1, -1, -1, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("Every minute (" + new Date() + ")");
  *    }
  *  });
  *
  *  mgr.addTimer(5, -1, -1, -1, -1, -1, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("Every hour at 5' (" + new Date() + ")");
  *    }
  *  });
  *
  *  mgr.addTimer(00, 12, -1, -1, -1, -1, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("Lunch time (" + new Date() + ")");
  *    }
  *  });
  *
  *  mgr.addTimer(07, 14, 1, Calendar.JANUARY, -1, -1, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("Happy birthday Lucas !");
  *    }
  *  });
  *
  *  mgr.addTimer(30, 9, 1, -1, -1, -1, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("On the first of every month at 9:30");
  *    }
  *  });
  *
  *  mgr.addTimer(00, 18, -1, -1, Calendar.FRIDAY, -1, new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("On every Friday at 18:00");
  *    }
  *  });
  *
  *  mgr.addTimer(00, 13, 1, Calendar.AUGUST, -1, 2001,  new TimerListener() {
  *    public void handleTimer(TimerEntry entry) {
  *      System.out.println("2 years that this class was programmed !");
  *    }
  *  });
  * </pre>
  *
  * @author  Olivier Dedieu, David Sims, Jim Lerner
  * @version 1.4, 01/02/2002
  * @modified Eirikur Hrafnsson eiki@idega.is
  */

public class TimerManager {

  protected TimerThread waiter;
  protected SortedSet /* of TimerEntry */ queue; // was a PriorityQueue
  private boolean debug = false;

  private void debug(String s) {
    if (debug)
      System.out.println("[" + Thread.currentThread().getName() + "] TimerManager: " + s);
  }

  /**
    * Creates a new TimerManager. The waiter thread will be started
    * only when the first timer listener will be added.
    *
    * @param isDaemon true if the waiter thread should run as a daemon.
    * @param threadName the name of the waiter thread
    */
  public TimerManager(boolean isDaemon, String threadName) {
    queue = (SortedSet) new TreeSet(); // PriorityQueue(false);
    waiter = new TimerThread(this, isDaemon, threadName);
   }

  /**
    * Creates a new TimerManager. The waiter thread will be started
    * only when the first timer listener will be added. The waiter
    * thread will <i>not</i> run as a daemon.
    */
  public TimerManager() {
    this(false, "TimerManager");
  }

  /**
    * Adds an timer for a specified date.
    *
    * @param date the timer date to be added.
    * @param listener the timer listener.
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * or less than 1 second closed to the current date).
    */
  public synchronized TimerEntry addTimer(Date date,
                             TimerListener listener) throws PastDateException {
    TimerEntry entry = new TimerEntry(date, listener);
    addTimer(entry);
    return entry;
  }

  /**
    * Adds an timer for a specified date.
    *
    * @param date the timer date to be added.
    * @param listener the timer listener.
    * @param name timer name
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * or less than 1 second closed to the current date).
    */
  public synchronized TimerEntry addTimer(Date date,
                             TimerListener listener, String name) throws PastDateException {
    TimerEntry entry = new TimerEntry(date, listener);
    entry.timerName = name;
    addTimer(entry);
    return entry;
  }

  /**
    * Adds an timer for a specified delay.
    *
    * @param delay the timer delay in minute (relative to now).
    * @param isRepetitive <code>true</code> if the timer must be
    * reactivated, <code>false</code> otherwise.
    * @param listener the timer listener.
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public synchronized TimerEntry addTimer(int delay, boolean isRepetitive,
                             TimerListener listener) throws PastDateException {
    TimerEntry entry = new TimerEntry(delay, isRepetitive, listener);
    addTimer(entry);
    return entry;
  }

  /**
    * Adds an timer for a specified delay.
    *
    * @param delay the timer delay in minute (relative to now).
    * @param isRepetitive <code>true</code> if the timer must be
    * reactivated, <code>false</code> otherwise.
    * @param listener the timer listener.
    * @param name timer name
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public synchronized TimerEntry addTimer(int delay, boolean isRepetitive,
                             TimerListener listener, String name) throws PastDateException {
    TimerEntry entry = new TimerEntry(delay, isRepetitive, listener);
    entry.timerName = name;
    addTimer(entry);
    return entry;
  }

  /**
    * Adds an timer for a specified date.
    *
    * @param minute minute of the timer. Allowed values 0-59.
    * @param hour hour of the timer. Allowed values 0-23.
    * @param dayOfMonth day of month of the timer (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfWeek</code>. Allowed values 1-7 (1 = Sunday, 2 =
    * Monday, ...). <code>java.util.Calendar</code> constants can  be used.
    * @param month month of the timer (-1 if every month). Allowed values
    * 0-11 (0 = January, 1 = February, ...). <code>java.util.Calendar</code>
    * constants can be used.
    * @param dayOfWeek day of week of the timer (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfMonth</code>. Allowed values 1-31.
    * @param year year of the timer. When this field is not set
    * (i.e. -1) the timer is repetitive (i.e. it is rescheduled when
    *  reached).
    * @param listener the timer listener.
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public synchronized TimerEntry addTimer(int minute, int hour,
                             int dayOfMonth, int month,
                             int dayOfWeek,
                             int year,
                             TimerListener listener)
    throws PastDateException {

    TimerEntry entry = new TimerEntry(minute, hour,
                                      dayOfMonth, month,
                                      dayOfWeek,
                                      year,
                                      listener);
    addTimer(entry);
    return entry;
  }


  /**
    * Adds an timer for a specified date.
    *
    * @param minute minute of the timer. Allowed values 0-59.
    * @param hour hour of the timer. Allowed values 0-23.
    * @param dayOfMonth day of month of the timer (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfWeek</code>. Allowed values 1-7 (1 = Sunday, 2 =
    * Monday, ...). <code>java.util.Calendar</code> constants can  be used.
    * @param month month of the timer (-1 if every month). Allowed values
    * 0-11 (0 = January, 1 = February, ...). <code>java.util.Calendar</code>
    * constants can be used.
    * @param dayOfWeek day of week of the timer (-1 if every
    * day). This attribute is exclusive with
    * <code>dayOfMonth</code>. Allowed values 1-31.
    * @param year year of the timer. When this field is not set
    * (i.e. -1) the timer is repetitive (i.e. it is rescheduled when
    *  reached).
    * @param listener the timer listener.
    * @param name timer name.
    * @return the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than 1 second closed to the current date).
    */
  public synchronized TimerEntry addTimer(int minute, int hour,
                             int dayOfMonth, int month,
                             int dayOfWeek,
                             int year,
                             TimerListener listener,String name)
    throws PastDateException {

    TimerEntry entry = new TimerEntry(minute, hour,
                                      dayOfMonth, month,
                                      dayOfWeek,
                                      year,
                                      listener,name);
    addTimer(entry);
    return entry;
  }



  /**
    * Adds an timer for a specified TimerEntry
    *
    * @param entry the TimerEntry.
    * @exception PastDateException if the timer date is in the past
    * (or less than one second too closed to the current date).
    */
   public synchronized void addTimer(TimerEntry entry) throws PastDateException {
     debug("Add a new timer entry : " + entry);
     if (queue.add(entry)) {
       debug("This new timer is the top one, update the waiter thread");
       waiter.update(((TimerEntry)queue.first()).timerTime);
//         waiter.update(((TimerEntry)queue.getTop()).timerTime);
     }
  }


  /**
    * Removes the specified TimerEntry.
    *
    * @param entry the TimerEntry that needs to be removed.
    * @return <code>true</code> if there was an timer for this date,
    * <code>false</code> otherwise.
    */
  public synchronized boolean removeTimer(TimerEntry entry) {
    if (!queue.contains(entry)) {
      return false;
    } // if

    // remove the item from the queue
    if (queue.remove(entry)) {
      // do not update the waiter if there are no more items in the queue
      if (queue.size() > 0) {
        waiter.update(( (TimerEntry) queue.first()).timerTime);
      } // if
      //        waiter.update(((TimerEntry)queue.getTop()).timerTime);
    } // if

    return true;
  } // removeTimer()

  /**
    * Removes all the timers. No more timers, even newly added ones, will
    * be fired.
    */
  public synchronized void removeAllTimers() {
  	if(waiter!=null){
	    waiter.stop();
	    waiter = null;
  	}
    queue.clear();
  }

  /**
    Tests whether the supplied TimerEntry is in the manager.

    @param TimerEntry
    @return boolean whether TimerEntry is contained within the manager
    */
  public synchronized boolean containsTimer(TimerEntry timerEntry) {
    return queue.contains(timerEntry);
  } // containsTimer()

  /**
    * Returns a copy of all timers in the manager.
    */
  public synchronized List /* TimerEntry */ getAllTimers() {
    final LinkedList result = new LinkedList();

    Iterator iterator = queue.iterator();
    while (iterator.hasNext()) {
      result.add(iterator.next());
    } // while

    return result;
  } // getAllTimers()

  /**
    * This is method is called when an timer date is reached. It
    * is only be called by the the TimerThread or by itself (if
    * the next timer is in less than 1 second.)
    */
  protected synchronized void notifyListeners() {
    debug("I receive an timer notification");

    // if the queue is empty, there's nothing to do
    if (queue.isEmpty()) {
      return;
    } // if

    // Removes this timer and notifies the listener IF they previous run is finished ( use entry.isDone() )
    TimerEntry entry = (TimerEntry) queue.first();
    queue.remove(entry);
    if(entry.canRun()){
	    try {
	      entry.listener.handleTimer(entry);
	    }
	    catch(Exception e) {
	    		e.printStackTrace();
	    }
  	}
  
    // Reactivates the timer if it is repetitive
    if (entry.isRepetitive) {
      entry.updateTimerTime();
      queue.add(entry);
    }

    // Notifies the TimerThread thread for the next timer
    if (queue.isEmpty()) {
      debug("There is no more timer to manage");
    }
    else {
      long timerTime = ((TimerEntry)queue.first()).timerTime;
      if (timerTime - System.currentTimeMillis() < 1000) {
        debug("The next timer is very close, I notify the listeners now");
        notifyListeners();
      }
      else {
        debug("I update the waiter for " + queue.first());
        waiter.restart(timerTime);
      }
    } // else
  } // notifyListeners()

  /**
    * Stops the waiter thread before ending.
    */
  public void finalize() {
    if (waiter != null)
      waiter.stop();
  }
}
