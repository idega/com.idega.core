package com.idega.core.data;

import java.sql.*;
import com.idega.data.CacheableEntity;
import com.idega.util.idegaTimestamp;
import com.idega.core.user.data.User;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.business.InformationFolder;

/**
 *  Description of the Class
 *
 *@author     gummi
 *@created    15. mars 2002
 */
public class ICInformationFolder extends CacheableEntity implements InformationFolder {

    private final static String DELETED_COLUMN = "DELETED";
    private final static String DELETED_BY_COLUMN = "DELETED_BY";
    private final static String DELETED_WHEN_COLUMN = "DELETED_WHEN";

    /**
     *  Description of the Field
     */
    public final static String DELETED = "Y";
    /**
     *  Description of the Field
     */
    public final static String NOT_DELETED = "N";


    /**
     *  Constructor for the ICInformationFolder object
     */
    public ICInformationFolder() {
        super();
    }


    /**
     *  Constructor for the ICInformationFolder object
     *
     *@param  id                Description of the Parameter
     *@exception  SQLException  Description of the Exception
     */
    public ICInformationFolder(int id) throws SQLException {
        super(id);
    }


    /**
     *  Description of the Method
     */
    public void initializeAttributes() {
        addAttribute(getIDColumnName());
        addManyToOneRelationship(getColumnOwnerGroup(), "Owner group", GenericGroup.class);
        addManyToOneRelationship(getColumnLocaleId(), "Locale id", ICLocale.class);
        addAttribute(getColumnName(), "Name", true, true, String.class);
        addAttribute(getColumnDescription(), "Description", true, true, String.class);
        addAttribute(getColumnType(), "Type", true, true, String.class);
        addAttribute(getColumnCreated(), "Created", true, true, java.sql.Timestamp.class);
        addAttribute(getColumnValid(), "Valid", true, true, Boolean.class);
        addAttribute(getColumnDeleted(), "Deleted", true, true, String.class, 1);
        addManyToOneRelationship(getColumnDeletedBy(), "Deleted by", User.class);
        addAttribute(getColumnDeletedWhen(), "Deleted when", true, true, Timestamp.class);
        addManyToOneRelationship(getColumnParentFolderId(), "Parent", ICInformationFolder.class);
        addManyToOneRelationship(getColumnObjectId(), "Object id", ICObject.class);

        addManyToManyRelationShip(com.idega.core.data.ICObjectInstance.class, "IC_OBJ_INST_INFO_FOLDER");
    }


    /**
     *  Gets the entityTableName attribute of the ICInformationFolder class
     *
     *@return    The entityTableName value
     */
    public static String getEntityTableName() {
        return "IC_INFO_FOLDER";
    }


    /**
     *  Gets the columnName attribute of the ICInformationFolder class
     *
     *@return    The columnName value
     */
    public static String getColumnName() {
        return "NAME";
    }


    /**
     *  Gets the columnDescription attribute of the ICInformationFolder class
     *
     *@return    The columnDescription value
     */
    public static String getColumnDescription() {
        return "DESCRIPTION";
    }


    /**
     *  Gets the columnType attribute of the ICInformationFolder class
     *
     *@return    The columnType value
     */
    public static String getColumnType() {
        return "CAT_TYPE";
    }


    /**
     *  Gets the columnCreated attribute of the ICInformationFolder class
     *
     *@return    The columnCreated value
     */
    public static String getColumnCreated() {
        return "CREATED";
    }


    /**
     *  Gets the columnValid attribute of the ICInformationFolder class
     *
     *@return    The columnValid value
     */
    public static String getColumnValid() {
        return "VALID";
    }


    /**
     *  Gets the columnOwnerGroup attribute of the ICInformationFolder class
     *
     *@return    The columnOwnerGroup value
     */
    public static String getColumnOwnerGroup() {
        return "OWNER_GROUP";
    }


    /**
     *  Gets the columnDeleted attribute of the ICInformationFolder class
     *
     *@return    The columnDeleted value
     */
    public static String getColumnDeleted() {
        return (DELETED_COLUMN);
    }


    /**
     *  Gets the columnDeletedBy attribute of the ICInformationFolder class
     *
     *@return    The columnDeletedBy value
     */
    public static String getColumnDeletedBy() {
        return (DELETED_BY_COLUMN);
    }


    /**
     *  Gets the columnDeletedWhen attribute of the ICInformationFolder class
     *
     *@return    The columnDeletedWhen value
     */
    public static String getColumnDeletedWhen() {
        return (DELETED_WHEN_COLUMN);
    }


    /**
     *  Gets the columnObjectId attribute of the ICInformationFolder class
     *
     *@return    The columnObjectId value
     */
    public static String getColumnObjectId() {
        return "IC_OBJECT_ID";
    }


    /**
     *  Gets the columnLocaleId attribute of the ICInformationFolder class
     *
     *@return    The columnLocaleId value
     */
    public static String getColumnLocaleId() {
        return ("IC_LOCALE_ID");
    }


    /**
     *  Gets the columnParentFolderId attribute of the ICInformationFolder class
     *
     *@return    The columnParentFolderId value
     */
    public static String getColumnParentFolderId() {
        return ("PARENT_FOLDER_ID");
    }


    /**
     *  Gets the entityName attribute of the ICInformationFolder object
     *
     *@return    The entityName value
     */
    public String getEntityName() {
        return getEntityTableName();
    }


