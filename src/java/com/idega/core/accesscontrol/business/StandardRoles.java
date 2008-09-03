/*
 * $Id: StandardRoles.java,v 1.5 2008/09/03 07:22:24 valdas Exp $
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


/**
 * <p>
 * This class holds the keys for the standard idegaWeb system level roles
 * </p>
 * Last modified: $Date: 2008/09/03 07:22:24 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.5 $
 */
public class StandardRoles {
	
	public static final String ROLE_KEY_ADMIN="admin";
	public static final String ROLE_KEY_BUILDER="builder";
	public static final String ROLE_KEY_DEVELOPER="developer";
	public static final String ROLE_KEY_USERADMIN="userapplication";
	public static final String ROLE_KEY_EDITOR="content_editor";
	public static final String ROLE_KEY_AUTHOR="content_author";
	
	public static final String ROLE_KEY_COMPANY = "company_role";
	
	//	Put ALL standard roles here!
	private static final String[] _ALL_STANDARD_ROLES = new String[] {ROLE_KEY_ADMIN, ROLE_KEY_BUILDER, ROLE_KEY_DEVELOPER, ROLE_KEY_USERADMIN, ROLE_KEY_EDITOR,
		ROLE_KEY_AUTHOR};
	public static final List<String> ALL_STANDARD_ROLES = Collections.unmodifiableList(Arrays.asList(_ALL_STANDARD_ROLES));
	
}
