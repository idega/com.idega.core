/*
 * $Id: EntityBulkUpdater.java,v 1.7 2002/04/06 19:07:43 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;

import com.idega.data.IDOLegacyEntity;
import com.idega.util.database.ConnectionBroker;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 */
public class EntityBulkUpdater {
  private Vector insert_ = null;
  private Vector update_ = null;
  private Vector delete_ = null;
  private Vector addto_ = null;
  private Vector removefrom_ = null;

  public static String insert = "insert";
  public static String update = "update";
  public static String delete = "delete";
  public static String addto = "addto";
  public static String removefrom = "removefrom";

  private IDOLegacyEntity relatedEntity;

  public EntityBulkUpdater() {
  }

  public EntityBulkUpdater(IDOLegacyEntity relatedEntity) {
    this.relatedEntity = relatedEntity;
  }

  /**
   * Adds a IDOLegacyEntity to the list of elememts to be updated/inserted
   * into the database.
   *
   * @param entity The entity to update/insert
   * @param action Should the entity be updated or inserted. If null then insert.
   *
   * @return True if the entity was added to the list, false otherwise.
   */
  public boolean add(IDOLegacyEntity entity, String action) {
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
    else if (action.equalsIgnoreCase(delete)) {
      if (delete_ == null)
        delete_ = new Vector();
      delete_.add(entity);
    }
    else if (action.equalsIgnoreCase(addto)) {
      if (addto_ == null)
        addto_ = new Vector();
      addto_.add(entity);
    }
    else if (action.equalsIgnoreCase(removefrom)) {
      if (removefrom_ == null)
        removefrom_ = new Vector();
      removefrom_.add(entity);
    }
    else
      return(false);

    return(true);
  }

   public boolean addAll(Collection entityCollection, String action) {
    if ((action == null) || (action.equalsIgnoreCase(update))) {
      if (update_ == null)
        update_ = new Vector();
      update_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(insert)) {
      if (insert_ == null)
        insert_ = new Vector();
      insert_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(delete)) {
      if (delete_ == null)
        delete_ = new Vector();
      delete_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(addto)) {
      if (addto_ == null)
        addto_ = new Vector();
      addto_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(removefrom)) {
      if (removefrom_ == null)
        removefrom_ = new Vector();
      removefrom_.addAll(entityCollection);
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
    if ((update_ == null) && (insert_ == null) && (delete_ == null))
      return;
    try {
      c = ConnectionBroker.getConnection();
      if (insert_ != null) {
        Iterator i = insert_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          e.insert(c);
          if( relatedEntity != null ){
            relatedEntity.addTo(e,c);
          }
        }
        insert_.clear();
      }

      if (update_ != null) {
        Iterator i = update_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          e.update(c);
        }
        update_.clear();
      }

      if (delete_ != null) {
        Iterator i = delete_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          if( relatedEntity != null ){
            e.removeFrom(relatedEntity,c);
          }
          e.delete(c);
        }
        delete_.clear();
      }

      if (addto_ != null) {
        Iterator i = addto_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          if( relatedEntity != null ){
            e.addTo(relatedEntity,c);
          }
        }
        addto_.clear();
      }
      if (removefrom_ != null) {
        Iterator i = removefrom_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          if( relatedEntity != null ){
            e.removeFrom(relatedEntity,c);
          }
        }
        removefrom_.clear();
      }

      c.commit();

    }
    catch(SQLException e) {
      try {
        c.rollback();
        System.err.println("EntityBulkUpdater : ROLLBACKED");
      }
      catch(Exception ex) {}
      e.printStackTrace(System.err);
      System.err.println("EntityBulkUpdater : ROLLBACK FAILED");
    }
    finally {
     if ( c != null )
       ConnectionBroker.freeConnection(c);
    }
  }

  public void setRelatedEntity(IDOLegacyEntity relatedEntity){
   this.relatedEntity = relatedEntity;
  }

}
