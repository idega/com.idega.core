
package com.idega.util;

import com.idega.util.IsCollator;
import java.util.*;
import java.util.Comparator;
import com.idega.data.genericentity.Member;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class GenericMemberComparator implements Comparator {

  public static final int NAME      = 1;
  public static final int SOCIAL    = 2;
  public static final int FIRSTLASTMIDDLE = 3;
  public static final int LASTFIRSTMIDDLE = 4;
  public static final int FIRSTMIDDLELAST = 5;


  private int sortBy;

  public GenericMemberComparator() {
      sortBy = NAME;
  }

  public GenericMemberComparator(int toSortBy) {
      sortBy = toSortBy;
  }

  public void sortBy(int toSortBy) {
      sortBy = toSortBy;
  }

  public int compare(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;
      int result = 0;

      switch (this.sortBy) {
        case NAME     : result = nameSort(o1, o2);
        break;

        case SOCIAL   : result = p1.getSocialSecurityNumber().compareTo(p2.getSocialSecurityNumber());
                        if(result == 0)
                          result = nameSort(o1, o2);
        break;
        case FIRSTLASTMIDDLE   : result = this.nameSort(o1,o2);
        break;
        case LASTFIRSTMIDDLE   : result = this.nameSortLastFirst(o1,o2);
        break;
        case FIRSTMIDDLELAST   : result = this.nameSortFirstMiddleLast(o1,o2);
        break;
      }

      return result;
  }

  private int nameSort(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;

      // check on first name first...

      //int result = p1.getFirstName().compareTo(p2.getFirstName());
      int result = IsCollator.getIsCollator().compare(p1.getFirstName(),p2.getFirstName());

      // if equal, check last name...
      if (result == 0){
          //result = p1.getLastName().compareTo(p2.getLastName());
          result = IsCollator.getIsCollator().compare(p1.getLastName(),p2.getLastName());
      }
      // if equal, check middle name...
      if (result == 0){
          //result = p1.getMiddleName().compareTo(p2.getMiddleName());
          result = IsCollator.getIsCollator().compare(p1.getMiddleName(),p2.getMiddleName());
      }
      return result;
  }

  private int nameSortLastFirst(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;

      // check on first name first...


          //result = p1.getLastName().compareTo(p2.getLastName());
      int result = IsCollator.getIsCollator().compare(p1.getLastName(),p2.getLastName());

      //int result = p1.getFirstName().compareTo(p2.getFirstName());
      if (result == 0){
      result = IsCollator.getIsCollator().compare(p1.getFirstName(),p2.getFirstName());
      }
      // if equal, check middle name...
      if (result == 0){
          //result = p1.getMiddleName().compareTo(p2.getMiddleName());
          result = IsCollator.getIsCollator().compare(p1.getMiddleName(),p2.getMiddleName());
      }

      return result;
  }

  private int nameSortFirstMiddleLast(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;

      // check on first name first...

       //int result = p1.getFirstName().compareTo(p2.getFirstName());
      int result = IsCollator.getIsCollator().compare(p1.getFirstName(),p2.getFirstName());

      // if equal, check middle name...
      if (result == 0){
          //result = p1.getMiddleName().compareTo(p2.getMiddleName());
          result = IsCollator.getIsCollator().compare(p1.getMiddleName(),p2.getMiddleName());
      }
      if (result == 0){
          //result = p1.getLastName().compareTo(p2.getLastName());
          result = IsCollator.getIsCollator().compare(p1.getLastName(),p2.getLastName());
      }


      return result;
  }
  public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

  public Iterator sort(Member[] members, int toSortBy) {
      sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Iterator sort(Member[] members) {
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Member[] sortedArray(Member[] members, int toSortBy) {
      sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          members[i] = (Member) objArr[i];
      }
      return (members);
  }

   public Vector sortedArray(Vector list) {
      Collections.sort(list, this);
      return list;
  }


  public Member[] sortedArray(Member[] members) {
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          members[i] = (Member) objArr[i];
      }
      return (members);
  }

  public Member[] reverseSortedArray(Member[] members, int toSortBy) {
      sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      Collections.reverse(list);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          members[i] = (Member) objArr[i];
      }
      return (members);
  }

}
