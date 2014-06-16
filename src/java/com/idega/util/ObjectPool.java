package com.idega.util;

/**
 * Title:        idega default
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>, borrowed and modified from www.javaworld.com
 * @version 1.0
 */

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ObjectPool
{

  public ObjectPool()
  {
   this.expirationTime = 30000; // 30 seconds
   this.locked = new ConcurrentHashMap<Object, Long>();
   this.unlocked = new ConcurrentHashMap<Object, Long>();
  }

   private long expirationTime;
   private Map<Object, Long> locked, unlocked;

   abstract Object create();
   abstract boolean validate( Object o );
   abstract void expire( Object o );

   Object checkOut()
    {
       long now = System.currentTimeMillis();
       if( this.unlocked.size() > 0 )
       {
    	  Set<Object> e = this.unlocked.keySet();
         for (Object o : e) {
             if( ( now - this.unlocked.get( o ).longValue() ) > this.expirationTime )
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
       Object o = create();
       this.locked.put( o, new Long( now ) );
       return( o );
    }


   void checkIn( Object o )
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
