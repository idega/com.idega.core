package com.idega.user.data.bean;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.idega.user.data.GroupTypeConstants;

@Entity
@DiscriminatorValue(GroupTypeConstants.GROUP_TYPE_ALIAS)
@Cacheable
public class GroupAlias extends Group {

	private static final long serialVersionUID = -8246319230635794717L;

}