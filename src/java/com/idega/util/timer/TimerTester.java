package com.idega.util.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

/**
  * This class run a bunch of tests.
  *
  * @author  Olivier Dedieu
  * @version 1.4, 01/02/2002
  * @modified Eirikur Hrafnsson eiki@idega.is
  */

public class TimerTester {
  public static void main(String[] args) throws Exception {

    TimerManager mgr = new TimerManager();

    long current = System.currentTimeMillis();
    System.out.println("Current date is " + new Date(current));

    TimerListener listener = new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("\u0007Timer : " + entry);
      }
    };

    // Date timer
    mgr.addTimer(new Date(current + (60 * 1000)), listener);
    mgr.addTimer(new Date(current + (30 * 1000)), listener);
    mgr.addTimer(new Date(current + (40 * 1000)), listener);
    mgr.addTimer(new Date(current + (20 * 1000)), listener);
    mgr.addTimer(new Date(current + (10 * 1000)), listener);
    mgr.addTimer(new Date(current + (50 * 1000)), listener);

    mgr.addTimer(new Date(System.currentTimeMillis() + 300000),
                 new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("5 minutes later");
      }
    });

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.WEEK_OF_YEAR, 1);
    mgr.addTimer(cal.getTime(), new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("\u0007One week later");
      }
    });


    // Elapsed-time timer
    mgr.addTimer(1, true, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("dring ! (" + new Date() + ")");
      }
    });


    // Cron-like timer (minute, hour, day of month, month, day of week, year)
    mgr.addTimer(-1, -1, -1, -1, -1, -1, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("Every minute (" + new Date() + ")");
      }
    });

    mgr.addTimer(5, -1, -1, -1, -1, -1, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("Every hour at 5' (" + new Date() + ")");
      }
    });

    mgr.addTimer(00, 12, -1, -1, -1, -1, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("Lunch time (" + new Date() + ")");
      }
    });

    mgr.addTimer(24, 15, 11, Calendar.AUGUST, -1, -1, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("Valerie's birthday");
      }
    });

    mgr.addTimer(30, 9, 1, -1, -1, -1, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("On the first of every month at 9:30");
      }
    });

    mgr.addTimer(00, 18, -1, -1, Calendar.FRIDAY, -1, new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("On every Friday at 18:00");
      }
    });

    mgr.addTimer(0, 0, 1, Calendar.JANUARY, -1, 2001,  new TimerListener() {
      public void handleTimer(TimerEntry entry) {
        System.out.println("Does it work ?");
      }
    });

    System.out.println("Here are the registered timers: ");
    System.out.println("----------------------------");
    List list = mgr.getAllTimers();
    for(Iterator it = list.iterator(); it.hasNext();) {
      System.out.println("- " + it.next());
    }
    System.out.println("----------------------------");
  }
}

