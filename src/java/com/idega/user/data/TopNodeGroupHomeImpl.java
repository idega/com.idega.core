/*
 * Created on 13.8.2004
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 */
package com.idega.user.data;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOFactory;
import com.idega.data.IDORelationshipException;

/**
 * @author aron
 *
 * TopNodeGroupHomeImpl TODO Describe this type
 */
public class TopNodeGroupHomeImpl extends IDOFactory implements
        TopNodeGroupHome {
    protected Class getEntityInterfaceClass() {
        return TopNodeGroup.class;
    }

    public TopNodeGroup create() throws javax.ejb.CreateException {
        return (TopNodeGroup) super.createIDO();
    }

    public TopNodeGroup findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException {
        return (TopNodeGroup) super.findByPrimaryKeyIDO(pk);
    }

    public TopNodeGroup findByPrimaryKey(TopNodeGroupKey primaryKey)
            throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Object pk = ((TopNodeGroupBMPBean) entity)
                .ejbFindByPrimaryKey(primaryKey);
        this.idoCheckInPooledEntity(entity);
        return this.findByPrimaryKey(pk);
    }

    public TopNodeGroup create(TopNodeGroupKey primaryKey)
            throws CreateException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Object pk = ((TopNodeGroupBMPBean) entity).ejbCreate(primaryKey);
        ((TopNodeGroupBMPBean) entity).ejbPostCreate();
        this.idoCheckInPooledEntity(entity);
        try {
            return this.findByPrimaryKey(pk);
        } catch (javax.ejb.FinderException fe) {
            throw new com.idega.data.IDOCreateException(fe);
        } catch (Exception e) {
            throw new com.idega.data.IDOCreateException(e);
        }
    }

    public TopNodeGroup create(Integer userID, Integer groupID)
            throws CreateException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Object pk = ((TopNodeGroupBMPBean) entity).ejbCreate(userID, groupID);
        ((TopNodeGroupBMPBean) entity).ejbPostCreate();
        this.idoCheckInPooledEntity(entity);
        try {
            return this.findByPrimaryKey(pk);
        } catch (javax.ejb.FinderException fe) {
            throw new com.idega.data.IDOCreateException(fe);
        } catch (Exception e) {
            throw new com.idega.data.IDOCreateException(e);
        }
    }

    public Collection findByUser(Integer userID) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((TopNodeGroupBMPBean) entity)
                .ejbFindByUser(userID);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    public Collection findByUser(User user) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((TopNodeGroupBMPBean) entity)
                .ejbFindByUser(user);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    public Collection findByGroup(Integer groupID) throws FinderException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        java.util.Collection ids = ((TopNodeGroupBMPBean) entity)
                .ejbFindByGroup(groupID);
        this.idoCheckInPooledEntity(entity);
        return this.getEntityCollectionForPrimaryKeys(ids);
    }

    public Collection getTopNodeGroups(User user)
            throws IDORelationshipException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        Collection theReturn = ((TopNodeGroupBMPBean) entity)
                .ejbHomegetTopNodeGroups(user);
        this.idoCheckInPooledEntity(entity);
        return theReturn;
    }

}
