/**
 *
 */
package com.idega.user.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.idega.user.data.User;
import com.idega.util.text.Name;


/**
 *  Last modified: $Date: 2008/05/24 14:12:39 $ by $Author: valdas $
 *
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.2 $
 */
public class UserComparator implements Comparator<User> {

	private Locale iLocale;
	private Collator collator;

	public UserComparator(Locale locale) {
		this.iLocale = locale;
		collator = Collator.getInstance(iLocale);
	}

	@Override
	public int compare(User user1, User user2) {
		Name name1 = new Name(user1.getFirstName(), user1.getMiddleName(), user1.getLastName());
		Name name2 = new Name(user2.getFirstName(), user2.getMiddleName(), user2.getLastName());

		return collator.compare(name1.getName(iLocale), name2.getName(iLocale));
	}

}