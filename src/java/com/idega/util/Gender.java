//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/

package com.idega.util;








/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*General Class to use to signify Male or Female

*/

public class Gender{



private char gender;



 	public Gender(char gender){

		this.gender=gender;

	}



 	public Gender(Character gender){

		this.gender=gender.charValue();

	}



 	public Gender(String gender){

		this.gender=gender.charAt(0);

	}



	public void setGender(char gender){

		this.gender=gender;

	}



	public void setGender(Character gender){

		setGender(gender.charValue());

	}







 	public char getGender(){

		return this.gender;

 	}



	public Character getCharGender(){

		return new Character(this.gender);

	}



	public boolean isMale(){

		if((this.gender=='M') || (this.gender =='m')){

			return true;

		}

		else{

			return false;

		}

	}





	public boolean isFemale(){

		if((this.gender=='F') || (this.gender =='f')){

			return true;

		}

		else{

			return false;

		}

	}





	public String toString(){

		return String.valueOf(this.gender);

	}



}

