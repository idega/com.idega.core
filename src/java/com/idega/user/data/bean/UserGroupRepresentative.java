/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;

@Entity
@DiscriminatorValue(UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE)
@Cacheable
public class UserGroupRepresentative extends Group implements Serializable {

	private static final long serialVersionUID = 6368813289727388227L;
	public static final String GROUP_TYPE_USER_REPRESENTATIVE = "ic_user_representative";
	
	
	@OneToOne(
			fetch = FetchType.EAGER, 
			mappedBy = "group", 
			cascade = CascadeType.ALL
	)
    private User user;


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
		user.setGroup(this);
	}
	
	@PostLoad
	private void setThisToUser() {
		if(user == null) {
			return;
		}
		this.user.setGroup(this);
	}

}