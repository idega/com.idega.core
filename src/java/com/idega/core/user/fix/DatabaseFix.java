package com.idega.core.user.fix;

import java.util.List;
import java.util.Iterator;
import com.idega.data.EntityFinder;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.core.data.GenericGroup;


/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class DatabaseFix {

  public DatabaseFix() {
    try {
      this.main();
    }
    catch (Exception ex) {

    }
  }

  public void main() throws Exception {
    List users = EntityFinder.findAll(User.getStaticInstance());

    if(users != null){
      Iterator iter = users.iterator();
      while (iter.hasNext()) {
        User item = (User)iter.next();
        System.err.print("Fixing user: "+item.getID());
        try {
          if(item.getGroupID() < 1){
            UserGroupRepresentative group = new UserGroupRepresentative();
            group.setName(item.getName());
            group.insert();

            item.setGroupID(group.getID());
            item.update();

            List groups = EntityFinder.findRelated(item,GenericGroup.getStaticInstance());

            if(groups != null){
              Iterator iter2 = groups.iterator();
              while (iter2.hasNext()) {
                GenericGroup item2 = (GenericGroup)iter2.next();
                item2.addUser(item);
              }
            }
            System.err.println(" - fixed");
          } else {
            System.err.println(" - not needed, groupid = "+item.getGroupID());
          }
        } catch (Exception ex) {
          System.err.println(" - failed");
          ex.printStackTrace();
        }
      }
      System.err.println("done");
    } else {
      System.err.println("no Users");
    }
  }
}


