package com.idega.util;

import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;

/**
 * Title:        Validator
 * Description:	 A class to handle basic data validation.
 * Copyright:    Copyright (c) 2002
 * Company:      idega software.
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class Validator implements Singleton
{
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new Validator();}};
	/**
	 * Constructor for Validator.
	 */
	private Validator()
	{
		// empty
	}
	
	public static Validator getInstance(){
		return (Validator) SingletonRepository.getRepository().getInstance(Validator.class, instantiator);
	}
	
	/**
	 * Checks if the string intToParse is a valid int
	 * @param intToParse the String to check for if it is a valid string for the primitive type int
	 * @return False the string is not convertable to int or the String intToParse is null. True otherwise
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
	 * Checks if the string longToParse is a valid long(integer)
	 * @param longToParse the String to check for if it is a valid string for the primitive type long
	 * @return False the string is not convertable to long or the String longToParse is null. True otherwise
	 */
	public boolean isLong(String longToParse){
		if(longToParse!=null){
			try{
				Long.parseLong(longToParse);
				return true;	
			}
			catch(NumberFormatException e){
				return false;	
			}	
		}
		return false;
	}
	
	/**
	 * Checks if the string numberToParse is a valid whole number (with unlimited range)
	 * @param numberToParse the String to check for if it is a number (sequence of integers)
	 * @return False if any of the characters in the string is not an integer or the String numberToParse is null. True otherwise
	 */
	public boolean isNumber(String numberToParse){
		if(numberToParse!=null){
			try{
				for (int i = 0; i < numberToParse.length(); i++) {
					String substr=numberToParse.substring(i,i+1);
					Integer.parseInt(substr);
				}
				return true;
			}
			catch(NumberFormatException e){
				return false;	
			}	
		}
		return false;
	}
	
	/**
	 * Checks if the string numberToParse is a valid whole number and is of character length equal or less than maxLength
	 * @param numberToParse the String to check for if it is a number (sequence of integers)
	 * @param maxLength the length of the sequence that the length of numberToParse must be less than or equal to
	 * @return False if any of the characters in the string is not an integer or the String isNumber is null or the length of the string is larger than maxLength. True otherwise
	 */
	public boolean isNumberWithMaxLength(String numberToParse,int maxLength){
		if(numberToParse!=null){
			try{
				int strLength=numberToParse.length();
				if(strLength<=maxLength){
					for (int i = 0; i <strLength; i++) {
						String substr=numberToParse.substring(i,i+1);
						Integer.parseInt(substr);
					}
					return true;
				}
				else{
					return false;	
				}
				
			}
			catch(NumberFormatException e){
				return false;	
			}	
		}
		return false;
	}
	
	/**
	 * Checks if the string floatToParse is a valid float number
	 * @param floatToParse the String to check for if it is a valid string for the primitive type float
	 * @return False the string is not convertable to float or the String floatToParse is null. True otherwise
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
	 * @param emailToParse the String to check for if it is considered a valid email address. Checks if the email contains an "@" character
	 * @return True if the string emailToParse is not null and contains the "@" symbol. False otherwise
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