    /**
     *  Gets the name attribute of the ICInformationFolder object
     *
     *@return    The name value
     */
    public String getName() {
        return getStringColumnValue(getColumnName());
    }


    /**
     *  Sets the name attribute of the ICInformationFolder object
     *
     *@param  name  The new name value
     */
    public void setName(String name) {
        setColumn(getColumnName(), name);
    }


    /**
     *  Gets the description attribute of the ICInformationFolder object
     *
     *@return    The description value
     */
    public String getDescription() {
        return getStringColumnValue(getColumnDescription());
    }


    /**
     *  Sets the description attribute of the ICInformationFolder object
     *
     *@param  description  The new description value
     */
    public void setDescription(String description) {
        setColumn(getColumnDescription(), description);
    }


    /**
     *  Gets the iCObjectId attribute of the ICInformationFolder object
     *
     *@return    The iCObjectId value
     */
    public int getICObjectId() {
        return getIntColumnValue(getColumnObjectId());
    }


    /**
     *  Sets the iCObjectId attribute of the ICInformationFolder object
     *
     *@param  id  The new iCObjectId value
     */
    public void setICObjectId(int id) {
        setColumn(getColumnObjectId(), id);
    }


    /**
     *  Gets the parentId attribute of the ICInformationFolder object
     *
     *@return    The parentId value
     */
    public int getParentId() {
        return getIntColumnValue(getColumnParentFolderId());
    }


    /**
     *  Sets the parentId attribute of the ICInformationFolder object
     *
     *@param  id  The new parentId value
     */
    public void setParentId(int id) {
        setColumn(getColumnParentFolderId(), id);
    }


    /**
     *  Gets the localeId attribute of the ICInformationFolder object
     *
     *@return    The localeId value
     */
    public int getLocaleId() {
        return getIntColumnValue(getColumnLocaleId());
    }


    /**
     *  Sets the localeId attribute of the ICInformationFolder object
     *
     *@param  id  The new localeId value
     */
    public void setLocaleId(int id) {
        setColumn(getColumnLocaleId(), id);
    }


    /**
     *  Gets the valid attribute of the ICInformationFolder object
     *
     *@return    The valid value
     */
    public boolean getValid() {
        return getBooleanColumnValue(getColumnValid());
    }


    /**
     *  Sets the valid attribute of the ICInformationFolder object
     *
     *@param  valid  The new valid value
     */
    public void setValid(boolean valid) {
        setColumn(getColumnValid(), valid);
    }


    /**
     *  Gets the created attribute of the ICInformationFolder object
     *
     *@return    The created value
     */
    public java.sql.Timestamp getCreated() {
        return (java.sql.Timestamp) getColumnValue(getColumnCreated());
    }


    /**
     *  Sets the created attribute of the ICInformationFolder object
     *
     *@param  created  The new created value
     */
    public void setCreated(java.sql.Timestamp created) {
        setColumn(getColumnCreated(), created);
    }


    /**
     *  Gets the type attribute of the ICInformationFolder object
     *
     *@return    The type value
     */
    public String getType() {
        return getStringColumnValue(getColumnType());
    }


    /**
     *  Sets the type attribute of the ICInformationFolder object
     *
     *@param  type  The new type value
     */
    public void setType(String type) {
        setColumn(getColumnType(), type);
    }


    /**
     *  Gets the ownerGroupID attribute of the ICInformationFolder object
     *
     *@return    The ownerGroupID value
     */
    public int getOwnerGroupID() {
        int id = getIntColumnValue(getColumnOwnerGroup());
        return id;
    }


    /**
     *  Sets the ownerGroupID attribute of the ICInformationFolder object
     *
     *@param  id  The new ownerGroupID value
     */
    public void setOwnerGroupID(int id) {
        setColumn(getColumnOwnerGroup(), id);
    }



    /**
     *@return    The deleted value
     */
    public boolean getDeleted() {
        String deleted = getStringColumnValue(getColumnDeleted());

        if ((deleted == null) || (deleted.equals(NOT_DELETED))) {
            return (false);
        } else if (deleted.equals(DELETED)) {
            return (true);
        } else {
            return (false);
        }
    }


    /**
     *@param  deleted  The new deleted value
     */
    public void setDeleted(boolean deleted) {
        if (deleted) {
            setColumn(getColumnDeleted(), DELETED);
            setDeletedWhen(idegaTimestamp.getTimestampRightNow());
//      setDeletedBy(iwc.getUserId());
        } else {
            setColumn(getColumnDeleted(), NOT_DELETED);
//      setDeletedBy(-1);
//      setDeletedWhen(null);
        }
    }


    /**
     *@return    The deletedBy value
     */
    public int getDeletedBy() {
        return (getIntColumnValue(getColumnDeletedBy()));
    }


    /**
     *@param  userID  The new deletedBy value
     */
    private void setDeletedBy(int userID) {
//    if (id == -1)
//      setColumn(getColumnDeletedBy(),(Object)null);
//    else
        setColumn(getColumnDeletedBy(), userID);
    }


    /**
     *@return    The deletedWhen value
     */
    public Timestamp getDeletedWhen() {
        return ((Timestamp) getColumnValue(getColumnDeletedWhen()));
    }


    /**
     *@param  when  The new deletedWhen value
     */
    private void setDeletedWhen(Timestamp when) {
        setColumn(getColumnDeletedWhen(), when);
    }


}
