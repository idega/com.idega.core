package com.idega.presentation.ui;

import java.io.Serializable;

import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.4 $
 *
 * Last modified: $Date: 2007/12/30 15:26:03 $ by $Author: civilis $
 *
 */
public class CreditCardNumber implements Serializable {

	private static final long serialVersionUID = 8178727736529158643L;
	private String number1;
	private String number2;
	private String number3;
	private String number4;
	
	public CreditCardNumber() {
	}
	
	public CreditCardNumber(String fullNumber, String separator) {
		setFullNumber(fullNumber, separator);
	}
	
	public CreditCardNumber(String number1, String number2, String number3, String number4) {
		setNumber1(number1);
		setNumber2(number2);
		setNumber3(number3);
		setNumber4(number4);
	}
	
	public String getNumber1() {
		return number1;
	}
	public void setNumber1(String number1) {
		
		if(number1 != null)
			this.number1 = number1;
	}
	public String getNumber2() {
		return number2;
	}
	public void setNumber2(String number2) {
		
		if(number2 != null)
			this.number2 = number2;
	}
	public String getNumber3() {
		return number3;
	}
	public void setNumber3(String number3) {
		
		if(number3 != null)
			this.number3 = number3;
	}
	public String getNumber4() {
		return number4;
	}
	public void setNumber4(String number4) {
		
		if(number4 != null)
			this.number4 = number4;
	}
	
	public void setFullNumber(String fullNumber, String separator) {

		String[] splitted = fullNumber.split(separator);
		
		if(splitted.length != 4) {
			number1 = null;
			number2 = null;
			number3 = null;
			number4 = null;
		} else {
			number1 = splitted[0];
			number2 = splitted[1];
			number3 = splitted[2];
			number4 = splitted[3];
		}
	}
	
	public String getFullNumber(String separator) {
		
		return new StringBuffer(number1 == null ? CoreConstants.EMPTY : number1).append(separator).append(number2 == null ? CoreConstants.EMPTY : number2).append(separator).append(number3 == null ? CoreConstants.EMPTY : number3).append(separator).append(number4 == null ? CoreConstants.EMPTY : number4).toString();
	}
	
	/**
	 * This is read by validators
	 * 
	 * @Override
	 */
	public String toString() {

		return getFullNumber(CoreConstants.EMPTY);
	}
}