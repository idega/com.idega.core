package com.idega.core.accesscontrol.data;

import com.idega.user.data.User;

public interface ICPermission extends com.idega.data.IDOEntity,
        com.idega.data.IDOLegacyEntity {

    public java.lang.String getPermissionString();

    public java.lang.String getContextType();

    public int getGroupID();

    public void initializeAttributes();

    public void setPermissionValue(java.lang.Boolean p0);

    public void setGroupID(java.lang.Integer p0);

    public void setContextValue(java.lang.String p0);

    public void setPermissionValue(boolean p0);

    public void setGroupID(int p0);

    public boolean getPermissionValue();

    public void setContextType(java.lang.String p0);

    public void setPermissionString(java.lang.String p0);

    public java.lang.String getContextValue();

    public void setInitiationDate(java.sql.Timestamp p0);

    public void setPassive();

    public void setPassiveBy(int p0);

    public java.sql.Timestamp getInitiationDate();

    public int getPassiveBy();

    public java.lang.String getStatus();

    public java.sql.Timestamp getTerminationDate();

    public boolean isActive();

    public boolean isPassive();

    public void setActive();

    public void removeBy(User currentUser);

    public void setToInheritToChildren();

    public void setToNOTInheritToChildren();

    public boolean doesInheritToChildren();
}
