package com.idega.util.text;

import java.util.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author       Ægir
 * @version 1.0
 */

 public class Name {

    private StringBuffer firstName = new StringBuffer("");
    private StringBuffer middleName = new StringBuffer("");
    private StringBuffer lastName = new StringBuffer("");
    private StringBuffer fullName = new StringBuffer("");

    public Name() {
    }

    public Name(String first, String middle, String last) {
        if(first != null)
            firstName.append(first);
        if(middle != null)
            middleName.append(middle);
        if(last != null)
            lastName.append(last);
    }

    public Name(String name) {
        setName(name);
    }

    public static void main(String[] args) {
        Name name = new Name("Ægir Laufdal  travis gravis   Traustason");
        System.out.println(name.getName());
        System.out.println(name.getFirstName());
        System.out.println(name.getMiddleName());
        System.out.println(name.getLastName());
    }

    public String getName() {
        fullName.append(firstName.toString());
        if(middleName.length() != 0) {
            fullName.append(" ");
            fullName.append(middleName.toString());
        }
        fullName.append(" ");
        fullName.append(lastName.toString());
        return fullName.toString();
    }

    public String getFirstName() {
        return this.firstName.toString();
    }

    public String getMiddleName() {
        return this.middleName.toString();
    }

    public String getLastName() {
        return this.lastName.toString();
    }

    public void setMiddleName(String middle) {
        if(middle != null) {
            if(middleName.length() > 0)
                middleName.append(" ");
            middleName.append(middle);
        }
    }

    public void setFirstName(String firstName) {
        if(firstName != null)
            this.firstName.append(firstName);
    }

    public void setLastName(String lastName) {
        if(lastName != null)
            this.lastName.append(lastName);
    }

    public void setName(String name) {
        if(name.length() < 1)
            return;
        StringTokenizer token = new StringTokenizer(name);
        setFirstName(((String)token.nextElement()));
        int count = token.countTokens()-1;
        for(int i = 0; i < count; i++) {
            setMiddleName(((String) token.nextElement()));;
        }
        if(token.countTokens() > 0)
            setLastName(((String)token.nextElement()));
    }

  }
