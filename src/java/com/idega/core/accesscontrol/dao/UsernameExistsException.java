/**
 * 
 */
package com.idega.core.accesscontrol.dao;

import com.idega.user.data.bean.User;

public class UsernameExistsException extends Exception {

  public UsernameExistsException(User user, String username){
    super("Exception creating login for user: " + user.getDisplayName() + " (" + user.getId() + ") with username: " + username);
  }
}