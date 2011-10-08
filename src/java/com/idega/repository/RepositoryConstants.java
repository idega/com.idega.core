package com.idega.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RepositoryConstants {

public static final String DAV_NAME_SPACE = "DAV:";

	public static final String ROLENAME_USERS = "users";
	public static final String ROLENAME_ROOT = "root";

	public static final String PATH_USERS = "/users";
	public static final String PATH_GROUPS = "/groups";
	public static final String PATH_ROLES = "/roles";
	public static final String PATH_ACTIONS = "/actions";

	public static final String SUBJECT_URI_ALL = "all";
	public static final String SUBJECT_URI_AUTHENTICATED = "authenticated";
	public static final String SUBJECT_URI_OWNER = "owner";
	public static final String SUBJECT_URI_SELF = "self";
	public static final String SUBJECT_URI_UNAUTHENTICATED = "unauthenticated";

	public static List<String> ALL_STANDARD_SUBJECT_URIS = Collections.unmodifiableList(Arrays.asList(
			SUBJECT_URI_ALL,
			SUBJECT_URI_AUTHENTICATED,
			SUBJECT_URI_OWNER,
			SUBJECT_URI_SELF,
			SUBJECT_URI_UNAUTHENTICATED
	));

	public static final String PROPERTYNAME_CATEGORY = "categories",

								REPOSITORY = "repository";
}