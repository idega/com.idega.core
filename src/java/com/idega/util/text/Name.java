package com.idega.util.text;

import java.util.*;

import com.idega.util.LocaleUtil;

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
        if(first != null) {
			this.firstName = first;
		}
        if(middle != null) {
			this.middleName = middle;
		}
        if(last != null) {
			this.lastName = last;
		}
    }

    public Name(String fullName) {
        setName(fullName);
    }

    public String getName() {
	    	if(this.fullName==null){
	    		StringBuffer fullNameBuffer = new StringBuffer();

	    		  this.firstName = (this.firstName==null) ? "" : this.firstName;
	    		  this.middleName = (this.middleName==null) ? "" : this.middleName;
	    		  this.lastName = (this.lastName==null) ? "" : this.lastName;
	    		
	    		 
	    		  fullNameBuffer.append(this.firstName).append(" ").append(this.middleName).append(" ").append(this.lastName);
	    		  
	    		  this.fullName = fullNameBuffer.toString();
	    		  this.fullName = TextSoap.findAndReplace(this.fullName,"  "," ");
	    	}
        return this.fullName;
    }
    
    public String getName(Locale locale) {
    		return getName(locale, false);
    }
    
    public String getName(Locale locale, boolean commaSeperated) {
    		if (this.fullName == null) {
    			StringBuffer buffer = new StringBuffer();
    			 this.firstName = (this.firstName==null) ? "" : this.firstName;
	    		 this.middleName = (this.middleName==null) ? "" : this.middleName;
	    		 this.lastName = (this.lastName==null) ? "" : this.lastName;
    			if (locale.equals(LocaleUtil.getIcelandicLocale())) {     
    				buffer.append(this.firstName).append(" ").append(this.middleName).append(" ").append(this.lastName);
    			}
    			else {
    				buffer.append(this.lastName);
    				if (commaSeperated) {
    					buffer.append(",");
    				}
    				buffer.append(" ").append(this.firstName).append(" ").append(this.middleName);
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
      this.middleName = middleName;
      this.fullName = null;
    }

    public void setFirstName(String firstName) {
    		this.firstName = firstName;
        this.fullName = null;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.fullName = null;
    }

    public void setName(String name) {
		if ((name != null) && (name.length() > 0)) {
		    this.fullName = name;
			StringTokenizer token = new StringTokenizer(name);
			int countWithoutFirstAndLast = token.countTokens() - 2;
			
			if( token.hasMoreTokens() ){
				this.firstName = token.nextToken();
	
				if (countWithoutFirstAndLast >= 1) {
					StringBuffer middleName = new StringBuffer();
	
					for (int i = 0; i < countWithoutFirstAndLast; i++) {
						middleName.append(token.nextToken());
	
						if (i != (countWithoutFirstAndLast - 1)) {
							middleName.append(" ");
						}
	
					}
	
					this.middleName = middleName.toString();
				}
				else { //set middle name == null
					this.middleName = null;
				}
	
				if (countWithoutFirstAndLast >= 0) {
					this.lastName = token.nextToken();
				}
				else { //remove last name
					this.lastName = null;
				}
			}
			else {
				System.out.println("com.idega.util.text.Name fullname is an empty string!");
			}
		}
	}
    
    /**
     * Capitalizes every part of a Name
     * @return 
     */
    public Name capitalize(){
        if(this.firstName!=null) {
			this.firstName = TextSoap.capitalize(this.firstName);
		}
        if(this.lastName!=null) {
			this.lastName = TextSoap.capitalize(this.lastName);
		}
        if(this.middleName!=null) {
			this.middleName = TextSoap.capitalize(this.middleName," ");
		}
        this.fullName = getName();
        return this;
    }
  }
