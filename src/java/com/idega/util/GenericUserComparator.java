
package com.idega.util;

import java.text.Collator;
import java.util.*;
import java.util.Comparator;
import com.idega.core.user.data.User;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class GenericUserComparator implements Comparator {

  public static final int NAME      = 1;
  public static final int FIRSTLASTMIDDLE = 2;
  public static final int LASTFIRSTMIDDLE = 3;
  public static final int FIRSTMIDDLELAST = 4;


  private int sortBy;
  private Locale locale;
  private Collator collator;

  public GenericUserComparator() {
      this.sortBy = NAME;
  }

  public GenericUserComparator(Locale locale) {
  	this.locale = locale;
  }

  public GenericUserComparator(int toSortBy) {
      this.sortBy = toSortBy;
  }

	public GenericUserComparator(Locale locale, int toSortBy) {
		this.locale = locale;
		this.sortBy = toSortBy;
	}

  public void sortBy(int toSortBy) {
      this.sortBy = toSortBy;
  }

  public int compare(Object o1, Object o2) {
  	if ( this.locale != null ) {
		this.collator = Collator.getInstance(this.locale);
	}
	else {
		this.collator = IsCollator.getIsCollator();
	}
  		
      int result = 0;

      switch (this.sortBy) {
        case NAME: 
	        if ( this.locale != null ) {
		        if ( this.locale.getLanguage().equalsIgnoreCase("is") ) {
			        result = nameSort(o1, o2);
			       	break; 
		        }
	        }
	      	result = nameSortLastFirst(o1,o2);
	        break;
        case FIRSTLASTMIDDLE:
	        result = this.nameSort(o1,o2);
	        break;
        case LASTFIRSTMIDDLE:
					result = this.nameSortLastFirst(o1,o2);
	        break;
        case FIRSTMIDDLELAST:
	        result = this.nameSortFirstMiddleLast(o1,o2);
	        break;
      }

      return result;
  }

  private int nameSort(Object o1, Object o2) {
      User p1 = (User) o1;
      User p2 = (User) o2;

      // check on first name first...

      //int result = p1.getFirstName().compareTo(p2.getFirstName());
      String one = p1.getFirstName()!=null?p1.getFirstName():"";
      String two = p2.getFirstName()!=null?p2.getFirstName():"";
      int result = this.collator.compare(one,two);

      // if equal, check last name...
      if (result == 0){
          one = p1.getLastName()!=null?p1.getLastName():"";
          two = p2.getLastName()!=null?p2.getLastName():"";
          //result = p1.getLastName().compareTo(p2.getLastName());
          result = IsCollator.getIsCollator().compare(one,two);
      }
      // if equal, check middle name...
      if (result == 0){
          one = p1.getMiddleName()!=null?p1.getMiddleName():"";
          two = p2.getMiddleName()!=null?p2.getMiddleName():"";
          //result = p1.getMiddleName().compareTo(p2.getMiddleName());
          result = this.collator.compare(one,two);
      }
      return result;
  }

  private int nameSortLastFirst(Object o1, Object o2) {
      User p1 = (User) o1;
      User p2 = (User) o2;

      // check on first name first...


          //result = p1.getLastName().compareTo(p2.getLastName());
      String one = p1.getLastName()!=null?p1.getLastName():"";
      String two = p2.getLastName()!=null?p2.getLastName():"";
      int result = IsCollator.getIsCollator().compare(one,two);

      //int result = p1.getFirstName().compareTo(p2.getFirstName());
      if (result == 0){
        one = p1.getFirstName()!=null?p1.getFirstName():"";
        two = p2.getFirstName()!=null?p2.getFirstName():"";
        result = IsCollator.getIsCollator().compare(one,two);
      }
      // if equal, check middle name...
      if (result == 0){
        //result = p1.getMiddleName().compareTo(p2.getMiddleName());
        one = p1.getMiddleName()!=null?p1.getMiddleName():"";
        two = p2.getMiddleName()!=null?p2.getMiddleName():"";
        result = this.collator.compare(one,two);
      }

      return result;
  }

  private int nameSortFirstMiddleLast(Object o1, Object o2) {
      User p1 = (User) o1;
      User p2 = (User) o2;

      // check on first name first...

      String one = p1.getFirstName()!=null?p1.getFirstName():"";
      String two = p2.getFirstName()!=null?p2.getFirstName():"";
      int result = IsCollator.getIsCollator().compare(one,two);
      // if equal, check middle name...
      if (result == 0){
        one = p1.getMiddleName()!=null?p1.getMiddleName():"";
        two = p2.getMiddleName()!=null?p2.getMiddleName():"";
        result = IsCollator.getIsCollator().compare(one,two);
      }
      if (result == 0){
        one = p1.getLastName()!=null?p1.getLastName():"";
        two = p2.getLastName()!=null?p2.getLastName():"";
        result = this.collator.compare(one,two);
      }
      return result;
  }
  public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

  public Iterator sort(User[] users, int toSortBy) {
      this.sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < users.length; i++) {
          list.add(users[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Iterator sort(User[] users) {
      List list = new LinkedList();
      for(int i = 0; i < users.length; i++) {
          list.add(users[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public User[] sortedArray(User[] users, int toSortBy) {
      this.sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < users.length; i++) {
          list.add(users[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          users[i] = (User) objArr[i];
      }
      return (users);
  }

   public Vector sortedArray(Vector list) {
      Collections.sort(list, this);
      return list;
  }


  public User[] sortedArray(User[] users) {
      List list = new LinkedList();
      for(int i = 0; i < users.length; i++) {
          list.add(users[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          users[i] = (User) objArr[i];
      }
      return (users);
  }

  public User[] reverseSortedArray(User[] users, int toSortBy) {
      this.sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < users.length; i++) {
          list.add(users[i]);
      }
      Collections.sort(list, this);
      Collections.reverse(list);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          users[i] = (User) objArr[i];
      }
      return (users);
  }

}
