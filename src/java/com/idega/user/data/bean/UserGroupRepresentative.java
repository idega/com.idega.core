/**
 *
 */
package com.idega.user.data.bean;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(UserGroupRepresentative.GROUP_TYPE_USER_REPRESENTATIVE)
@Cacheable
public class UserGroupRepresentative extends Group implements Serializable {

	private static final long serialVersionUID = 6368813289727388227L;
	public static final String GROUP_TYPE_USER_REPRESENTATIVE = "ic_user_representative";

}