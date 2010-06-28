/*
 * $Id: GeneralGroup.java 1.1 Sep 23, 2009 laddi Exp $
 * Created on Sep 23, 2009
 *
 * Copyright (C) 2009 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.data.bean;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.idega.user.data.bean.Group;

@Entity
@DiscriminatorValue(GeneralGroup.GROUP_TYPE_GENERAL)
public class GeneralGroup extends Group implements Serializable {

	private static final long serialVersionUID = -6774474296932614241L;
	public static final String GROUP_TYPE_GENERAL = "general";

}