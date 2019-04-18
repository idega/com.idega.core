package com.idega.util.text;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.LocaleUtil;
import com.idega.util.StringUtil;

/**
 * Title: com.idega.util.text.Name Description: A helper class for splitting up
 * a fullname into first,middle and lastnames. Copyright: Copyright (c) 2002
 * Company:
 *
 * @author Eirikur Hrafnsson
 * @version 1.1
 */

public class Name {

	private String firstName = null;
	private String middleName = null;
	private String lastName = null;
	private String fullName = null;

	public Name() {
	}

	public Name(String first, String middle, String last) {
		if (first != null) {
			this.firstName = first;
		}
		if (middle != null) {
			this.middleName = middle;
		}
		if (last != null) {
			this.lastName = last;
		}
	}

	public Name(String fullName) {
		setName(fullName);
	}

	public String getName() {
		if (this.fullName == null) {
			StringBuffer fullNameBuffer = new StringBuffer();

			this.firstName = (this.firstName == null) ? CoreConstants.EMPTY : this.firstName;
			this.middleName = (this.middleName == null) ? CoreConstants.EMPTY : this.middleName;
			this.lastName = (this.lastName == null) ? CoreConstants.EMPTY : this.lastName;

			fullNameBuffer.append(this.firstName).append(CoreConstants.SPACE).append(this.middleName).append(CoreConstants.SPACE).append(this.lastName);

			this.fullName = fullNameBuffer.toString();
			this.fullName = TextSoap.findAndReplace(this.fullName, "  ", CoreConstants.SPACE);
		}
		return this.fullName;
	}

	public String getName(Locale locale) {
		return getName(locale, false);
	}

	public String getName(Locale locale, boolean commaSeperated) {
		if (this.fullName == null) {
			StringBuffer buffer = new StringBuffer();
			this.firstName = (this.firstName == null) ? CoreConstants.EMPTY : this.firstName;
			this.middleName = (this.middleName == null) ? CoreConstants.EMPTY : this.middleName;
			this.lastName = (this.lastName == null) ? CoreConstants.EMPTY : this.lastName;
			if (locale.equals(LocaleUtil.getIcelandicLocale())) {
				buffer.append(this.firstName).append(CoreConstants.SPACE).append(this.middleName).append(CoreConstants.SPACE).append(this.lastName);
			} else {
				buffer.append(this.lastName);
				if (commaSeperated) {
					buffer.append(",");
				}
				buffer.append(CoreConstants.SPACE).append(this.firstName).append(CoreConstants.SPACE).append(this.middleName);
			}
			return buffer.toString();

		}
		return getName();
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName != null ? middleName.trim() : middleName;
		this.fullName = null;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName != null ? firstName.trim() : firstName;
		this.fullName = null;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName != null ? lastName.trim() : lastName;
		this.fullName = null;
	}

	private String getCapitalized(String input) {
		if (StringUtil.isEmpty(input) || input.length() < 2) {
			return input;
		}

		return input.substring(0, 1).toUpperCase().concat(input.substring(1));
	}

	public void setName(String name) {
		if ((name != null) && (name.length() > 0)) {
			this.fullName = name;
			StringTokenizer token = new StringTokenizer(name);
			int countWithoutFirstAndLast = token.countTokens() - 2;

			if (token.hasMoreTokens()) {
				this.firstName = token.nextToken();
				if (!StringUtil.isEmpty(firstName)) {
					firstName = getCapitalized(firstName);
				}

				if (countWithoutFirstAndLast >= 1) {
					StringBuffer middleName = new StringBuffer();

					for (int i = 0; i < countWithoutFirstAndLast; i++) {
						middleName.append(token.nextToken());

						if (i != (countWithoutFirstAndLast - 1)) {
							middleName.append(CoreConstants.SPACE);
						}

					}

					this.middleName = middleName.toString();
					this.middleName = getCapitalized(this.middleName);
				} else { // set middle name == null
					this.middleName = null;
				}

				if (countWithoutFirstAndLast >= 0) {
					this.lastName = token.nextToken();
					lastName = getCapitalized(lastName);
				} else { // remove last name
					this.lastName = null;
				}

				this.middleName = getCorrected(this.middleName);
				this.lastName = getCorrected(this.lastName);
			} else {
				System.out.println("com.idega.util.text.Name fullname is an empty string!");
			}
		}
	}

	private String getCorrected(String namePart) {
		if (StringUtil.isEmpty(namePart)) {
			return namePart;
		}

		if (namePart.indexOf(CoreConstants.DOT) == -1) {
			return namePart;
		}

		String[] parts = namePart.split("\\.");
		if (ArrayUtil.isEmpty(parts)) {
			return namePart;
		}

		StringBuilder namePartCorrected = new StringBuilder();
		boolean space = false;
		for (Iterator<String> partsIter = Arrays.asList(parts).iterator(); partsIter.hasNext();) {
			String part = partsIter.next();
			space = part.startsWith(CoreConstants.SPACE);
			if (space) {
				part = part.trim();
			}
			part = TextSoap.capitalize(part);
			if (space) {
				namePartCorrected.append(CoreConstants.SPACE);
			}
			namePartCorrected.append(part);
			if (partsIter.hasNext() || parts.length == 1) {
				namePartCorrected.append(CoreConstants.DOT);
			}
		}
		return namePartCorrected.toString();
	}

	/**
	 * Capitalizes every part of a Name
	 *
	 * @return
	 */
	public Name capitalize() {
		if (this.firstName != null) {
			this.firstName = TextSoap.capitalize(this.firstName);
		}
		if (this.lastName != null) {
			this.lastName = getCorrected(TextSoap.capitalize(this.lastName));
		}
		if (this.middleName != null) {
			this.middleName = getCorrected(TextSoap.capitalize(this.middleName, CoreConstants.SPACE));
		}
		this.fullName = getName();
		return this;
	}

	public static void main(String[] arguments) {
		Name name = new Name("George L. Henry");
		System.out.println(name.getFirstName() + CoreConstants.SPACE + name.getMiddleName() + CoreConstants.SPACE + name.getLastName());
	}
}