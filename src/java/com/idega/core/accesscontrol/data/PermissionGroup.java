package com.idega.core.accesscontrol.data;

import com.idega.core.data.GenericGroup;

public interface PermissionGroup extends com.idega.user.data.Group,GenericGroup
{
 public java.lang.String getGroupTypeValue();
}
