package com.idega.util;

/**
 * Title:        idega default
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>, borrowed and modified from www.javaworld.com
 * @version 1.0
 */

import java.util.Hashtable;
import java.util.Enumeration;

public abstract class ObjectPool
{

  public ObjectPool()
  {
   expirationTime = 30000; // 30 seconds
   locked = new Hashtable();
   unlocked = new Hashtable();
  }

   private long expirationTime;
   private Hashtable locked, unlocked;

   abstract Object create();
   abstract boolean validate( Object o );
   abstract void expire( Object o );

    synchronized Object checkOut()
    {
       long now = System.currentTimeMillis();
       Object o;
       if( unlocked.size() > 0 )
       {
          Enumeration e = unlocked.keys();
          while( e.hasMoreElements() )
          {
             o = e.nextElement();
             if( ( now - ( ( Long ) unlocked.get( o ) ).longValue() ) > expirationTime )
             {
                // object has expired
                unlocked.remove( o );
                expire( o );
                o = null;
             }
             else
             {
                if( validate( o ) )
                {
                   unlocked.remove( o );
                   locked.put( o, new Long( now ) );
                   return( o );
                }
                else
                {
                   // object failed validation
                   unlocked.remove( o );
                   expire( o );
                   o = null;
                }
             }
          }
       }
       // no objects available, create a new one
       o = create();
       locked.put( o, new Long( now ) );
       return( o );
    }


    synchronized void checkIn( Object o )
    {
       locked.remove( o );
       unlocked.put( o, new Long( System.currentTimeMillis() ) );
    }

    /**
     * @todo implement
     *
     * checkIn(Object o, Object key)
     *
     * checkOut(Object key)
     *
     */


}
