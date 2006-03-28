/*
 * $Id: ICObjectInstanceHome.java,v 1.3 2006/03/28 10:20:10 tryggvil Exp $
 * Created on 14.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.component.data;


import javax.ejb.FinderException;
import com.idega.data.IDOHome;
import com.idega.data.IDORemoveRelationshipException;

/**
 * 
 *  Last modified: $Date: 2006/03/28 10:20:10 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:aron@idega.com">aron</a>
 * @version $Revision: 1.3 $
 */
public interface ICObjectInstanceHome extends IDOHome {
    public ICObjectInstance create() throws javax.ejb.CreateException;
    public ICObjectInstance createLegacy();
    public ICObjectInstance findByPrimaryKey(Object pk)
            throws javax.ejb.FinderException;

    public ICObjectInstance findByPrimaryKey(int id)
            throws javax.ejb.FinderException;

    public ICObjectInstance findByPrimaryKeyLegacy(int id)
            throws java.sql.SQLException;

    /**
     * @see com.idega.core.component.data.ICObjectInstanceBMPBean#ejbHomeRemoveRelation
     */
    public void removeRelation(ICObjectInstance instance, Class relatedEntity)
            throws IDORemoveRelationshipException;

    public ICObjectInstance findByUniqueId(String uuid) throws FinderException;
}
