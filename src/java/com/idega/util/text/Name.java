package com.idega.util.text;

import java.util.*;

/**
 * Title: com.idega.util.text.Name
 * Description: A helper class for splitting up a fullname into first,middle and lastnames.
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author  Eirikur Hrafnsson
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
        if(first != null)
            firstName = first;
        if(middle != null)
            middleName = middle;
        if(last != null)
            lastName = last;
    }

    public Name(String fullName) {
        setName(fullName);
    }

    public String getName() {
        return this.fullName;
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
      this.middleName = middleName;
    }

    public void setFirstName(String firstName) {
    	this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setName(String name) {
		if ((name != null) && (name.length() > 0)) {
			StringTokenizer token = new StringTokenizer(name);
			int countWithoutFirstAndLast = token.countTokens() - 2;

			firstName = (((String) token.nextElement()));

			if (countWithoutFirstAndLast >= 1) {
				StringBuffer middleName = new StringBuffer();

				for (int i = 0; i < countWithoutFirstAndLast; i++) {
					middleName.append((String) token.nextElement());

					if (i != (countWithoutFirstAndLast - 1))
						middleName.append(" ");

				}

				this.middleName = middleName.toString());
			}
			else { //set middle name == null
				this.middleName = null;
			}

			if (countWithoutFirstAndLast >= 0) {
				lastName = (String) token.nextElement();
			}
			else { //remove last name
				this.lastName = null;
			}
		}
	}
    }

  }
