package com.idega.user.util;

import com.idega.repository.data.ConstantsPlaceholder;
import com.idega.user.data.GroupTypeConstants;

/**
 * A collection of the static variables used in the user system like group types and default metadata.
 * @author <a href="mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 *
 */
public class ICUserConstants extends GroupTypeConstants implements ConstantsPlaceholder {

	public static final String META_DATA_GROUP_NUMBER = "GROUP_NUMBER";
	
	//gives you the right to change a users personal id
	public static final String ROLE_KEY_EDIT_PERSONAL_ID = "PINEdit";
	
	// do not set this variable to final
	// the help bundle identfier might be changed by bundle starters
	public static String HELP_BUNDLE_IDENTFIER = "com.idega.core";
}
