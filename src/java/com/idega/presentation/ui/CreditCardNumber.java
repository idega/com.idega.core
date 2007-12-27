package com.idega.presentation.ui;

import java.io.Serializable;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1.2.2 $
 *
 * Last modified: $Date: 2007/12/27 20:32:22 $ by $Author: civilis $
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
		
	}
	
	public String getFullNumber(String separator) {
		return null;
	}
	
	/**
	 * This is read by validators
	 * 
	 * @Override
	 */
	public String toString() {
		
		return new StringBuffer(number1).append(number2).append(number3).append(number4).toString();
	}
}