package com.idega.util;
/**
 * Title:        Validator
 * Description:	 A class to handle basic data validation.
 * Copyright:    Copyright (c) 2002
 * Company:      idega software.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>,<a href="mailto:gummi@idega.is">Gudmundur Saemundsson</a>
 * @version 1.0
 */
public class Validator
{
	private static Validator validator;
	/**
	 * Constructor for Validator.
	 */
	private Validator()
	{
	}
	
	public static Validator getInstance(){
		if(validator==null){
			validator = new Validator();	
		}
		return validator;		
	}
	
	/**
	 * Checks if the string intToParse is a valid int
	 *
	 */
	public boolean isInt(String intToParse){
		if(intToParse!=null){
			try{
				Integer.parseInt(intToParse);
				return true;	
			}
			catch(NumberFormatException e){
				return false;	
			}	
		}
		return false;
	}
	
	/**
	 * Checks if the string floatToParse is a valid float number
	 *
	 */
	public boolean isFloat(String floatToParse){
		if(floatToParse!=null){
			try{
				Float.parseFloat(floatToParse);
				return true;	
			}
			catch(NumberFormatException e){
				return false;	
			}	
		}
		return false;
	}
	/**
	 * Checks if the string emailToParse is a valid email
	 */
	public boolean isEmail(String emailToParse){
		if(emailToParse!=null){
			if(emailToParse.equals("")){
				return false;
			}
			else if(emailToParse.indexOf("@")!=-1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 *Checks if the string emailToParse is null or empty
	 *@return true if the stringToParse is not null or not empty
	 */
	public boolean isStringValid(String stringToParse){
		if(stringToParse!=null){
			if(stringToParse.equals("")){
				return false;
			}
			else{
				return true;
			}
		}
		return false;
	}
}
