/*
 * $Id: EntityBulkUpdater.java,v 1.1 2001/05/25 00:26:36 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;

import com.idega.data.GenericEntity;
import com.idega.util.database.ConnectionBroker;
import java.util.Vector;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 */
public class EntityBulkUpdater {
  private Vector insert_ = null;
  private Vector update_ = null;

  public static String insert = "insert";
  public static String update = "update";

  public EntityBulkUpdater() {
  }

  /**
   * Adds a GenericEntity to the list of elememts to be updated/inserted
   * into the database.
   *
   * @param entity The entity to update/insert
   * @param action Should the entity be updated or inserted. If null then insert.
   *
   * @return True if the entity was added to the list, false otherwise.
   */
  public boolean add(GenericEntity entity, String action) {
    if ((action == null) || (action.equalsIgnoreCase(update))) {
      if (update_ == null)
        update_ = new Vector();
      update_.add(entity);
    }
    else if (action.equalsIgnoreCase(insert)) {
      if (insert_ == null)
        insert_ = new Vector();
      insert_.add(entity);
    }
    else
      return(false);

    return(true);
  }

  /**
   * Executed the updates/inserts on the lists.
   */
  public void execute() {
    Connection c = null;
    if ((update_ == null) && (insert_ == null))
      return;
    try {
      c = ConnectionBroker.getConnection();
      if (update_ != null) {
        Iterator i = update_.iterator();
        while (i.hasNext()) {
          GenericEntity e = (GenericEntity)i.next();
          e.update(c);
        }
      }

      if (insert_ != null) {
        Iterator i = insert_.iterator();
        while (i.hasNext()) {
          GenericEntity e = (GenericEntity)i.next();
          e.insert(c);
        }
      }

      c.commit();
      ConnectionBroker.freeConnection(c);
      update_.clear();
      insert_.clear();
    }
    catch(SQLException e) {
      try {
        c.rollback();
      }
      catch(Exception ex) {}
      e.printStackTrace();
    }
  }
}