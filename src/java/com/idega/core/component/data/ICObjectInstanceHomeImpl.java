/*
 * $Id: ICObjectInstanceHomeImpl.java,v 1.3 2006/03/28 10:20:10 tryggvil Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.data;


import javax.ejb.FinderException;
import com.idega.data.IDOFactory;
import com.idega.data.IDORemoveRelationshipException;

/**
 * 
 *  Last modified: $Date: 2006/03/28 10:20:10 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public class ICObjectInstanceHomeImpl extends IDOFactory implements
        ICObjectInstanceHome {
    protected Class getEntityInterfaceClass() {
        return ICObjectInstance.class;
    }

    public ICObjectInstance create() throws javax.ejb.CreateException {
        return (ICObjectInstance) super.createIDO();
    }
    
    public ICObjectInstance createLegacy(){
        try{
    		return create();
    	}
    	catch(javax.ejb.CreateException ce){
    		throw new RuntimeException("CreateException:"+ce.getMessage());
    	}

    }

    public ICObjectInstance findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException {
        return (ICObjectInstance) super.findByPrimaryKeyIDO(pk);
    }
    
    public ICObjectInstance findByPrimaryKey(int id) throws javax.ejb.FinderException{
        return (ICObjectInstance) super.findByPrimaryKeyIDO(id);
       }


       public ICObjectInstance findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
      	try{
      		return findByPrimaryKey(id);
      	}
      	catch(javax.ejb.FinderException fe){
      		throw new java.sql.SQLException(fe.getMessage());
      	}

       }

    public void removeRelation(ICObjectInstance instance, Class relatedEntity)
            throws IDORemoveRelationshipException {
        com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
        ((ICObjectInstanceBMPBean) entity).ejbHomeRemoveRelation(instance, relatedEntity);
        this.idoCheckInPooledEntity(entity);
        
    }

	public ICObjectInstance findByUniqueId(String uuid) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Integer id = ((ICObjectInstanceBMPBean) entity).ejbFindByUniqueId(uuid);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(id);
	}
}
