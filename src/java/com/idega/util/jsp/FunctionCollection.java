package com.idega.util.jsp;



import java.util.Calendar;
import java.util.GregorianCalendar;

import java.sql.*;



public class FunctionCollection{



/**

*

*

*Margt mj�g merkilegt

*

**/



	public int getLengthOfMonth(int mon, int ar){

	    GregorianCalendar calendar = new GregorianCalendar();



	    int dagarman = 31;



		switch (mon) {

			case  0 :

		          dagarman = 31;

		          break;

                        case 1 :

                          dagarman=31;

                          break;

			case 2 :

      			        if (calendar.isLeapYear(ar)) {

                                  dagarman=29;

                                }

                                else {

                                  dagarman=28;

				}

                              break;

                        case 3 :

                          dagarman=31;

                          break;

			case 4 :

	                  dagarman = 30;

			  break;

                        case 5 :

                          dagarman=31;

                          break;

                        case 6 :

                          dagarman=30;

                          break;

                        case 7 :

                          dagarman=31;

                          break;

                        case 8 :

                          dagarman=31;

                          break;

                        case 9 :

                          dagarman=30;

                          break;

                        case 10 :

                          dagarman=31;

                          break;

                        case 11 :

                          dagarman=30;

                          break;

                        case 12 :

                          dagarman=31;

                          break;

                        case 13 :

                          dagarman=31;

                          break;



		}

		return dagarman;

	}



	public String getNameOfMonth(int mon){

	String manudurnafn ="";



		switch (mon) {

			case 0  :

					manudurnafn=("Desember");

				break;

			case 01 :

					manudurnafn=("Jan�ar");

				break;

			case 02 :

					manudurnafn=("Febr�ar");

				break;

			case 03 :

					manudurnafn=("Mars");

				break;

			case 04 :

					manudurnafn=("Apr�l");

				break;

			case 05 :

					manudurnafn=("Ma�");

				break;

			case 06 :

					manudurnafn=("J�n�");

				break;

			case 07 :

					manudurnafn=("J�l�");

				break;

			case 8 :

					manudurnafn=("Agust");

				break;

			case 9 :

					manudurnafn=("September");

				break;

			case 10 :

					manudurnafn=("Okt�ber");

				break;

			case 11 :

					manudurnafn=("N�vember");

				break;

			case 12 :

					manudurnafn=("Desember");

				break;

			case 13 :

					manudurnafn=("Jan�ar");

				break;

		}

		return manudurnafn;

	}



	public String getNameOfDay(int dagur) {

		String nafn="";



		switch (dagur) {

			case 1:

				nafn=("Sunnudagur");

				break;

			case 2:

				nafn=("M�nudagur");

				break;

			case 3:

				nafn=("�ri�judagur");

				break;

			case 4:

				nafn=("Mi�vikudagur");

				break;

			case 5:

				nafn=("Fimmtudagur");

				break;

			case 6:

				nafn=("F�studagur");

				break;

			case 7:

				nafn=("Laugardagur");

				break;

		}

		return nafn;

	}



	public int getDayOfWeek(int ar,int manudur,int dagur) {



		GregorianCalendar calendar = new GregorianCalendar(ar,manudur-1,dagur);



		int vdagur = calendar.get(Calendar.DAY_OF_WEEK);





                return vdagur;

	}





	public boolean getHoliday(int ar, int manudur, int dagur) {



		boolean svara =false;



/*			if ( (getDayOfWeek(ar,manudur,dagur) == 1) || (getDayOfWeek(ar,manudur,dagur) == 7) )  {

				svara = true;

			}*/

			if  (getDayOfWeek(ar,manudur,dagur) == 1) {

				svara = true;

			}





			if (!(svara)) {
				switch (manudur) {

					case 1:

						if (dagur==1) {
							svara=true;
						}

						break;

					case 2:

						break;

					case 3:

						break;

					case 4:

						break;

					case 5:

						if (dagur==1) {
							svara=true;
						}

						break;

					case 6:

						if (dagur==17) {
							svara=true;
						}

						break;

					case 7:

						break;

					case 8:

						break;

					case 9:

						break;

					case 10:

						break;

					case 11:

						break;

					case 12:

						break;



				}
			}



		return svara;

	}



	public String getDateStamp(){



		GregorianCalendar calendar = new GregorianCalendar();



		String stamp;



		int	dayM = calendar.get(Calendar.DAY_OF_MONTH);

		int	monthY = calendar.get(Calendar.MONTH)+1;

		int	year = calendar.get(Calendar.YEAR)+000;



		stamp=dayM+"."+getNameOfMonth(monthY)+" "+year;



	return stamp;



	}





	public String getDateStampRS(){



		GregorianCalendar calendar = new GregorianCalendar();



		String stamp;



		int	dayM = calendar.get(Calendar.DAY_OF_MONTH);

		int	monthY = calendar.get(Calendar.MONTH)+1;

		int	year = calendar.get(Calendar.YEAR)+000;



		stamp=year+"."+monthY+" "+dayM;



	return stamp;



	}



	public int getMonth(){



		GregorianCalendar calendar = new GregorianCalendar();



		int month = calendar.get(Calendar.MONTH)+1;



		return month;

	}



	public int getDay(){



		GregorianCalendar calendar = new GregorianCalendar();



		int day = calendar.get(Calendar.DAY_OF_MONTH);



		return day;

	}



	public int getYear(){



		GregorianCalendar calendar = new GregorianCalendar();



		int year = calendar.get(Calendar.YEAR);



		return year;

	}





	public int getRows(ResultSet RS){



		ResultSet RS_new= RS;

		int teljari=0;

		try {



			while (RS_new.next() ) {

				++teljari;

			}



			RS_new.close();

		}

		catch (SQLException E) {

    	E.printStackTrace();

    	}



	return teljari;

	}











	public String getTimeStamp(){

		//gera fyrir time



		GregorianCalendar calendar = new GregorianCalendar();



		String stamp;



		int	dayM = calendar.get(Calendar.DAY_OF_MONTH);

		int	monthY = calendar.get(Calendar.MONTH)+1;

		int	year = calendar.get(Calendar.YEAR)+000;



		stamp=dayM+"."+getNameOfMonth(monthY)+" "+year;



	return stamp;







	}
}