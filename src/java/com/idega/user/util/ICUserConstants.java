package com.idega.user.util;

import com.idega.repository.data.ConstantsPlaceholder;

/**
 * A collection of the static variables used in the user system like group types and default metadata.
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 *
 */
public class ICUserConstants implements ConstantsPlaceholder {

	public static final String META_DATA_GROUP_NUMBER = "GROUP_NUMBER";
	public static final String GROUP_TYPE_ALIAS = "alias";
	public static final String GROUP_TYPE_GENERAL = "general";
	public static final String REQUEST_PARAMETER_SELECTED_GROUP_ID = "r_sel_gr_id";
	// do not set this variable to final
	// the help bundle identfier might be changed by bundle starters
	public static String HELP_BUNDLE_IDENTFIER = "com.idega.core";
}
