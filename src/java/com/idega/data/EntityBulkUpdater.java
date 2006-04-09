/*
 * $Id: EntityBulkUpdater.java,v 1.10 2006/04/09 12:13:15 laddi Exp $
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

  private GenericEntity relatedEntity;

  public EntityBulkUpdater() {
  }

  public EntityBulkUpdater(IDOLegacyEntity relatedEntity) {
    this.relatedEntity = (GenericEntity)relatedEntity;
  }
  
	public EntityBulkUpdater(GenericEntity relatedEntity) {
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
      if (this.update_ == null) {
				this.update_ = new Vector();
			}
      this.update_.add(entity);
    }
    else if (action.equalsIgnoreCase(insert)) {
      if (this.insert_ == null) {
				this.insert_ = new Vector();
			}
      this.insert_.add(entity);
    }
    else if (action.equalsIgnoreCase(delete)) {
      if (this.delete_ == null) {
				this.delete_ = new Vector();
			}
      this.delete_.add(entity);
    }
    else if (action.equalsIgnoreCase(addto)) {
      if (this.addto_ == null) {
				this.addto_ = new Vector();
			}
      this.addto_.add(entity);
    }
    else if (action.equalsIgnoreCase(removefrom)) {
      if (this.removefrom_ == null) {
				this.removefrom_ = new Vector();
			}
      this.removefrom_.add(entity);
    }
		else {
			return(false);
		}

    return(true);
  }

   public boolean addAll(Collection entityCollection, String action) {
    if ((action == null) || (action.equalsIgnoreCase(update))) {
      if (this.update_ == null) {
				this.update_ = new Vector();
			}
      this.update_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(insert)) {
      if (this.insert_ == null) {
				this.insert_ = new Vector();
			}
      this.insert_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(delete)) {
      if (this.delete_ == null) {
				this.delete_ = new Vector();
			}
      this.delete_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(addto)) {
      if (this.addto_ == null) {
				this.addto_ = new Vector();
			}
      this.addto_.addAll(entityCollection);
    }
    else if (action.equalsIgnoreCase(removefrom)) {
      if (this.removefrom_ == null) {
				this.removefrom_ = new Vector();
			}
      this.removefrom_.addAll(entityCollection);
    }
		else {
			return(false);
		}

    return(true);
  }

  /**
   * Executed the updates/inserts on the lists.
   */
  public void execute() {
    Connection c = null;
    if ((this.update_ == null) && (this.insert_ == null) && (this.delete_ == null)) {
			return;
		}
    try {
      c = ConnectionBroker.getConnection();
      if( c.getAutoCommit() ){
				c.setAutoCommit(false);
      }

      
      if (this.insert_ != null) {
        Iterator i = this.insert_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          e.insert(c);
          if( this.relatedEntity != null ){
            this.relatedEntity.addTo(e,c);
          }
        }
        this.insert_.clear();
      }

      if (this.update_ != null) {
        Iterator i = this.update_.iterator();
        while (i.hasNext()) {
          GenericEntity e = (GenericEntity)i.next();
          e.update(c);
        }
        this.update_.clear();
      }

      if (this.delete_ != null) {
        Iterator i = this.delete_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          if( this.relatedEntity != null ){
            e.removeFrom((IDOLegacyEntity)this.relatedEntity,c);
          }
          e.delete(c);
        }
        this.delete_.clear();
      }

      if (this.addto_ != null) {
        Iterator i = this.addto_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          if( this.relatedEntity != null ){
            e.addTo((IDOLegacyEntity)this.relatedEntity,c);
          }
        }
        this.addto_.clear();
      }
      if (this.removefrom_ != null) {
        Iterator i = this.removefrom_.iterator();
        while (i.hasNext()) {
          IDOLegacyEntity e = (IDOLegacyEntity)i.next();
          if( this.relatedEntity != null ){
            e.removeFrom((IDOLegacyEntity)this.relatedEntity,c);
          }
        }
        this.removefrom_.clear();
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
     if ( c != null ){
				try {
					c.setAutoCommit(true);
				}
				catch (SQLException e1) {
					e1.printStackTrace();
				}
				ConnectionBroker.freeConnection(c);
     }

    }
  }

  public void setRelatedEntity(IDOLegacyEntity relatedEntity){
   this.relatedEntity = (GenericEntity)relatedEntity;
  }
  
	public void setRelatedEntity(GenericEntity relatedEntity){
	 this.relatedEntity = relatedEntity;
	}

}
