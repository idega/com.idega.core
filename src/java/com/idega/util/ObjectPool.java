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
   this.expirationTime = 30000; // 30 seconds
   this.locked = new Hashtable();
   this.unlocked = new Hashtable();
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
       if( this.unlocked.size() > 0 )
       {
          Enumeration e = this.unlocked.keys();
          while( e.hasMoreElements() )
          {
             o = e.nextElement();
             if( ( now - ( ( Long ) this.unlocked.get( o ) ).longValue() ) > this.expirationTime )
             {
                // object has expired
                this.unlocked.remove( o );
                expire( o );
                o = null;
             }
             else
             {
                if( validate( o ) )
                {
                   this.unlocked.remove( o );
                   this.locked.put( o, new Long( now ) );
                   return( o );
                }
                else
                {
                   // object failed validation
                   this.unlocked.remove( o );
                   expire( o );
                   o = null;
                }
             }
          }
       }
       // no objects available, create a new one
       o = create();
       this.locked.put( o, new Long( now ) );
       return( o );
    }


    synchronized void checkIn( Object o )
    {
       this.locked.remove( o );
       this.unlocked.put( o, new Long( System.currentTimeMillis() ) );
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
