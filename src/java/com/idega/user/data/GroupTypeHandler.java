package com.idega.user.data;


import com.idega.business.IBOService;
import com.idega.presentation.Image;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public interface GroupTypeHandler extends IBOService  {

  public GroupInfo getGroupInformationClass(Group group);
  public Class getGroupClassType();
  public Group getGroupInformationClassByPrimaryKey(Integer groupID);
  public Image getGroupTypeIcon();

}