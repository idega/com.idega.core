/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.util;

//import idega.*;

//import java.awt.event.*;

import java.util.*;



/**

 * General class for text manipulation

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, Eirikur Hrafnsson

*@version 1.2

*/

public class NumberManipulator{



	/**

	 * Function to change all numbers in a string to characters i.e. 0 to a, 1 to b, 2 to c etc.

	 */

	public static String changeAllNumbersToCharachters(String inputString){



              char[] returnArr=inputString.toCharArray();



              int length = returnArr.length;



              for(int i=0;i<length;i++){



                int number;



                  try{



                    number=Integer.parseInt(String.valueOf(returnArr[i]));



                    switch (number) {

                      case 0:

                        returnArr[i]='a';

                        break;

                      case 1:

                        returnArr[i]='b';

                        break;

                      case 2:

                        returnArr[i]='c';

                        break;

                      case 3:

                        returnArr[i]='d';

                        break;

                      case 4:

                        returnArr[i]='e';

                        break;

                      case 5:

                        returnArr[i]='f';

                        break;

                      case 6:

                        returnArr[i]='g';

                        break;

                      case 7:

                        returnArr[i]='h';

                        break;

                      case 8:

                        returnArr[i]='i';

                        break;

                      case 9:

                        returnArr[i]='j';

                        break;

                    }







                  }

                  catch(NumberFormatException ex){



                  }







              }



              return new String(returnArr);

	}



} // class NumberManipulator

