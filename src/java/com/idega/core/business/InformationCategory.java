package com.idega.core.business;

import java.sql.Timestamp;

/**
 *  <p>
 *
 *  Title: idegaWeb</p> <p>
 *
 *  Description: </p> <p>
 *
 *  Copyright: Copyright (c) 2001</p> <p>
 *
 *  Company: idega</p>
 *
 *@author     <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 *@created    15. mars 2002
 *@version    1.0
 */

public interface InformationCategory {

    /**
     *  Gets the name attribute of the InformationCategory object
     *
     *@return    The name value
     */
    public String getName();


    /**
     *  Gets the iD attribute of the InformationCategory object
     *
     *@return    The iD value
     */
    public int getID();


    /**
     *  Gets the description attribute of the InformationCategory object
     *
     *@return    The description value
     */
    public String getDescription();


    /**
     *  Gets the valid attribute of the InformationCategory object
     *
     *@return    The valid value
     */
    public boolean getValid();


    /**
     *  Gets the created attribute of the InformationCategory object
     *
     *@return    The created value
     */
    public Timestamp getCreated();


    /**
     *  Gets the type attribute of the InformationCategory object
     *
     *@return    The type value
     */
    public String getType();


    /**
     *  Gets the deleted attribute of the InformationCategory object
     *
     *@return    The deleted value
     */
    public boolean getDeleted();


    /**
     *  Gets the deletedBy attribute of the InformationCategory object
     *
     *@return    The deletedBy value
     */
    public int getDeletedBy();


    /**
     *  Gets the deletedWhen attribute of the InformationCategory object
     *
     *@return    The deletedWhen value
     */
    public Timestamp getDeletedWhen();

}
