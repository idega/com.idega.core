package com.idega.util;







/** Creditcardnumber checker

*	Visa <br>

*	MasterCard <br>

*	American Express <br>

*	Diner's Club <br>

*	Carte Blanche <br>

*	Discover <br>

*	en Route <br>

*	JCB <br>



*

**/



public class cardChecker{



public boolean isValid(String cardnum) throws NumberFormatException{



//try{



	//throws a numberformatexception if not a number

	long cardnumberint = Long.parseLong(cardnum);

	





  if ((cardnum == null) || (cardnum.length() !=16))

    return false;



  int sum = 0; 

  int mul = 1; 

  int l= cardnum.length();

  String digit;

  int tproduct;

  

  for (int i = 0; i < l; i++) {

    digit = cardnum.substring(l-i-1,l-i);

    tproduct = Integer.parseInt(digit ,10)*mul;

    if (tproduct >= 10)

      sum += (tproduct % 10) + 1;

    else

      sum += tproduct;

    if (mul == 1)

      mul++;

    else

      mul--;

  }



  if ((sum % 10) == 0)

    return  true;

  else

     return false;

/*}





catch (NumberFormatException e){

System.out.print("Numberformatexception i cardChecker");

	return "false ’ numerf.";

}

*/



}



}

