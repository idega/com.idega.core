package com.idega.util.timer;


/**
  * The listener interface for receiving timer events.
  *
  * @author  Olivier Dedieu, David Sims, Jim Lerner
  * @version 1.4, 01/02/2002
  * @modified Eirikur Hrafnsson eiki@idega.is
  */
public interface TimerListener {

  /**
    * Invoked when an timer is triggered.
    *
    * @param entry the TimerEntry which has been triggered.
    */
  public abstract void handleTimer(TimerEntry entry);
}

