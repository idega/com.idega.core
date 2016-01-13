/*
 * $Id: StandardRoles.java,v 1.7 2008/11/11 15:59:28 valdas Exp $
 * Created on 2.3.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.accesscontrol.business;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.idega.util.StringUtil;


/**
 * <p>
 * This class holds the keys for the standard idegaWeb system level roles
 * </p>
 * Last modified: $Date: 2008/11/11 15:59:28 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.7 $
 */
public class StandardRoles {

	public static final String ROLE_KEY_ADMIN="admin";
	public static final String ROLE_KEY_BUILDER="builder";
	public static final String ROLE_KEY_DEVELOPER="developer";
	public static final String ROLE_KEY_USERADMIN="userapplication";
	public static final String ROLE_KEY_EDITOR="content_editor";
	public static final String ROLE_KEY_AUTHOR="content_author";

	public static final String ROLE_KEY_ISI_USER = "isi_user";
	public static final String ROLE_KEY_UMFI_USER = "umfi_user";
	public static final String ROLE_KEY_REG_UNION_USER = "reg_union_user";
	public static final String ROLE_KEY_SPORT_UNION_USER = "sport_union_user";

	public static final String ROLE_KEY_COMPANY = "company_role";
	public static final String ROLE_KEY_CUSTOMER = "customer_role";

	public static final String ROLE_KEY_FORM_EDITOR = "form_editor";

	//	Put ALL standard roles here!
	private static final String[] _ALL_STANDARD_ROLES = new String[] {ROLE_KEY_ADMIN, ROLE_KEY_BUILDER, ROLE_KEY_DEVELOPER, ROLE_KEY_USERADMIN, ROLE_KEY_EDITOR,
		ROLE_KEY_AUTHOR, ROLE_KEY_FORM_EDITOR};
	public static final List<String> ALL_STANDARD_ROLES = Collections.unmodifiableList(Arrays.asList(_ALL_STANDARD_ROLES));

	public static final StandardRoleHomePageResolver getRoleEnumerator(String roleKey) {
		if (StringUtil.isEmpty(roleKey)) {
			return null;
		}

		if (ROLE_KEY_ADMIN.equals(roleKey)) {
			return StandardRoleHomePageResolver.ADMIN;
		}
		if (ROLE_KEY_BUILDER.equals(roleKey)) {
			return StandardRoleHomePageResolver.BUILDER;
		}
		if (ROLE_KEY_DEVELOPER.equals(roleKey)) {
			return StandardRoleHomePageResolver.DEVELOPER;
		}
		if (ROLE_KEY_USERADMIN.equals(roleKey)) {
			return StandardRoleHomePageResolver.USER_APPLICATION;
		}
		if (ROLE_KEY_EDITOR.equals(roleKey)) {
			return StandardRoleHomePageResolver.CONTENT_EDITOR;
		}
		if (ROLE_KEY_AUTHOR.equals(roleKey)) {
			return StandardRoleHomePageResolver.CONTENT_AUTHOR;
		}
		if (ROLE_KEY_FORM_EDITOR.equals(roleKey)) {
			return StandardRoleHomePageResolver.FORM_EDITOR;
		}

		return null;
	}
}
